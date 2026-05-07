/**
 * YapaMarket — Backend local de desarrollo
 * Puerto: 8080
 * Base URL para emulador Android: http://10.0.2.2:8080/v1
 * Base URL para dispositivo físico: http://<TU_IP_LOCAL>:8080/v1
 */

const express    = require('express');
const bcrypt     = require('bcryptjs');
const jwt        = require('jsonwebtoken');
const cors       = require('cors');
const path       = require('path');
const fs         = require('fs');
const fsPromises = require('fs').promises;
const multer     = require('multer');

const app    = express();
const PORT   = process.env.PORT || 8080;
const SECRET = process.env.JWT_SECRET || 'YapaMarket_dev_secret_key';

app.use(cors());
app.set('trust proxy', 1);
app.use(express.json());

// Configuración de carpeta estática para imágenes
const UPLOADS_DIR = path.join(__dirname, 'uploads');
if (!fs.existsSync(UPLOADS_DIR)) fs.mkdirSync(UPLOADS_DIR);
app.use('/uploads', express.static(UPLOADS_DIR));

// Configuración de Multer
const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, UPLOADS_DIR),
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, uniqueSuffix + path.extname(file.originalname));
  }
});
const upload = multer({ 
  storage,
  limits: { fileSize: 2 * 1024 * 1024 } // 2MB
});

const db         = require('./db');

const departamentos = require('./departamentos.json');
const provincias = require('./provincias.json');
const distritos = require('./distritos.json');

// ─────────────────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────

function generateTokens(userId, role = 'USER') {
  const accessToken  = jwt.sign({ sub: userId, role }, SECRET, { expiresIn: '1h' });
  const refreshToken = jwt.sign({ sub: userId, type: 'refresh' }, SECRET, { expiresIn: '30d' });
  return { access_token: accessToken, refresh_token: refreshToken };
}

function authenticateToken(req, res, next) {
  const header = req.headers['authorization'];
  if (!header || !header.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Token requerido' });
  }
  try {
    const payload = jwt.verify(header.slice(7), SECRET);
    req.userId = payload.sub;
    req.userRole = payload.role;
    next();
  } catch {
    return res.status(401).json({ error: 'Token inválido o expirado' });
  }
}

function isAdmin(req, res, next) {
  if (req.userRole === 'ADMIN') {
    next();
  } else {
    res.status(403).json({ error: 'Acceso denegado: Se requiere rol de administrador' });
  }
}

function userToDto(user) {
  return { id: user.id, name: user.name, email: user.email, avatar_url: user.avatar_url, role: user.role };
}

// ─────────────────────────────────────────────────────────────────────────────
// ROUTES — /v1
// ─────────────────────────────────────────────────────────────────────────────

const router = express.Router();

// ── Auth ──────────────────────────────────────────────────────────────────────

// ── Auth ──────────────────────────────────────────────────────────────────────

router.post('/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    const result = await db.query('SELECT * FROM users WHERE LOWER(email) = LOWER($1)', [email]);
    const user = result.rows[0];

    if (!user || !bcrypt.compareSync(password, user.password)) {
      return res.status(401).json({ error: 'Credenciales inválidas' });
    }

    const tokens = generateTokens(user.id, user.role);
    console.log(`[LOGIN] ${email} (Role: ${user.role}) ✓`);
    return res.json({ ...tokens, user: userToDto(user) });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/auth/register', async (req, res) => {
  try {
    const { name, email, password } = req.body;
    if (!name || !email || !password) {
      return res.status(400).json({ error: 'Nombre, email y contraseña son requeridos' });
    }

    const exists = await db.query('SELECT id FROM users WHERE LOWER(email) = LOWER($1)', [email]);
    if (exists.rows.length > 0) {
      return res.status(409).json({ error: 'El email ya está registrado' });
    }

    const hashedPassword = bcrypt.hashSync(password, 8);
    const result = await db.query(
      'INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id',
      [name, email, hashedPassword]
    );

    const tokens = generateTokens(result.rows[0].id);
    console.log(`[REGISTER] ${email} ✓`);
    return res.status(201).json(tokens);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/auth/refresh', (req, res) => {
  const { refresh_token } = req.body;
  if (!refresh_token) return res.status(400).json({ error: 'refresh_token requerido' });
  try {
    const payload = jwt.verify(refresh_token, SECRET);
    if (payload.type !== 'refresh') throw new Error();
    const tokens = generateTokens(payload.sub);
    return res.json(tokens);
  } catch {
    return res.status(401).json({ error: 'Refresh token inválido' });
  }
});

router.get('/auth/me', authenticateToken, async (req, res) => {
  try {
    const result = await db.query('SELECT * FROM users WHERE id = $1', [req.userId]);
    const user = result.rows[0];
    if (!user) return res.status(404).json({ error: 'Usuario no encontrado' });
    return res.json(userToDto(user));
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener perfil' });
  }
});

// ── Profile Management ─────────────────────────────────────────────────────────

// Actualizar perfil (nombre, email)
router.put('/users/profile', authenticateToken, async (req, res) => {
  try {
    const { name, email } = req.body;
    if (!name || !email) return res.status(400).json({ error: 'Nombre y email son requeridos' });

    // Verificar si el nuevo email ya está en uso por OTRO usuario
    const emailCheck = await db.query('SELECT id FROM users WHERE LOWER(email) = LOWER($1) AND id != $2', [email, req.userId]);
    if (emailCheck.rows.length > 0) return res.status(409).json({ error: 'El email ya está en uso' });

    const result = await db.query(
      'UPDATE users SET name = $1, email = $2 WHERE id = $3 RETURNING *',
      [name, email, req.userId]
    );
    
    res.json(userToDto(result.rows[0]));
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Error al actualizar perfil' });
  }
});

// Cambiar contraseña
router.put('/users/change-password', authenticateToken, async (req, res) => {
  try {
    const { old_password, new_password } = req.body;
    if (!old_password || !new_password) return res.status(400).json({ error: 'Ambas contraseñas son requeridas' });

    const result = await db.query('SELECT password FROM users WHERE id = $1', [req.userId]);
    const user = result.rows[0];

    if (!bcrypt.compareSync(old_password, user.password)) {
      return res.status(400).json({ error: 'La contraseña actual es incorrecta' });
    }

    const hashed = bcrypt.hashSync(new_password, 8);
    await db.query('UPDATE users SET password = $1 WHERE id = $2', [hashed, req.userId]);
    
    res.json({ message: 'Contraseña actualizada exitosamente' });
  } catch (error) {
    res.status(500).json({ error: 'Error al cambiar contraseña' });
  }
});

// Subir Avatar
router.post('/users/avatar', authenticateToken, upload.single('avatar'), async (req, res) => {
  try {
    if (!req.file) return res.status(400).json({ error: 'No se subió ningún archivo' });

    // Leer el archivo recién subido
    const imageBuffer = await fsPromises.readFile(req.file.path);
    // Convertir a Base64
    const base64Image = imageBuffer.toString('base64');
    const dataUri = `data:${req.file.mimetype};base64,${base64Image}`;
    
    // Guardar el Data URI (texto largo) en la base de datos
    const result = await db.query('UPDATE users SET avatar_url = $1 WHERE id = $2 RETURNING *', [dataUri, req.userId]);
    
    // Eliminar el archivo físico del servidor (ya no lo necesitamos)
    await fsPromises.unlink(req.file.path);
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Usuario no encontrado' });
    }

    res.json(userToDto(result.rows[0]));
  } catch (error) {
    console.error('[AVATAR_ERROR]', error);
    res.status(500).json({ error: error.message || 'Error al procesar avatar' });
  }
});

// ── Products ──────────────────────────────────────────────────────────────────

router.get('/products', async (req, res) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const size = parseInt(req.query.size) || 20;
    const offset = (page - 1) * size;

    // Obtener productos paginados con filtro opcional de categoría
    const category = req.query.category;
    let productsRes;
    let totalRes;

    if (category && category !== 'ALL') {
      productsRes = await db.query(
        'SELECT * FROM products WHERE category = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
        [category, size, offset]
      );
      totalRes = await db.query('SELECT COUNT(*) FROM products WHERE category = $1', [category]);
    } else {
      productsRes = await db.query(
        'SELECT * FROM products ORDER BY created_at DESC LIMIT $1 OFFSET $2',
        [size, offset]
      );
      totalRes = await db.query('SELECT COUNT(*) FROM products');
    }

    // Obtener todas las variantes de estos productos
    const productIds = productsRes.rows.map(p => p.id);
    let variants = [];
    if (productIds.length > 0) {
      const variantsRes = await db.query(
        'SELECT * FROM product_variants WHERE product_id = ANY($1)',
        [productIds]
      );
      variants = variantsRes.rows;
    }

    // Mapear variantes a sus productos correspondientes
    const items = productsRes.rows.map(p => ({
      ...p,
      variants: variants.filter(v => v.product_id === p.id)
    }));

    // const totalRes = await db.query('SELECT COUNT(*) FROM products'); // Already handled above
    const totalPages = Math.ceil(parseInt(totalRes.rows[0].count) / size);

    return res.json({ items, page, total_pages: totalPages });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Error al obtener productos' });
  }
});

router.get('/products/:id', async (req, res) => {
  try {
    const productRes = await db.query('SELECT * FROM products WHERE id = $1', [req.params.id]);
    if (productRes.rows.length === 0) return res.status(404).json({ error: 'Producto no encontrado' });
    
    const variantsRes = await db.query('SELECT * FROM product_variants WHERE product_id = $1', [req.params.id]);
    
    return res.json({ 
      ...productRes.rows[0], 
      variants: variantsRes.rows 
    });
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener detalle del producto' });
  }
});

// ── Orders ────────────────────────────────────────────────────────────────────

router.get('/orders', authenticateToken, async (req, res) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const size = parseInt(req.query.size) || 10;
    const offset = (page - 1) * size;

    const ordersRes = await db.query(
      'SELECT * FROM orders WHERE user_id = $1 ORDER BY created_at DESC LIMIT $2 OFFSET $3',
      [req.userId, size, offset]
    );

    const orderIds = ordersRes.rows.map(o => o.id);
    let items = [];
    if (orderIds.length > 0) {
      const itemsRes = await db.query(
        'SELECT * FROM order_items WHERE order_id = ANY($1)',
        [orderIds]
      );
      items = itemsRes.rows;
    }

    const data = ordersRes.rows.map(o => ({
      ...o,
      items: items.filter(i => i.order_id === o.id)
    }));

    const totalRes = await db.query('SELECT COUNT(*) FROM orders WHERE user_id = $1', [req.userId]);
    const totalCount = parseInt(totalRes.rows[0].count);
    const totalPages = Math.ceil(totalCount / size);

    return res.json({ items: data, page, total_pages: totalPages, total_count: totalCount });
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener órdenes' });
  }
});

router.get('/orders/:id', authenticateToken, async (req, res) => {
  try {
    const orderRes = await db.query(
      'SELECT * FROM orders WHERE id = $1 AND user_id = $2',
      [req.params.id, req.userId]
    );
    if (orderRes.rows.length === 0) return res.status(404).json({ error: 'Orden no encontrada' });
    
    const itemsRes = await db.query('SELECT * FROM order_items WHERE order_id = $1', [req.params.id]);
    
    return res.json({ ...orderRes.rows[0], items: itemsRes.rows });
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener detalle de la orden' });
  }
});

router.post('/orders', authenticateToken, async (req, res) => {
  const client = await db.pool.connect();
  try {
    const { items, total } = req.body;
    if (!items || !Array.isArray(items) || items.length === 0) {
      return res.status(400).json({ error: 'Se requieren items para crear una orden' });
    }

    await client.query('BEGIN');

    const orderTotal = total || items.reduce((acc, i) => acc + (i.price * i.quantity), 0);
    const orderRes = await client.query(
      'INSERT INTO orders (user_id, total) VALUES ($1, $2) RETURNING *',
      [req.userId, orderTotal]
    );
    const orderId = orderRes.rows[0].id;

    for (const item of items) {
      await client.query(
        `INSERT INTO order_items (order_id, product_id, product_name, image_url, condition, color, storage_gb, quantity, price) 
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)`,
        [orderId, item.product_id, item.product_name, item.image_url, item.condition, item.color, item.storage_gb, item.quantity, item.price]
      );
    }

    await client.query('COMMIT');
    console.log(`[ORDER CREATED] id=${orderId} user=${req.userId} total=$${orderTotal}`);
    res.status(201).json(orderRes.rows[0]);
  } catch (error) {
    await client.query('ROLLBACK');
    console.error(error);
    res.status(500).json({ error: 'Error al procesar la orden' });
  } finally {
    client.release();
  }
});

// ── Favorites ─────────────────────────────────────────────────────────────────

router.get('/favorites', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      `SELECT p.* FROM products p 
       JOIN favorites f ON p.id = f.product_id 
       WHERE f.user_id = $1`,
      [req.userId]
    );
    
    // Obtener variantes para estos productos favoritos
    const productIds = result.rows.map(p => p.id);
    let items = result.rows;
    if (productIds.length > 0) {
      const variantsRes = await db.query('SELECT * FROM product_variants WHERE product_id = ANY($1)', [productIds]);
      items = result.rows.map(p => ({
        ...p,
        variants: variantsRes.rows.filter(v => v.product_id === p.id)
      }));
    }

    res.json(items);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener favoritos' });
  }
});

router.post('/favorites', authenticateToken, async (req, res) => {
  try {
    const { product_id } = req.body;
    await db.query(
      'INSERT INTO favorites (user_id, product_id) VALUES ($1, $2) ON CONFLICT DO NOTHING',
      [req.userId, product_id]
    );
    res.status(201).json({ message: 'Añadido a favoritos' });
  } catch (error) {
    res.status(500).json({ error: 'Error al añadir a favoritos' });
  }
});

router.delete('/favorites/:productId', authenticateToken, async (req, res) => {
  try {
    await db.query(
      'DELETE FROM favorites WHERE user_id = $1 AND product_id = $2',
      [req.userId, req.params.productId]
    );
    res.json({ message: 'Eliminado de favoritos' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar de favoritos' });
  }
});

// ── Addresses ─────────────────────────────────────────────────────────────────

router.get('/addresses', authenticateToken, async (req, res) => {
  try {
    const result = await db.query(
      'SELECT * FROM addresses WHERE user_id = $1 ORDER BY is_default DESC, created_at DESC',
      [req.userId]
    );
    res.json(result.rows);
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener direcciones' });
  }
});

router.post('/addresses', authenticateToken, async (req, res) => {
  const client = await db.pool.connect();
  try {
    const { alias, street, number, department_id, province_id, district_id, department_name, province_name, district_name, is_default } = req.body;
    
    await client.query('BEGIN');

    // Si es la primera o se marca como default, desmarcar las otras
    if (is_default) {
      await client.query('UPDATE addresses SET is_default = FALSE WHERE user_id = $1', [req.userId]);
    }

    const result = await client.query(
      `INSERT INTO addresses (user_id, alias, street, number, department_id, province_id, district_id, department_name, province_name, district_name, is_default) 
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) RETURNING *`,
      [req.userId, alias, street, number, department_id, province_id, district_id, department_name, province_name, district_name, is_default]
    );

    await client.query('COMMIT');
    res.status(201).json(result.rows[0]);
  } catch (error) {
    await client.query('ROLLBACK');
    res.status(500).json({ error: 'Error al crear dirección' });
  } finally {
    client.release();
  }
});

router.put('/addresses/:id', authenticateToken, async (req, res) => {
  const client = await db.pool.connect();
  try {
    const { id } = req.params;
    const { alias, street, number, department_id, province_id, district_id, department_name, province_name, district_name, is_default } = req.body;

    await client.query('BEGIN');

    if (is_default) {
      await client.query('UPDATE addresses SET is_default = FALSE WHERE user_id = $1', [req.userId]);
    }

    const result = await client.query(
      `UPDATE addresses SET alias=$1, street=$2, number=$3, department_id=$4, province_id=$5, district_id=$6, 
       department_name=$7, province_name=$8, district_name=$9, is_default=$10 WHERE id=$11 AND user_id=$12 RETURNING *`,
      [alias, street, number, department_id, province_id, district_id, department_name, province_name, district_name, is_default, id, req.userId]
    );

    if (result.rows.length === 0) {
      await client.query('ROLLBACK');
      return res.status(404).json({ error: 'Dirección no encontrada' });
    }

    await client.query('COMMIT');
    res.json(result.rows[0]);
  } catch (error) {
    await client.query('ROLLBACK');
    res.status(500).json({ error: 'Error al actualizar dirección' });
  } finally {
    client.release();
  }
});

router.delete('/addresses/:id', authenticateToken, async (req, res) => {
  try {
    const result = await db.query('DELETE FROM addresses WHERE id = $1 AND user_id = $2 RETURNING *', [req.params.id, req.userId]);
    if (result.rows.length === 0) return res.status(404).json({ error: 'Dirección no encontrada' });
    res.json({ message: 'Dirección eliminada' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar dirección' });
  }
});

// ── Product Management (ADMIN ONLY) ──────────────────────────────────────────

router.post('/products', authenticateToken, isAdmin, async (req, res) => {
  const client = await db.pool.connect();
  try {
    const { 
      name, image_url, short_description, is_offer, is_new_arrival, category,
      technical_specs, inspection_checklist, variants 
    } = req.body;

    await client.query('BEGIN');

    const productRes = await client.query(
      `INSERT INTO products (name, image_url, short_description, is_offer, is_new_arrival, category, technical_specs, inspection_checklist) 
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8) RETURNING *`,
      [name, image_url, short_description, is_offer, is_new_arrival, category || 'PHONES', technical_specs, inspection_checklist]
    );
    const productId = productRes.rows[0].id;

    if (variants && Array.isArray(variants)) {
      for (const v of variants) {
        await client.query(
          `INSERT INTO product_variants (product_id, condition, processor, ram_gb, storage_gb, color, price, stock) 
           VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
          [productId, v.condition, v.processor, v.ram_gb, v.storage_gb, v.color, v.price, v.stock]
        );
      }
    }

    await client.query('COMMIT');
    console.log(`[ADMIN] Producto creado: ${name}`);
    res.status(201).json(productRes.rows[0]);
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('[DB_CREATE_ERROR]', error);
    res.status(500).json({ error: `Error al crear: ${error.message}` });
  } finally {
    client.release();
  }
});

router.put('/products/:id', authenticateToken, isAdmin, async (req, res) => {
  const client = await db.pool.connect();
  try {
    const { id } = req.params;
    const { 
      name, image_url, short_description, is_offer, is_new_arrival, category,
      technical_specs, inspection_checklist, variants 
    } = req.body;

    await client.query('BEGIN');

    const productRes = await client.query(
      `UPDATE products SET name=$1, image_url=$2, short_description=$3, is_offer=$4, is_new_arrival=$5, 
       category=$6, technical_specs=$7, inspection_checklist=$8 WHERE id=$9 RETURNING *`,
      [name, image_url, short_description, is_offer, is_new_arrival, category, technical_specs, inspection_checklist, id]
    );

    if (productRes.rows.length === 0) {
      await client.query('ROLLBACK');
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    // Actualizar variantes: Eliminar y volver a insertar es lo más simple para sincronización completa
    await client.query('DELETE FROM product_variants WHERE product_id = $1', [id]);
    
    if (variants && Array.isArray(variants)) {
      for (const v of variants) {
        await client.query(
          `INSERT INTO product_variants (product_id, condition, processor, ram_gb, storage_gb, color, price, stock) 
           VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
          [id, v.condition, v.processor, v.ram_gb, v.storage_gb, v.color, v.price, v.stock]
        );
      }
    }

    await client.query('COMMIT');
    console.log(`[ADMIN] Producto actualizado: ${name}`);
    res.json(productRes.rows[0]);
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('[DB_UPDATE_ERROR]', error);
    res.status(500).json({ error: `Error al actualizar: ${error.message}` });
  } finally {
    client.release();
  }
});

router.delete('/products/:id', authenticateToken, isAdmin, async (req, res) => {
  try {
    const result = await db.query('DELETE FROM products WHERE id = $1 RETURNING name', [req.params.id]);
    if (result.rows.length === 0) return res.status(404).json({ error: 'Producto no encontrado' });
    
    console.log(`[ADMIN] Producto eliminado: ${result.rows[0].name}`);
    res.json({ message: 'Producto eliminado' });
  } catch (error) {
    res.status(500).json({ error: 'Error al eliminar producto' });
  }
});

router.post('/upload', authenticateToken, isAdmin, upload.single('image'), (req, res) => {
  if (!req.file) return res.status(400).json({ error: 'No se subió ninguna imagen' });
  
  // En producción, esto debería ser la URL completa o relativa al host
  const imageUrl = `http://localhost:8080/uploads/${req.file.filename}`;
  res.json({ image_url: imageUrl });
});

// ── Ubigeo (Perú) ─────────────────────────────────────────────────────────────

router.get('/ubigeo/departments', (req, res) => {
  return res.json(departamentos);
});

router.get('/ubigeo/provinces/:departmentId', (req, res) => {
  return res.json(provincias.filter(p => p.department_id === req.params.departmentId));
});

router.get('/ubigeo/districts/:provinceId', (req, res) => {
  return res.json(distritos.filter(d => d.province_id === req.params.provinceId));
});

// ─────────────────────────────────────────────────────────────────────────────
// MOUNT & START
// ─────────────────────────────────────────────────────────────────────────────

app.use('/v1', router);

app.get('/', (_, res) => res.json({ status: 'YapaMarket Backend running', version: '1.1' }));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`\n🚀 YapaMarket Backend corriendo en http://localhost:${PORT}/v1`);
  console.log(`📱 Emulador Android  → http://10.0.2.2:${PORT}/v1`);
  console.log(`📡 Dispositivo físico → http://<TU_IP_LOCAL>:${PORT}/v1`);
  console.log(`\n🔑 Credenciales demo:`);
  console.log(`   [Usuario] Email: demo@YapaMarket.dev | Pass: demo1234`);
  console.log(`   [Admin]   Email: admin@YapaMarket.dev | Pass: admin1234\n`);
  console.log(`📦 Endpoints disponibles:`);
  console.log(`   GET  /v1/orders          → Lista de órdenes del usuario`);
  console.log(`   GET  /v1/orders/:id      → Detalle de una orden`);
  console.log(`   POST /v1/orders          → Crear orden\n`);
});
