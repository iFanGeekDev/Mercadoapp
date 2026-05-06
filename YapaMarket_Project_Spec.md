# 📱 YapaMarket: Premium Second-Hand Ecosystem

**Versión:** 1.2 (Mayo 2026)  
**Estado:** MVP Operacional en PostgreSQL  
**Objetivo:** Revolucionar el mercado de dispositivos de segunda mano en Perú mediante transparencia técnica y una experiencia de usuario premium.

---

## 🚀 1. Visión General del Ecosistema
YapaMarket no es solo una tienda, es un ecosistema completo diseñado para escalar. Conecta a usuarios finales con una gestión de inventario profesional y transparente.

- **Frontend Móvil:** Android Nativo (Kotlin/Jetpack Compose).
- **Frontend Administrativo:** Web App (React + Vite + Tailwind CSS).
- **Backend Core:** Node.js + Express API.
- **Base de Datos:** PostgreSQL (Relacional, Robusta y Escalable).

---

## 📱 2. Android App: Experiencia del Cliente
Diseñada bajo los estándares de Google Stitch, ofrece una interfaz fluida y moderna.

### Características Clave:
*   **Autenticación Robusta:** Login y Registro con validación en tiempo real y persistencia de sesión vía JWT.
*   **Catálogo Inteligente:** Navegación por categorías (Smartphones, Laptops, Audio, etc.) con búsqueda optimizada.
*   **Selector de Variantes Avanzado:** Sistema dinámico que permite elegir Color, RAM, Almacenamiento y Condición, validando stock disponible instantáneamente.
*   **Transparencia Técnica:** Visualización del **Checklist de Inspección** para cada equipo (Pantalla, Batería, Cámaras, etc.).
*   **Gestión de Direcciones (Perú):** Integración total con **Ubigeo** (Departamento, Provincia, Distrito) para envíos precisos.
*   **Historial de Pedidos:** Seguimiento detallado de compras pasadas y estados de envío.
*   **Sistema de Favoritos:** Lista de deseos personalizada sincronizada con el servidor para guardar equipos de interés.

---

## 💻 3. Panel Web Admin: Gestión de Negocio
Una herramienta poderosa para que el equipo operativo gestione el inventario y las ventas.

### Características Clave:
*   **Dashboard de Métricas:** Visualización en tiempo real de Total de Equipos, Ventas Diarias, Alertas de Stock Bajo y Tasas de Conversión.
*   **Gestión de Productos (CRUD):** Interfaz premium para crear, editar y eliminar equipos con soporte de múltiples variantes.
*   **Módulo de Inspección Técnica:** Interfaz obligatoria para marcar el estado de los componentes de cada equipo antes de su venta.
*   **Gestor de Imágenes Real:** Sistema de subida física de archivos (`multer`) que garantiza que cada producto tenga fotos reales y de alta calidad.
*   **Seguridad RBAC:** Control de acceso basado en roles (Solo administradores autenticados).

---

## ⚙️ 4. Backend e Infraestructura
El motor que impulsa todo el sistema, optimizado para el rendimiento local y la escalabilidad futura.

### Tech Stack:
*   **Servidor:** Node.js con Express.
*   **Base de Datos:** PostgreSQL (Tablas: `users`, `products`, `product_variants`, `orders`, `order_items`).
*   **Seguridad:** Encriptación de contraseñas con **Bcrypt** y tokens de acceso **JWT**.
*   **Almacenamiento:** Sistema de archivos estáticos para imágenes del catálogo.
*   **Arquitectura:** API RESTful con respuestas en formato JSON consistente.

---

## 📊 5. Modelo de Datos (PostgreSQL)
El diseño relacional garantiza la integridad de los datos y la capacidad de generar reportes complejos.

- **`users`**: Soporta clientes y administradores con perfiles protegidos.
- **`products`**: Almacena información técnica, descripciones y el checklist de calidad en formato **JSONB**.
- **`product_variants`**: Maneja la complejidad de tener un mismo modelo con diferentes colores o capacidades.
- **`orders` / `order_items`**: Transacciones atómicas para registrar ventas y detalles de compra.

---

## 🗺️ 6. Roadmap Próximos Pasos
1.  **Notificaciones Push:** Alertas en tiempo real sobre ofertas y estados de pedidos en la App.
2.  **Integración de Pagos:** Conexión con pasarelas de pago (Niubiz/Culqi).
3.  **Filtros de Búsqueda Avanzados:** Filtrado por precio, condición y marca en el catálogo móvil.
4.  **Generación de Reportes PDF:** Exportación de inventarios y reportes de ventas desde el panel admin.

---
*Este documento es una representación fiel del estado técnico actual de YapaMarket y sirve como base para futuras expansiones y presentaciones de inversión.*
