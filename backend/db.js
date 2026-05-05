const { Pool } = require('pg');

const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'yapamarket',
  password: 'postgres', // Usando la contraseña que descubrimos
  port: 5432,
});

module.exports = {
  query: (text, params) => pool.query(text, params),
  pool
};
