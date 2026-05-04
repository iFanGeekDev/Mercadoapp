# MercadoApp

Base inicial de una app Android (Kotlin + MVVM + Clean-ish architecture) para venta de equipos de segunda mano.

## Características Implementadas
- **UI & Navegación:** Compose + Navigation.
- **Arquitectura:** MVVM con `StateFlow`, Coroutines, e Inyección de dependencias con Hilt.
- **Catálogo Home:** Muestra ofertas, últimos equipos y todos los productos con Paging 3 para paginación eficiente.
- **Detalle de Producto:** Selección dinámica de variantes (Condición, Procesador, RAM, Almacenamiento, Color) con validación cruzada basada en stock. Integración con Coil para carga de imágenes reales.
- **Carrito de Compras:** Gestión de carrito con fusión de cantidades para productos idénticos, cálculo de subtotales y total.
- **Autenticación:** Flujo de login con JWT, almacenado de forma segura localmente. Guard de navegación para redirigir a Login o Home según el estado de autenticación.
- **Persistencia Local y Caché (Offline-first):**
  - **Room:** Almacenamiento de caché de productos para uso offline y persistencia del carrito de compras.
  - **DataStore:** Almacenamiento de tokens de acceso (`accessToken`, `refreshToken`).
- **Conexión a Backend:** Integración de API remota mediante Retrofit, OkHttp y Kotlin Serialization, mapeo mediante DTOs.

## Siguientes Pasos Sugeridos
1. **Flujo de Checkout:** Implementar la pantalla de pago (CheckoutScreen) consumiendo los items del carrito.
2. **Pantalla de Registro:** Añadir funcionalidad para registrar nuevos usuarios.
3. **Gestión de Perfil:** Pantalla para ver y editar datos del usuario autenticado, y ver su historial de compras.
4. **Testing Completo:** Ampliar la cobertura con Unit tests para ViewModels/Repositorios y UI tests para los flujos principales (Login, Agregar al carrito, Checkout).
5. **Manejo de Errores y UI States Complejos:** Refinar la experiencia de usuario (UX) ante fallos de red o errores de backend (ej. token expirado y refresh token automático).
