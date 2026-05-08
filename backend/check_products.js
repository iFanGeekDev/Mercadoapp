const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  connectionString: "postgresql://postgres:OHAmrhVGrNDsdLTymQUKxOfnYUCLmhtV@trolley.proxy.rlwy.net:55654/railway",
  ssl: { rejectUnauthorized: false }
});

async function checkProducts() {
  try {
    const res = await pool.query("SELECT id, name, category, technical_specs FROM products WHERE name ILIKE '%Sony%'");
    console.log('--- PRODUCTOS ENCONTRADOS ---');
    res.rows.forEach(p => {
      console.log(`Producto: ${p.name}`);
      console.log(`Categoría: ${p.category}`);
      console.log(`Specs Type: ${typeof p.technical_specs}`);
      console.log(`Specs:`, p.technical_specs);
      console.log('------------------------');
    });
  } catch (err) {
    console.error(err);
  } finally {
    await pool.end();
  }
}

checkProducts();
