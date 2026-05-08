const { Pool } = require('pg');

const pool = new Pool({
  connectionString: "postgresql://postgres:OHAmrhVGrNDsdLTymQUKxOfnYUCLmhtV@trolley.proxy.rlwy.net:55654/railway",
  ssl: { rejectUnauthorized: false }
});

async function fixData() {
  try {
    const res = await pool.query('SELECT id, name, technical_specs FROM products');
    console.log(`Analizando ${res.rows.length} productos...`);

    for (const p of res.rows) {
      const specs = p.technical_specs || {};
      
      const newSpecs = {
        processor: Array.isArray(specs.processor) ? specs.processor : [String(specs.processor || '')],
        ram_gb: Array.isArray(specs.ram_gb) ? specs.ram_gb.map(v => parseInt(String(v).replace(/\D/g, '')) || 0) : [parseInt(String(specs.ram_gb || 0).replace(/\D/g, '')) || 0],
        storage_gb: Array.isArray(specs.storage_gb) ? specs.storage_gb.map(v => parseInt(String(v).replace(/\D/g, '')) || 0) : [parseInt(String(specs.storage_options?.[0] || specs.storage_gb || 0).replace(/\D/g, '')) || 0],
        colors: Array.isArray(specs.colors) ? specs.colors : [String(specs.colors || 'Standard')]
      };

      await pool.query(
        'UPDATE products SET technical_specs = $1 WHERE id = $2',
        [JSON.stringify(newSpecs), p.id]
      );
      console.log(`✅ Reparado: ${p.name}`);
    }

    console.log('--- MIGRACIÓN COMPLETADA ---');
  } catch (err) {
    console.error('❌ Error en migración:', err);
  } finally {
    await pool.end();
  }
}

fixData();
