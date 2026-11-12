# SGA · Despliegue

## Backend

Desde `sga/`, con Java 25:

```sh
./gradlew test bootJar
```

El artefacto ejecutable queda en `build/libs/sga-0.0.1-SNAPSHOT.jar`. Configure estas variables en el alojamiento:

| Variable | Valor |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` para desactivar la consola, Swagger y las cuentas de ejemplo. |
| `SGA_JWT_SECRET` | Secreto del servidor de al menos 32 bytes. Genérelo con `openssl rand -hex 32`. |
| `SPRING_DATASOURCE_URL` | Ruta de H2 en un volumen persistente, por ejemplo `jdbc:h2:file:/var/lib/sga/sgadb`. El valor por defecto es `jdbc:h2:file:./data/sgadb`. |
| `PORT` | Puerto asignado por el alojamiento. El valor local por defecto es `8080`. |
| `SGA_CORS_ALLOWED_ORIGINS` | Orígenes del frontend separados por comas, por ejemplo `https://sga.ejemplo.com`. Incluya esquema y dominio, sin ruta ni barra final. Déjelo vacío si ambos servicios comparten origen. |
| `SGA_USUARIOS_INICIALES_HABILITADOS` | `true` para crear la cuenta inicial del perfil `prod`. El valor por defecto es `false`. |
| `SGA_ADMIN_CORREO`, `SGA_ADMIN_CONTRASENA`, `SGA_ADMIN_TELEFONO`, `SGA_ADMIN_DOCUMENTO`, `SGA_ADMIN_FECHA_NACIMIENTO` | Datos de la cuenta inicial. Use una contraseña única y una fecha `AAAA-MM-DD`. |

Comando de inicio:

```sh
java -Duser.timezone=America/Bogota -jar build/libs/sga-0.0.1-SNAPSHOT.jar
```

Configure la comprobación de salud en `GET /actuator/health`. Con `prod`, ingrese desde el frontend con la cuenta definida por las variables `SGA_ADMIN_*` y cree una reserva para `APT-301` (dos balcones) o `APT-302` (patio). Ambos tienen una tarifa inicial de 200.000 COP por ocupante facturable y noche. El importe inicial es de referencia para el proyecto. El catálogo público carga los apartamentos activos desde `GET /api/apartamentos`.

H2 en archivo requiere una instancia de Spring Boot por archivo de base de datos. Monte un volumen persistente para que la base sobreviva al reemplazo del proceso o contenedor. El primer arranque crea el esquema y carga los datos básicos desde `src/main/resources/data.sql`. Los arranques posteriores conservan los registros existentes, incluidos cambios de tarifas, reservas, folios y usuarios.

## Frontend

Siga la [guía del frontend](../../Frontend-Testing/frontend/README.md) para compilar con la URL pública de la API y configurar las rutas del alojamiento estático.
