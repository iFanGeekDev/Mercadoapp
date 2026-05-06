const { Pool } = require('pg');

const isProduction = process.env.NODE_ENV === 'production' || process.env.DATABASE_URL;

const pool = new Pool(
  isProduction 
    ? { 
        connectionString: process.env.DATABASE_URL,
        ssl: { rejectUnauthorized: false } // Requerido por la mayoría de hostings (Heroku, Railway, etc)
      }
    : {
        user: 'postgres',
        host: 'localhost',
        database: 'yapamarket',
        password: 'postgres',
        port: 5432,
      }
);

module.exports = {
  query: (text, params) => pool.query(text, params),
  pool
};
