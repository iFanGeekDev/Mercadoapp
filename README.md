# MercadoApp

Base inicial de una app Android (Kotlin + MVVM + Clean-ish architecture) para venta de equipos de segunda mano.

## Características Implementadas
- **UI & Navegación Moderna:** Compose + Navigation con un rediseño Premium Dark Theme, glassmorphism, gradientes, y animaciones en toda la aplicación.
- **Arquitectura:** MVVM con `StateFlow`, Coroutines, e Inyección de dependencias con Hilt.
- **Catálogo Home:** Muestra ofertas, últimos equipos y productos con Paging 3 para paginación eficiente. Skeleton Shimmers para estados de carga.
- **Detalle de Producto:** Selección de variantes (Condición, Procesador, RAM, Almacenamiento, Color) con validación cruzada y **Autocompletado Inteligente** priorizando la última selección y basándose en el stock disponible de forma estricta.
- **Carrito de Compras:** Gestión de carrito optimizada (consultas SQL directas para evitar lag), cálculo en tiempo real de subtotales, totales, y control de items.
- **Autenticación (Login/Registro):** Flujo de login y registro conectados a una API REST, con intercepción de peticiones (AuthInterceptor/TokenRefreshInterceptor) para mantener sesiones.
- **Backend Local Express.js:** Un backend simulado incluido (`/backend/server.js`) con base de datos en memoria para probar todos los flujos E2E de inicio a fin.
- **Flujo de Checkout (Pago):** Formulario robusto de 2 pasos (Resumen y Datos de Envío/Pago) con validaciones nativas y retención de estado.
- **Persistencia Local y Caché (Offline-first):** Room y DataStore para uso fluido.

## Siguientes Pasos Sugeridos (Próxima Sesión)

Para continuar iterando sobre esta base madura, aquí están las recomendaciones principales para mañana:

1. **Pull Request & Code Review:**
   - Crear el Merge PR desde la rama actual (`feature/checkout-registro-perfil-tests`) hacia `main` en GitHub.

2. **Integrar Validaciones Reales de Pago (SDK):**
   - El checkout actualmente simula el procesamiento. Como regla de negocio, convendría integrar una pasarela real en "modo test" (por ejemplo Stripe, o MercadoPago).
   - *Nota:* Esto requerirá conectar el backend local de Express con la SDK del procesador de pagos y retornar un "Client Secret" a la App de Android.

3. **Pruebas End-to-End (E2E) con Appium o Macrobenchmark:**
   - La lógica de negocio (`autoSelectFields`, `checkout validation`) es muy buena. Debemos agregar Unit Tests robustos para esas funciones específicas, y E2E UI Tests para simular que un usuario logre hacer una compra fluida.

4. **Flujo de "Mis Órdenes":**
   - Una vez la orden se simula pagada en el checkout, debería almacenarse en el backend y ser visible desde la `ProfileScreen` bajo un botón de "Mis Compras" u "Órdenes Recientes". 

5. **Mejorar Persistencia de Sesión al reinicio:**
   - Asegurarnos mediante pruebas que cuando la App se cierre desde la memoria (Swipe kill) y vuelva a abrir, mantenga al usuario logueado en la pantalla Home sin pedir nuevamente credenciales.
