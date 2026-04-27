# MercadoApp (starter)

Base inicial de una app Android (Kotlin + MVVM + Clean-ish architecture) para venta de equipos de segunda mano.

## Incluye
- Compose + Navigation
- MVVM con `StateFlow`
- Coroutines
- Inyección de dependencias con Hilt
- Catálogo home (ofertas, últimos, todos)
- Detalle de producto con selección de variantes:
  - Condición (`FAIR`, `NORMAL`, `EXCELLENT`)
  - Procesador
  - RAM
  - Almacenamiento
  - Color
- Validación cruzada de opciones según stock disponible
- Carga de imagen del detalle con Coil
- Test unitarios iniciales para la lógica de variantes

## Próximos pasos sugeridos
1. Conectar backend real (Ktor/Retrofit + DTO + mappers + paging).
2. Cargar imágenes reales con Coil.
3. Agregar carrito/checkout y autenticación.
4. Persistencia local (Room + DataStore).
5. Testing: unit tests del resolver de variantes + UI tests.
