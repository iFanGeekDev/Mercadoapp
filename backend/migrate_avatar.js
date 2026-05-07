const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function migrate() {
  const client = await pool.connect();
  try {
    console.log('🚀 Iniciando migración de base de datos...');
    await client.query('ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url TEXT;');
    console.log('✅ Columna avatar_url añadida exitosamente.');
  } catch (err) {
    console.error('❌ Error en la migración:', err);
  } finally {
    client.release();
    pool.end();
  }
}

migrate();
