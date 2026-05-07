-- YapaMarket Database Schema (PostgreSQL)

-- Extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    role VARCHAR(50) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Productos
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    image_url TEXT,
    short_description TEXT,
    is_offer BOOLEAN DEFAULT FALSE,
    is_new_arrival BOOLEAN DEFAULT FALSE,
    category VARCHAR(50) DEFAULT 'PHONES',
    technical_specs JSONB, -- Estructura: { processor: [], ram_gb: [], storage_gb: [], colors: [] }
    inspection_checklist JSONB, -- Estructura: [{ category, item, status, note }]
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla de Variantes de Productos
CREATE TABLE IF NOT EXISTS product_variants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID REFERENCES products(id) ON DELETE CASCADE,
    condition VARCHAR(50) NOT NULL CHECK (condition IN ('NEW', 'EXCELLENT', 'NORMAL', 'FAIR')),
    processor VARCHAR(100),
    ram_gb INTEGER,
    storage_gb INTEGER,
    color VARCHAR(50), -- Hexadecimal o Nombre
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabla de Órdenes
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'PROCESSING' CHECK (status IN ('PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Tabla de Ítems de Órdenes
CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID,
    product_name VARCHAR(255),
    image_url TEXT,
    condition VARCHAR(50),
    color VARCHAR(50),
    storage_gb INTEGER,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- 6. Tabla de Direcciones
CREATE TABLE IF NOT EXISTS addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    alias VARCHAR(255), -- Ejemplo: 'Casa', 'Trabajo'
    street VARCHAR(255) NOT NULL,
    number VARCHAR(50),
    department_id VARCHAR(50),
    province_id VARCHAR(50),
    district_id VARCHAR(50),
    department_name VARCHAR(100),
    province_name VARCHAR(100),
    district_name VARCHAR(100),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Tabla de Favoritos
CREATE TABLE IF NOT EXISTS favorites (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    product_id UUID REFERENCES products(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id)
);

-- Índices básicos para optimizar búsquedas
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_variants_product_id ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);

-- Insertar Usuario Admin por defecto (Password: admin1234 - hash de ejemplo)
-- Nota: En la implementación real, bcrypt.hash será usado por el servidor.
-- Este insert es referencial.
-- INSERT INTO users (name, email, password, role) VALUES ('Admin Principal', 'admin@YapaMarket.dev', '$2a$08$p1pP5...', 'ADMIN');
