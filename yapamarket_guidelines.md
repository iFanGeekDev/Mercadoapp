# YapaMarket - Antigravity Skill & Context Guidelines

Este documento define el contexto, arquitectura y reglas de desarrollo para YapaMarket. Úsalo como fuente de verdad antes de generar código o plantear soluciones.

## 1. Estructura del Proyecto (Monorepo)
El proyecto está estructurado como un monorepo que contiene el ecosistema completo de la aplicación:
*   `app/` - Aplicación móvil Android.
*   `backend/` - Servidor Node.js / Express.
*   `web/` - Panel de Administración (Próximamente en React/Next.js).

## 2. Idioma y Localización
*   **Idioma UI:** Toda la interfaz orientada al usuario final (App y Web) debe estar estrictamente en **Español Latinoamericano**.
*   **Contexto Geográfico (Perú):** 
    *   Los envíos y las direcciones utilizan el formato geográfico peruano (Ubigeo): **Departamento**, **Provincia** y **Distrito**.
    *   Las interfaces de dirección deben utilizar selectores dependientes (Dropdowns) en este orden geográfico.

## 3. Arquitectura Android (`app/`)
*   **UI:** Construido al 100% con **Jetpack Compose**. No usar XML.
*   **Patrón de Diseño:** **MVVM** (Model-View-ViewModel) con Estado Unidireccional (StateFlow para `UiState`).
*   **Inyección de Dependencias:** **Dagger Hilt** (`@HiltViewModel`, `@Inject`).
*   **Red / API:** **Retrofit** con **Kotlinx Serialization** (`@Serializable` en los DTOs).
*   **Navegación:** Compose Navigation tradicional o con Hilt.
*   **Diseño Visual:**
    *   Tema oscuro moderno (`Dark900`, `Dark800`).
    *   Uso de gradientes y colores vibrantes (`Brand500`, `Accent500`).
    *   Cuidar las proporciones visuales (por ejemplo, iconos proporcionados respecto a los números, padding generosos para evitar clicks accidentales cerca de la cámara/bordes).

## 4. Arquitectura Backend (`backend/`)
*   **Framework:** Node.js con Express.
*   **Base de Datos:** PostgreSQL. La base de datos local se llama `YAPAMARKET`.
*   **Autenticación:** JWT (JSON Web Tokens) devolviendo `access_token` y `refresh_token`.
*   **Datos de prueba (Local):** Uso de archivos JSON locales (como Ubigeo) cuando la DB no está disponible, o in-memory storage para prototipado rápido.

## 5. Arquitectura Web (`web/` - Futura)
*   **Framework:** React (Vite) o Next.js.
*   **Estilos:** Preferencia por CSS nativo estilizado o TailwindCSS según lo decida el usuario, manteniendo concordancia con el diseño oscuro y moderno de la app móvil.
*   **Propósito:** Panel de administración de inventario (CRUD de productos con manejo de variantes y stock).

## 6. Flujo de Trabajo y Buenas Prácticas
*   **Verificar dependencias:** Antes de codificar, asegurar que las bibliotecas de Kotlin (como serialization) están configuradas adecuadamente.
*   **Concurrencia:** Los cambios en un entorno pueden afectar al otro. Por ejemplo, al actualizar el modelo de producto en Backend, se debe actualizar la entidad/DTO en Android y en Web.
*   **Commits y Git:** Utilizar el formato Conventional Commits (e.g., `feat:`, `fix:`, `refactor:`).
*   **SKILL AUTOMÁTICO - AUTO-COMMIT:** Siempre que realice un cambio, implementación o modificación de código que cumpla un objetivo, **debo subir todo a Git de forma automática**. El commit debe contener un mensaje claro, descriptivo y bien comentado siguiendo Conventional Commits, para asegurar un excelente tracking y trazabilidad en el repositorio. No debo esperar a que el usuario me lo pida, debe ser parte de mi flujo de cierre de tarea.
