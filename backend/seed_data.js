const { Pool } = require('pg');

const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'yapamarket',
  password: 'postgres',
  port: 5432,
});

const products = [
  // SMARTPHONES
  { name: 'iPhone 15 Pro', img: 'https://images.unsplash.com/photo-1696446701796-da61225697cc?w=600', desc: 'Titanio de grado aeroespacial, chip A17 Pro.', is_offer: true, is_new: true, cat: 'Smartphones' },
  { name: 'Samsung Galaxy S23 Ultra', img: 'https://images.unsplash.com/photo-1678911820864-e2c567c655d7?w=600', desc: 'Cámara de 200MP y S-Pen integrado.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'Google Pixel 8 Pro', img: 'https://images.unsplash.com/photo-1696446702183-bc13636730ed?w=600', desc: 'El teléfono de Google más potente con IA avanzada.', is_offer: true, is_new: true, cat: 'Smartphones' },
  { name: 'Xiaomi 14 Ultra', img: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600', desc: 'Lente Leica de una pulgada para fotografía profesional.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'iPhone 14', img: 'https://images.unsplash.com/photo-1663499482523-1c0c1bae4ce1?w=600', desc: 'Gran autonomía y sistema de cámara dual.', is_offer: true, is_new: false, cat: 'Smartphones' },
  { name: 'OnePlus 12', img: 'https://images.unsplash.com/photo-1707246591040-4f5148003117?w=600', desc: 'Smooth Beyond Belief, Snapdragon 8 Gen 3.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'Nothing Phone (2)', img: 'https://images.unsplash.com/photo-1689327021550-9377484399e5?w=600', desc: 'Interfaz Glyph única y diseño transparente.', is_offer: true, is_new: false, cat: 'Smartphones' },
  { name: 'Motorola Edge 40', img: 'https://images.unsplash.com/photo-1610945264443-f257a2a537b0?w=600', desc: 'Diseño ultra delgado y pantalla curva 144Hz.', is_offer: false, is_new: false, cat: 'Smartphones' },
  
  // LAPTOPS
  { name: 'MacBook Air M2', img: 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=600', desc: 'Increíblemente delgado y rápido.', is_offer: true, is_new: true, cat: 'Laptops' },
  { name: 'Dell XPS 13', img: 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=600', desc: 'Pantalla InfinityEdge y acabado premium.', is_offer: false, is_new: false, cat: 'Laptops' },
  { name: 'HP Spectre x360', img: 'https://images.unsplash.com/photo-1544006659-f0b21f04cb1d?w=600', desc: 'Convertible 2 en 1 con pantalla OLED.', is_offer: true, is_new: true, cat: 'Laptops' },
  { name: 'Lenovo ThinkPad X1 Carbon', img: 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600', desc: 'El estándar de oro para productividad empresarial.', is_offer: false, is_new: false, cat: 'Laptops' },
  { name: 'Asus ROG Zephyrus G14', img: 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=600', desc: 'Potencia gaming en formato ultraportátil.', is_offer: true, is_new: true, cat: 'Laptops' },
  
  // TABLETS
  { name: 'iPad Pro M2', img: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600', desc: 'La experiencia definitiva de iPad.', is_offer: false, is_new: true, cat: 'Tablets' },
  { name: 'iPad Air 5', img: 'https://images.unsplash.com/photo-1589739900243-4b52cd9b104e?w=600', desc: 'Ligero, colorido y con chip M1.', is_offer: true, is_new: false, cat: 'Tablets' },
  { name: 'Samsung Galaxy Tab S9', img: 'https://images.unsplash.com/photo-1561154418-523881555754?w=600', desc: 'Pantalla Dynamic AMOLED 2X y resistencia al agua.', is_offer: false, is_new: true, cat: 'Tablets' },
  { name: 'Microsoft Surface Pro 9', img: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600', desc: 'Portabilidad de tablet, potencia de laptop.', is_offer: true, is_new: false, cat: 'Tablets' },
  
  // AUDIO
  { name: 'AirPods Pro 2', img: 'https://images.unsplash.com/photo-1588423770674-f2855ee4735a?w=600', desc: 'Cancelación de ruido activa mejorada.', is_offer: true, is_new: true, cat: 'Audio' },
  { name: 'Sony WH-1000XM5', img: 'https://images.unsplash.com/photo-1546435770-a3e426da471b?w=600', desc: 'Líder en cancelación de ruido en la industria.', is_offer: false, is_new: true, cat: 'Audio' },
  { name: 'Bose QuietComfort Ultra', img: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600', desc: 'Comodidad legendaria y sonido inmersivo.', is_offer: true, is_new: true, cat: 'Audio' },
  { name: 'Beats Studio Pro', img: 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600', desc: 'Sonido potente y diseño icónico.', is_offer: false, is_new: false, cat: 'Audio' },
  { name: 'Marshall Major IV', img: 'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=600', desc: '80 horas de reproducción inalámbrica.', is_offer: true, is_new: false, cat: 'Audio' },
  
  // WATCHES
  { name: 'Apple Watch Series 9', img: 'https://images.unsplash.com/photo-1434493907317-a46b5bc78344?w=600', desc: 'Más potente y ecológico que nunca.', is_offer: false, is_new: true, cat: 'Watches' },
  { name: 'Samsung Galaxy Watch 6', img: 'https://images.unsplash.com/photo-1508685096489-7a689bdca030?w=600', desc: 'Seguimiento de salud avanzado y bisel rotativo.', is_offer: true, is_new: true, cat: 'Watches' },
  { name: 'Garmin Fenix 7', img: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600', desc: 'El reloj multideporte definitivo para exteriores.', is_offer: false, is_new: false, cat: 'Watches' },
  { name: 'Pixel Watch 2', img: 'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600', desc: 'Lo mejor de Google y Fitbit en tu muñeca.', is_offer: true, is_new: true, cat: 'Watches' },

  // MORE SMARTPHONES (to reach 40+)
  { name: 'Huawei P60 Pro', img: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600', desc: 'Estética de perla y cámara XMAGE.', is_offer: false, is_new: false, cat: 'Smartphones' },
  { name: 'Sony Xperia 1 V', img: 'https://images.unsplash.com/photo-1523206489230-c012c64b2b48?w=600', desc: 'Pantalla 4K OLED para creadores de contenido.', is_offer: true, is_new: true, cat: 'Smartphones' },
  { name: 'Zenfone 10', img: 'https://images.unsplash.com/photo-1592890288564-76628a30a657?w=600', desc: 'Potencia insignia en tamaño compacto.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'Realme GT 5', img: 'https://images.unsplash.com/photo-1616348436168-de43ad0db179?w=600', desc: 'Carga ultra rápida de 240W.', is_offer: true, is_new: false, cat: 'Smartphones' },
  { name: 'Honor Magic 6 Pro', img: 'https://images.unsplash.com/photo-1556656793-062ff98782ee?w=600', desc: 'Pantalla ultra resistente y zoom periscópico.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'Nothing Phone (2a)', img: 'https://images.unsplash.com/photo-1678911820864-e2c567c655d7?w=600', desc: 'El diseño de Nothing para todos.', is_offer: true, is_new: true, cat: 'Smartphones' },
  { name: 'Redmi Note 13 Pro', img: 'https://images.unsplash.com/photo-1512499617640-c74ae3a79d37?w=600', desc: 'Cámara de 200MP a precio imbatible.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'Poco F5 Pro', img: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600', desc: 'Potencia bruta para gamers.', is_offer: true, is_new: false, cat: 'Smartphones' },
  { name: 'iPhone 13 Mini', img: 'https://images.unsplash.com/photo-1605236453051-ad20914a2d8b?w=600', desc: 'El último gran teléfono pequeño.', is_offer: false, is_new: false, cat: 'Smartphones' },
  { name: 'Galaxy A54', img: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600', desc: 'El rey de la gama media de Samsung.', is_offer: true, is_new: false, cat: 'Smartphones' },
  { name: 'Vivo X100 Pro', img: 'https://images.unsplash.com/photo-1523206489230-c012c64b2b48?w=600', desc: 'Fotografía retrato llevada al siguiente nivel.', is_offer: false, is_new: true, cat: 'Smartphones' },
  { name: 'Oppo Find X7 Ultra', img: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600', desc: 'Sistema de cámara cuádruple principal.', is_offer: true, is_new: true, cat: 'Smartphones' },
  { name: 'MacBook Pro 14 M3', img: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600', desc: 'Potencia para profesionales creativos.', is_offer: false, is_new: true, cat: 'Laptops' },
  { name: 'Razer Blade 15', img: 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=600', desc: 'Estética minimalista, potencia gaming máxima.', is_offer: true, is_new: false, cat: 'Laptops' },
  { name: 'Surface Laptop 5', img: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600', desc: 'Elegancia y rendimiento en cada detalle.', is_offer: false, is_new: false, cat: 'Laptops' },
  { name: 'Galaxy Buds 2 Pro', img: 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600', desc: 'Sonido Hi-Fi de 24 bits y ajuste cómodo.', is_offer: true, is_new: false, cat: 'Audio' },
];

async function seed() {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    
    // Limpiar tablas para tener datos frescos
    await client.query('DELETE FROM product_variants');
    await client.query('DELETE FROM products');

    for (const p of products) {
      const technical_specs = {
        processor: ['Chip M-Series', 'Snapdragon', 'Tensor'].filter(() => Math.random() > 0.5),
        ram_gb: [8, 16, 32],
        storage_gb: [256, 512, 1024],
        colors: ['Black', 'Silver', 'Blue', 'Gold']
      };

      const checklist = [
        { category: 'Pantalla', item: 'Sin rayaduras', status: 'APPROVED', note: 'Verificado' },
        { category: 'Batería', item: 'Salud > 85%', status: 'APPROVED', note: '89%' },
        { category: 'Cámaras', item: 'Funcional', status: 'APPROVED', note: '' },
      ];

      const productRes = await client.query(
        `INSERT INTO products (name, image_url, short_description, is_offer, is_new_arrival, technical_specs, inspection_checklist) 
         VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id`,
        [p.name, p.img, p.desc, p.is_offer, p.is_new, JSON.stringify(technical_specs), JSON.stringify(checklist)]
      );
      
      const productId = productRes.rows[0].id;

      // Crear 2-3 variantes por producto
      const conditions = ['EXCELLENT', 'NORMAL', 'FAIR'];
      const variantsCount = 2 + Math.floor(Math.random() * 2);
      
      for (let i = 0; i < variantsCount; i++) {
        await client.query(
          `INSERT INTO product_variants (product_id, condition, processor, ram_gb, storage_gb, color, price, stock) 
           VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
          [
            productId, 
            conditions[i % 3], 
            'Generic CPU', 
            8 * (i + 1), 
            128 * (i + 1), 
            '#333333', 
            299 + (Math.random() * 800), 
            5 + Math.floor(Math.random() * 20)
          ]
        );
      }
    }

    await client.query('COMMIT');
    console.log('✅ 40+ productos creados exitosamente en yapamarket');
  } catch (e) {
    await client.query('ROLLBACK');
    console.error('❌ Error en el seeding:', e);
  } finally {
    client.release();
    pool.end();
  }
}

seed();
