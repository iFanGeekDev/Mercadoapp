/**
 * MercadoApp — Backend local de desarrollo
 * Puerto: 8080
 * Base URL para emulador Android: http://10.0.2.2:8080/v1
 * Base URL para dispositivo físico: http://<TU_IP_LOCAL>:8080/v1
 */

const express    = require('express');
const bcrypt     = require('bcryptjs');
const jwt        = require('jsonwebtoken');
const cors       = require('cors');

const app    = express();
const PORT   = 8080;
const SECRET = 'mercadoapp_dev_secret_key';

app.use(cors());
app.use(express.json());

// ─────────────────────────────────────────────────────────────────────────────
// IN-MEMORY DATA STORE
// ─────────────────────────────────────────────────────────────────────────────

const users = [
  {
    id:       'user-001',
    name:     'Diego Demo',
    email:    'demo@mercadoapp.dev',
    password: bcrypt.hashSync('demo1234', 8),
    avatar_url: null
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
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────

function generateTokens(userId) {
  const accessToken  = jwt.sign({ sub: userId }, SECRET, { expiresIn: '1h' });
  const refreshToken = jwt.sign({ sub: userId, type: 'refresh' }, SECRET, { expiresIn: '30d' });
  return { access_token: accessToken, refresh_token: refreshToken };
}

function authenticate(req, res, next) {
  const header = req.headers['authorization'];
  if (!header || !header.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Token requerido' });
  }
  try {
    const payload = jwt.verify(header.slice(7), SECRET);
    req.userId = payload.sub;
    next();
  } catch {
    return res.status(401).json({ error: 'Token inválido o expirado' });
  }
}

function userToDto(user) {
  return { id: user.id, name: user.name, email: user.email, avatar_url: user.avatar_url };
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
  const tokens = generateTokens(user.id);
  console.log(`[LOGIN] ${email} ✓`);
  return res.json(tokens);
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

router.get('/auth/me', authenticate, (req, res) => {
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

// ─────────────────────────────────────────────────────────────────────────────
// MOUNT & START
// ─────────────────────────────────────────────────────────────────────────────

app.use('/v1', router);

app.get('/', (_, res) => res.json({ status: 'MercadoApp Backend running', version: '1.0' }));

app.listen(PORT, '0.0.0.0', () => {
  console.log(`\n🚀 MercadoApp Backend corriendo en http://localhost:${PORT}/v1`);
  console.log(`📱 Emulador Android  → http://10.0.2.2:${PORT}/v1`);
  console.log(`📡 Dispositivo físico → http://<TU_IP_LOCAL>:${PORT}/v1`);
  console.log(`\n🔑 Credenciales demo:`);
  console.log(`   Email:    demo@mercadoapp.dev`);
  console.log(`   Password: demo1234\n`);
});
