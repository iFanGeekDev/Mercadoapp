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
const multer     = require('multer');

const app    = express();
const PORT   = 8080;
const SECRET = 'YapaMarket_dev_secret_key';

app.use(cors());
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
const upload = multer({ storage });

const departamentos = require('./departamentos.json');
const provincias = require('./provincias.json');
const distritos = require('./distritos.json');

// ─────────────────────────────────────────────────────────────────────────────
// IN-MEMORY DATA STORE
// ─────────────────────────────────────────────────────────────────────────────

const users = [
  {
    id:       'user-001',
    name:     'Diego Demo',
    email:    'demo@YapaMarket.dev',
    password: bcrypt.hashSync('demo1234', 8),
    avatar_url: null,
    role:     'USER'
  },
  {
    id:       'admin-001',
    name:     'Admin Principal',
    email:    'admin@YapaMarket.dev',
    password: bcrypt.hashSync('admin1234', 8),
    avatar_url: null,
    role:     'ADMIN'
  }
];

const products = [
  {
    id: 'iphone-13',
    name: 'iPhone 13',
    image_url: 'https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=600',
    short_description: 'Pantalla OLED 6.1" con chip A15 Bionic.',
    is_offer: true,
    is_new_arrival: true,
    technical_specs: {
      processor: ['A15 Bionic'],
      ram_gb:    [4],
      storage_gb:[128, 256],
      colors:    ['Midnight', 'Blue', 'Red']
    },
    variants: [
      { condition: 'FAIR',      processor: 'A15 Bionic', ram_gb: 4, storage_gb: 128, color: 'Midnight', price: 349.0, stock: 3 },
      { condition: 'NORMAL',    processor: 'A15 Bionic', ram_gb: 4, storage_gb: 128, color: 'Blue',     price: 419.0, stock: 8 },
      { condition: 'EXCELLENT', processor: 'A15 Bionic', ram_gb: 4, storage_gb: 256, color: 'Red',      price: 519.0, stock: 4 }
    ]
  },
  {
    id: 's22',
    name: 'Samsung S22',
    image_url: 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600',
    short_description: 'Compacto, potente y con excelente cámara.',
    is_offer: false,
    is_new_arrival: true,
    technical_specs: {
      processor: ['Snapdragon 8 Gen 1'],
      ram_gb:    [8],
      storage_gb:[128, 256],
      colors:    ['Black', 'White']
    },
    variants: [
      { condition: 'FAIR',   processor: 'Snapdragon 8 Gen 1', ram_gb: 8, storage_gb: 128, color: 'Black', price: 309.0, stock: 5 },
      { condition: 'NORMAL', processor: 'Snapdragon 8 Gen 1', ram_gb: 8, storage_gb: 256, color: 'White', price: 399.0, stock: 7 }
    ]
  },
  {
    id: 'pixel-7',
    name: 'Google Pixel 7',
    image_url: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600',
    short_description: 'La mejor cámara computacional, chip Tensor G2.',
    is_offer: true,
    is_new_arrival: false,
    technical_specs: {
      processor: ['Google Tensor G2'],
      ram_gb:    [8],
      storage_gb:[128, 256],
      colors:    ['Obsidian', 'Snow', 'Lemongrass']
    },
    variants: [
      { condition: 'NORMAL',    processor: 'Google Tensor G2', ram_gb: 8, storage_gb: 128, color: 'Obsidian',   price: 449.0, stock: 6 },
      { condition: 'EXCELLENT', processor: 'Google Tensor G2', ram_gb: 8, storage_gb: 256, color: 'Snow',       price: 549.0, stock: 2 }
    ]
  },
  {
    id: 'xiaomi-13',
    name: 'Xiaomi 13',
    image_url: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600',
    short_description: 'Leica optics y Snapdragon 8 Gen 2.',
    is_offer: false,
    is_new_arrival: true,
    technical_specs: {
      processor: ['Snapdragon 8 Gen 2'],
      ram_gb:    [8, 12],
      storage_gb:[256],
      colors:    ['Black', 'White', 'Flora Green']
    },
    variants: [
      { condition: 'FAIR',      processor: 'Snapdragon 8 Gen 2', ram_gb: 8,  storage_gb: 256, color: 'Black',       price: 389.0, stock: 4 },
      { condition: 'NORMAL',    processor: 'Snapdragon 8 Gen 2', ram_gb: 8,  storage_gb: 256, color: 'White',       price: 469.0, stock: 3 },
      { condition: 'EXCELLENT', processor: 'Snapdragon 8 Gen 2', ram_gb: 12, storage_gb: 256, color: 'Flora Green', price: 549.0, stock: 1 }
    ]
  },
  {
    id: 'oneplus-11',
    name: 'OnePlus 11',
    image_url: 'https://images.unsplash.com/photo-1582534648-4b4b4b4b4b4b?w=600',
    short_description: 'Carga rápida 100W y pantalla AMOLED 120Hz.',
    is_offer: false,
    is_new_arrival: true,
    technical_specs: {
      processor: ['Snapdragon 8 Gen 2'],
      ram_gb:    [8, 16],
      storage_gb:[128, 256],
      colors:    ['Titan Black', 'Eternal Green']
    },
    variants: [
      { condition: 'FAIR',   processor: 'Snapdragon 8 Gen 2', ram_gb: 8,  storage_gb: 128, color: 'Titan Black',   price: 429.0, stock: 3 },
      { condition: 'NORMAL', processor: 'Snapdragon 8 Gen 2', ram_gb: 16, storage_gb: 256, color: 'Eternal Green', price: 529.0, stock: 5 }
    ]
  }
];

// ─────────────────────────────────────────────────────────────────────────────
// ORDERS IN-MEMORY STORE
// ─────────────────────────────────────────────────────────────────────────────

const orders = [
  {
    id: 'ord-001',
    user_id: 'user-001',
    status: 'DELIVERED',
    created_at: new Date(Date.now() - 1000 * 60 * 60 * 24 * 5).toISOString(), // hace 5 días
    total: 419.0,
    items: [
      {
        product_id:   'iphone-13',
        product_name: 'iPhone 13',
        image_url:    'https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=600',
        condition:    'NORMAL',
        color:        'Blue',
        storage_gb:   128,
        quantity:     1,
        price:        419.0
      }
    ]
  },
  {
    id: 'ord-002',
    user_id: 'user-001',
    status: 'SHIPPED',
    created_at: new Date(Date.now() - 1000 * 60 * 60 * 24 * 1).toISOString(), // hace 1 día
    total: 858.0,
    items: [
      {
        product_id:   's22',
        product_name: 'Samsung S22',
        image_url:    'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600',
        condition:    'FAIR',
        color:        'Black',
        storage_gb:   128,
        quantity:     1,
        price:        309.0
      },
      {
        product_id:   'pixel-7',
        product_name: 'Google Pixel 7',
        image_url:    'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600',
        condition:    'NORMAL',
        color:        'Obsidian',
        storage_gb:   128,
        quantity:     1,
        price:        449.0
      }
    ]
  },
  {
    id: 'ord-003',
    user_id: 'user-001',
    status: 'PROCESSING',
    created_at: new Date(Date.now() - 1000 * 60 * 30).toISOString(), // hace 30 min
    total: 549.0,
    items: [
      {
        product_id:   'xiaomi-13',
        product_name: 'Xiaomi 13',
        image_url:    'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600',
        condition:    'EXCELLENT',
        color:        'Flora Green',
        storage_gb:   256,
        quantity:     1,
        price:        549.0
      }
    ]
  }
];

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

router.post('/auth/login', (req, res) => {
  const { email, password } = req.body;
  const user = users.find(u => u.email === email);
  if (!user || !bcrypt.compareSync(password, user.password)) {
    return res.status(401).json({ error: 'Credenciales inválidas' });
  }
  const tokens = generateTokens(user.id, user.role);
  console.log(`[LOGIN] ${email} (Role: ${user.role}) ✓`);
  return res.json({ ...tokens, user: userToDto(user) });
});

router.post('/auth/register', (req, res) => {
  const { name, email, password } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ error: 'Nombre, email y contraseña son requeridos' });
  }
  if (users.find(u => u.email === email)) {
    return res.status(409).json({ error: 'El email ya está registrado' });
  }
  const newUser = {
    id:         `user-${Date.now()}`,
    name,
    email,
    password:   bcrypt.hashSync(password, 8),
    avatar_url: null
  };
  users.push(newUser);
  const tokens = generateTokens(newUser.id);
  console.log(`[REGISTER] ${email} ✓`);
  return res.status(201).json(tokens);
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

router.get('/auth/me', authenticateToken, (req, res) => {
  const user = users.find(u => u.id === req.userId);
  if (!user) return res.status(404).json({ error: 'Usuario no encontrado' });
  return res.json(userToDto(user));
});

// ── Products ──────────────────────────────────────────────────────────────────

router.get('/products', (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const size = parseInt(req.query.size) || 20;
  const start = (page - 1) * size;
  const items = products.slice(start, start + size);
  const totalPages = Math.ceil(products.length / size);
  return res.json({ items, page, total_pages: totalPages });
});

router.get('/products/:id', (req, res) => {
  const product = products.find(p => p.id === req.params.id);
  if (!product) return res.status(404).json({ error: 'Producto no encontrado' });
  return res.json(product);
});

// ── Orders ────────────────────────────────────────────────────────────────────

/**
 * GET /v1/orders — Devuelve las órdenes del usuario autenticado
 * Soporta ?page=1&size=10
 */
router.get('/orders', authenticateToken, (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const size = parseInt(req.query.size) || 10;
  const userOrders = orders
    .filter(o => o.user_id === req.userId)
    .sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
  const start = (page - 1) * size;
  const items = userOrders.slice(start, start + size);
  const totalPages = Math.ceil(userOrders.length / size);
  console.log(`[ORDERS] user=${req.userId} page=${page} → ${items.length} orders`);
  return res.json({ items, page, total_pages: totalPages, total_count: userOrders.length });
});

/**
 * GET /v1/orders/:id — Detalle de una orden específica
 */
router.get('/orders/:id', authenticateToken, (req, res) => {
  const order = orders.find(o => o.id === req.params.id && o.user_id === req.userId);
  if (!order) return res.status(404).json({ error: 'Orden no encontrada' });
  return res.json(order);
});

/**
 * POST /v1/orders — Crea una nueva orden (se llama al confirmar checkout)
 * Body: { items: [{ product_id, condition, color, storage_gb, quantity, price }], total }
 */
router.post('/orders', authenticateToken, (req, res) => {
  const { items, total } = req.body;
  if (!items || !Array.isArray(items) || items.length === 0) {
    return res.status(400).json({ error: 'Se requieren items para crear una orden' });
  }
  const newOrder = {
    id:         `ord-${Date.now()}`,
    user_id:    req.userId,
    status:     'PROCESSING',
    created_at: new Date().toISOString(),
    total:      total || items.reduce((acc, i) => acc + (i.price * i.quantity), 0),
    items
  };
  orders.push(newOrder);
  console.log(`[ORDER CREATED] id=${newOrder.id} user=${req.userId} total=$${newOrder.total}`);
  return res.status(201).json(newOrder);
});

// ── Product Management (ADMIN ONLY) ──────────────────────────────────────────

router.post('/products', authenticateToken, isAdmin, (req, res) => {
  const product = {
    id: `prod-${Date.now()}`,
    ...req.body
  };
  products.push(product);
  console.log(`[ADMIN] Producto creado: ${product.name}`);
  res.status(201).json(product);
});

router.put('/products/:id', authenticateToken, isAdmin, (req, res) => {
  const { id } = req.params;
  const index = products.findIndex(p => p.id === id);
  if (index === -1) return res.status(404).json({ error: 'Producto no encontrado' });
  
  products[index] = { ...products[index], ...req.body };
  console.log(`[ADMIN] Producto actualizado: ${products[index].name}`);
  res.json(products[index]);
});

router.delete('/products/:id', authenticateToken, isAdmin, (req, res) => {
  const { id } = req.params;
  const index = products.findIndex(p => p.id === id);
  if (index === -1) return res.status(404).json({ error: 'Producto no encontrado' });
  
  const deleted = products.splice(index, 1);
  console.log(`[ADMIN] Producto eliminado: ${deleted[0].name}`);
  res.json({ message: 'Producto eliminado' });
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
