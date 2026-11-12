# SGA · Frontend

Aplicación React y Vite para iniciar sesión y gestionar reservas desde recepción o administración. La portada pública consulta el catálogo del backend. Cada apartamento tiene una ficha con los servicios del anuncio y un acceso al formulario de recepción.

## Prueba local

Desde `sga/`, inicie Spring Boot con Java 25:

```sh
SGA_JWT_SECRET="$(openssl rand -hex 32)" ./gradlew bootRun
```

Spring Boot crea `sga/data/sgadb.mv.db` al iniciar y carga los datos base. Al reiniciar desde el mismo directorio, se conservan las reservas y los datos guardados.

Desde `Frontend-Testing/frontend/`, con Node 22.13 o superior de la rama 22:

```sh
npm ci
npm run dev
```

Abra la URL que imprime Vite. El servidor de desarrollo reenvía `/api` a `localhost:8080`. Para otra URL del backend, copie `.env.example` a `.env.local` y edite `VITE_API_URL`.

1. Ingrese con `recepcion@sga.local` y la contraseña de prueba `Recepcion2026`.
2. Abra el detalle de `APT-301` (dos balcones) o `APT-302` (patio) desde el inicio y pulse **Crear reserva en recepción**. El formulario selecciona ese apartamento. Use fechas futuras, una hora estimada de llegada y al menos un ocupante con documento, nombre y fecha de nacimiento.
3. Abra el detalle, confirme la reserva y compruebe que aparece al filtrar por **Confirmada**. Desde el detalle también puede cancelarla indicando el motivo.

## Despliegue del frontend

```sh
npm ci
VITE_API_URL=https://api.ejemplo.com/api npm run build
```

Publique `dist/` en un alojamiento estático. Configure el alojamiento para servir `index.html` en las rutas de navegación como `/reservas/nueva` y `/reservas/RES-2026-00001`, conservando la entrega normal de archivos estáticos. Si frontend y API comparten origen, puede omitir `VITE_API_URL` y dirigir `/api/*` a Spring Boot en el proxy. Con orígenes separados, configure `SGA_CORS_ALLOWED_ORIGINS` en el backend con el origen HTTPS del frontend.

La variable `VITE_API_URL` se incorpora al compilar y es visible en el navegador. Consulte [el despliegue del backend](../../sga/docs/despliegue.md) para configurar el perfil, el secreto JWT y el acceso de prueba.
