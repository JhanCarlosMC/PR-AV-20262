# Seguridad — Guía 12

## Ejecutar

Requiere JDK 25. La clave se obtiene de `SGA_JWT_SECRET` (prioridad) o de
`src/main/resources/application-secrets.properties`, excluido de Git. Hay una
plantilla `.example`. Genere una clave con `openssl rand -base64 48`; sin clave
válida de al menos 32 bytes el servidor no arranca.

```sh
./gradlew bootRun
./gradlew build
```

JWT HS256, emisor `sga`, duración 30 minutos. El login es
`POST /api/auth/login`, con `correo` y `contrasena`; devuelve `token`, `tipo`,
`expiraEn` y `roles`. Enviar `Authorization: Bearer <token>`.
Swagger: http://localhost:8080/swagger-ui.html, botón Authorize.

Usuarios exclusivos del entorno académico H2:

| Correo | Contraseña | Rol |
|---|---|---|
| admin@sga.co | Admin2026 | ADMINISTRADOR |
| recepcion@sga.local | Recepcion2026 | RECEPCIONISTA |
| servicio@sga.local | Servicio2026 | PERSONAL_SERVICIO |
| huesped1@sga.local | Huesped2026 | HUESPED |
| huesped2@sga.local | Huesped2026 | HUESPED |

El registro público exige nombre, correo, contraseña, teléfono, documento y fecha
de nacimiento pasada. Siempre asigna HUESPED, aunque el cliente envíe otro rol.
Correo y documento únicos; duplicados responden 409. Contraseñas con BCrypt.

## Matriz aplicada

| Ruta/operación | HUESPED | RECEPCIONISTA | ADMINISTRADOR | PERSONAL_SERVICIO |
|---|---|---|---|---|
| Crear reserva | PORTAL y documento propio | DIRECTO | Todos los canales | No |
| Consultar/cancelar reserva | Solo titular | Sí | Sí | No |
| Listar/confirmar reservas | No | Sí | Sí | No |
| /api/temporadas/** | No | No | Sí | No |
| /api/tarifas/** | No | No | Sí | No |
| GET /api/apartamentos | Sí, también sin sesión | Sí | Sí | Sí |
| Otras operaciones de /api/apartamentos/** | No | No | Sí | No |
| /api/bloqueos/** | No | No | Sí | No |
| PUT /api/apartamentos/{id}/estado-operativo | No | No | Sí | Sí |

El listado público de apartamentos ya está implementado. Las reglas de administración
de apartamentos, temporadas, tarifas y bloqueos se reservan para endpoints futuros. El contrato OpenAPI publica solo las operaciones
implementadas. Las rutas no declaradas se deniegan por defecto; nuevos recursos
con propietario (folios/perfil) necesitan su propio verificador antes de habilitarse.

Canal y titular son reglas de acceso dependientes de la identidad autenticada;
por eso están en infraestructura, no en el dominio. La reserva sigue aplicando
sus propias reglas de negocio sin conocer usuarios ni Spring Security.

Para huéspedes, una reserva inexistente responde 403 igual que una ajena; el
personal autorizado recibe 404. Credenciales erradas o usuario inactivo: 401 con
ErrorResponse. Denegación por método: 403 con ErrorResponse. La cadena de filtros
usa la respuesta estándar OAuth2 (401 y WWW-Authenticate, sin JSON).

## Verificación

`SeguridadIntegrationTest` levanta HTTP en puerto aleatorio, usa H2 y tokens
firmados reales; comprueba login, hashes, registro, duplicados, inactividad,
roles, canales, titular, token alterado y vencido. Crea sus propios datos de
alojamiento y reserva sin depender de fechas fijas. Las claves de pruebas son
exclusivas de pruebas. `docs/api/seguridad.http` permite repetir peticiones a mano.
El arranque normal carga dos apartamentos, sus tarifas y una política en H2. Las pruebas usan bases en memoria separadas del archivo local.

## Despliegue

Activar `SPRING_PROFILES_ACTIVE=prod` y proporcionar `SGA_JWT_SECRET`. Este perfil
deshabilita la consola H2, Swagger y los usuarios de ejemplo. Consulte
[Despliegue](despliegue.md) para configurar la cuenta inicial y CORS.
Usar HTTPS. No hay refresh token: al vencer se inicia sesión de nuevo.

Referencia técnica: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
