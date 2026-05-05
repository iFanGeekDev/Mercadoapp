const { Pool } = require('pg');
const bcrypt = require('bcryptjs');

const pool = new Pool({
  user: 'postgres', host: 'localhost', database: 'yapamarket', password: 'postgres', port: 5432,
});

async function createAdmins() {
  const admins = [
    { name: 'Admin Principal', email: 'admin@YapaMarket.dev', pass: 'admin1234' },
    { name: 'Diego Admin', email: 'diego@YapaMarket.dev', pass: 'diego1234' }
  ];

  for (const a of admins) {
    const hash = bcrypt.hashSync(a.pass, 8);
    await pool.query(
      'INSERT INTO users (name, email, password, role) VALUES ($1, $2, $3, $4) ON CONFLICT (email) DO UPDATE SET password = $3',
      [a.name, a.email, hash, 'ADMIN']
    );
    console.log(`✅ Admin creado: ${a.email} | Pass: ${a.pass}`);
  }
  pool.end();
}

createAdmins();
