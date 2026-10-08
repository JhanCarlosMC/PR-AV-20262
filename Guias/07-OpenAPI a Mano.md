# Guía rápida: cómo escribir el `openapi.yaml` a mano

**Programación Avanzada — Proyecto SGA**
Complemento de la Guía 07 (Diseño de APIs desde el Dominio)

---

## 1. Qué es y dónde va

El archivo `openapi.yaml` es el **contrato** de la API: describe cada endpoint, qué recibe, qué devuelve y con qué códigos responde. Se escribe **antes** de programar los controllers (enfoque *design-first*), a partir de los casos de uso de la Guía 06 y los DTOs del paquete `application.dto`.

Ubicación en el repositorio:

```
sga-<nombre-del-alojamiento>/
├── docs/
│   └── api/
│       └── openapi.yaml
├── src/
└── build.gradle
```

El archivo no se compila. Se versiona junto al código y se valida en [Swagger Editor](https://editor.swagger.io/).

---

## 2. Anatomía del archivo

Todo `openapi.yaml` tiene seis bloques de primer nivel:

| Bloque | Para qué sirve |
|---|---|
| `openapi` | Versión de la especificación. Usamos `3.1.0`. |
| `info` | Título, descripción y versión de **su** API. |
| `servers` | URL base donde corre la API (`http://localhost:8080`). |
| `tags` | Grupos de endpoints. Use uno por agregado o módulo (Reservas, Folios, Disponibilidad…). |
| `paths` | Los endpoints. Es la parte más larga. |
| `components` | Piezas reutilizables: esquemas (DTOs), respuestas, parámetros y seguridad. |

Regla de oro de YAML: **indentación con 2 espacios, nunca tabulaciones**. Casi todos los errores en Swagger Editor son de indentación.

---

## 3. Template inicial

Copie este archivo en `docs/api/openapi.yaml` y reemplace lo que está entre `<...>`. Ya trae las convenciones del curso: errores con `ErrorResponse`, respuestas reutilizables y seguridad JWT.

```yaml
openapi: 3.1.0

info:
  title: API SGA — <Nombre del alojamiento>
  description: |
    API RESTful del Sistema de Gestión de Alojamiento de <nombre>.

    **Agregado principal:** Reserva
  version: 1.0.0
  contact:
    name: <Nombre del equipo>
    email: <correo@uqvirtual.edu.co>

servers:
  - url: http://localhost:8080
    description: Servidor de desarrollo local

tags:
  - name: Reservas
    description: Ciclo de vida del agregado Reserva
  # - name: <OtroModulo>
  #   description: <...>

paths:

  # ==================== RESERVAS ====================

  /api/reservas:
    post:
      tags: [Reservas]
      summary: <Qué hace, en lenguaje del dominio>
      operationId: crearReserva
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/<NombreRequest>'
      responses:
        '201':
          description: <Recurso creado>
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/<NombreResponse>'
        '400':
          $ref: '#/components/responses/DatosInvalidos'
        '409':
          $ref: '#/components/responses/ReglaDeNegocio'

  /api/reservas/{codigo}:
    get:
      tags: [Reservas]
      summary: <Consultar por identificador>
      operationId: consultarReserva
      parameters:
        - $ref: '#/components/parameters/CodigoReserva'
      responses:
        '200':
          description: <Recurso encontrado>
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/<NombreResponse>'
        '404':
          $ref: '#/components/responses/NoEncontrado'

components:

  parameters:
    CodigoReserva:
      name: codigo
      in: path
      required: true
      schema:
        type: string
        example: RES-2026-0001

  schemas:

    # ---------- Request ----------
    <NombreRequest>:
      type: object
      required: [<campo1>, <campo2>]
      properties:
        <campo1>:
          type: string
        <campo2>:
          type: string
          format: date

    # ---------- Response ----------
    <NombreResponse>:
      type: object
      properties:
        codigo:
          type: string
        estado:
          type: string

    # ---------- Error ----------
    ErrorResponse:
      type: object
      properties:
        timestamp:
          type: string
          format: date-time
        status:
          type: integer
        error:
          type: string
        mensaje:
          type: string
          description: Mensaje con significado de negocio, nunca una traza técnica
        path:
          type: string

  responses:
    DatosInvalidos:
      description: La petición no cumple el formato esperado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    NoEncontrado:
      description: El recurso no existe
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    ReglaDeNegocio:
      description: La operación viola una regla de negocio
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'

  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

security:
  - bearerAuth: []
```

---

## 4. Paso a paso: documentar un endpoint

Repita esta secuencia por cada fila de su tabla de endpoints (Guía 07, punto 1 de la actividad).

### Paso 1. Partir del caso de uso

Cada endpoint sale de un caso de uso. Anote el nombre del caso, el actor y las reglas de negocio (RN) que puede violar. Esas RN se convertirán en respuestas `409`.

Ejemplo: *Confirmar reserva* — Recepcionista — RN-09 (hora estimada de llegada obligatoria).

### Paso 2. Elegir ruta y método

Siga las convenciones del curso:

| Operación | Método | Ruta | Éxito |
|---|---|---|---|
| Crear | `POST` | `/api/reservas` | `201` |
| Listar / buscar | `GET` | `/api/reservas?estado=...&page=0&size=10` | `200` |
| Consultar uno | `GET` | `/api/reservas/{codigo}` | `200` |
| Transición de estado | `PUT` | `/api/reservas/{codigo}/confirmar` | `200` |
| Cálculo con cuerpo complejo | `POST` | `/api/cotizaciones` | `200` |

Reglas del proyecto:

- Rutas con **sustantivos en plural**; la acción de dominio va como último segmento solo en transiciones (`/confirmar`, `/cancelar`, `/check-in`).
- **No hay `DELETE`**: las bajas son lógicas (sección 12.4 del proyecto). Se modelan como transición (`PUT .../desactivar`).
- **No hay `PATCH` genérico** para cambiar estado: cada transición tiene su propio endpoint porque cada una tiene sus propias reglas.
- Paginación con `page` (desde 0) y `size` (máximo 10).

### Paso 3. Declarar parámetros

Hay dos tipos de parámetro que usarán casi siempre:

```yaml
parameters:
  - name: codigo          # parte de la ruta: /api/reservas/{codigo}
    in: path
    required: true        # los de path SIEMPRE son required
    schema:
      type: string
  - name: estado          # filtro opcional: ?estado=CONFIRMADA
    in: query
    required: false
    schema:
      $ref: '#/components/schemas/EstadoReserva'
```

Si el mismo parámetro aparece en varios endpoints, muévalo a `components/parameters` y referéncielo con `$ref`.

### Paso 4. Describir el cuerpo (`requestBody`)

Solo para `POST` y `PUT` que reciben datos. El esquema debe coincidir **campo por campo** con su record de `application.dto`:

```java
public record CancelarReservaRequest(String motivo) { }
```

```yaml
CancelarReservaRequest:
  type: object
  required: [motivo]
  properties:
    motivo:
      type: string
      minLength: 5
      maxLength: 300
```

Las validaciones de formato (`required`, `minLength`, `minimum`, `pattern`) van aquí. Las reglas de negocio **no**: esas se documentan como `409`.

### Paso 5. Listar todas las respuestas

Documente **todas** las respuestas posibles, no solo la exitosa:

| Código | Cuándo |
|---|---|
| `200` | Consulta o transición exitosa |
| `201` | Recurso creado |
| `400` | Formato inválido (campo faltante, fecha mal escrita, rango imposible) |
| `401` | Sin token |
| `403` | Con token, pero el rol no tiene permiso |
| `404` | El recurso no existe |
| `409` | Viola una regla de negocio (convención del curso) |

En la descripción del `409` diga **qué regla** se viola:

```yaml
'409':
  description: |
    La reserva no se puede confirmar:
    - RN-09: falta la hora estimada de llegada
    - El estado actual no permite la transición
  content:
    application/json:
      schema:
        $ref: '#/components/schemas/ErrorResponse'
```

### Paso 6. Agregar un ejemplo

Un ejemplo vale más que la descripción. Agréguelo en el `requestBody`:

```yaml
requestBody:
  required: true
  content:
    application/json:
      schema:
        $ref: '#/components/schemas/CancelarReservaRequest'
      example:
        motivo: El huésped cambió sus fechas de viaje
```

---

## 5. Ejemplo completo de un endpoint

Así queda una transición de estado aplicando los seis pasos:

```yaml
  /api/reservas/{codigo}/cancelar:
    put:
      tags: [Reservas]
      summary: Cancelar una reserva
      description: |
        Cancela una reserva vigente y registra el motivo.
        Caso de uso: CU-xx Cancelar reserva.
      operationId: cancelarReserva
      parameters:
        - $ref: '#/components/parameters/CodigoReserva'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CancelarReservaRequest'
            example:
              motivo: El huésped cambió sus fechas de viaje
      responses:
        '200':
          description: Reserva cancelada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ReservaResponse'
        '400':
          $ref: '#/components/responses/DatosInvalidos'
        '404':
          $ref: '#/components/responses/NoEncontrado'
        '409':
          description: La reserva está en un estado que no permite cancelarla
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
```

---

## 6. Tipos de datos: de Java a OpenAPI

| En Java (DTO) | En OpenAPI |
|---|---|
| `String` | `type: string` |
| `int` | `type: integer` |
| `long` (dinero en pesos) | `type: integer`, `format: int64` |
| `boolean` | `type: boolean` |
| `LocalDate` | `type: string`, `format: date` → `2026-10-10` |
| `LocalTime` | `type: string`, `format: time` → `15:30:00` |
| `LocalDateTime` / `Instant` | `type: string`, `format: date-time` |
| `enum` | `type: string` con `enum: [VALOR1, VALOR2]` |
| `List<X>` | `type: array` con `items: { $ref: ... }` |
| Otro record | `$ref: '#/components/schemas/X'` |

Recuerde: el dinero **nunca** se representa con `number` (decimal); use enteros en pesos, igual que en el dominio.

Los enums se declaran una sola vez en `components/schemas` y se reutilizan:

```yaml
CanalOrigen:
  type: string
  enum: [PORTAL, DIRECTO, EXTERNO]
```

Para un listado paginado, defina un esquema de página:

```yaml
PaginaReservas:
  type: object
  properties:
    contenido:
      type: array
      items:
        $ref: '#/components/schemas/ReservaResumenResponse'
    pagina:
      type: integer
    tamanio:
      type: integer
    totalElementos:
      type: integer
      format: int64
    totalPaginas:
      type: integer
```

---

## 7. Validar en Swagger Editor

1. Abra [https://editor.swagger.io/](https://editor.swagger.io/).
2. Borre el ejemplo del panel izquierdo y pegue su archivo completo.
3. Corrija hasta que el panel izquierdo muestre **cero errores**.
4. En el panel derecho revise que los endpoints aparezcan agrupados por tag y que los esquemas estén al final, en **Schemas**.
5. Tome una captura del resultado para el entregable.

Recomendación: valide cada vez que termine un endpoint, no al final. Encontrar un error de indentación en 30 líneas es fácil; en 800 no.

---

## 8. Errores frecuentes

| Síntoma en Swagger Editor | Causa | Solución |
|---|---|---|
| `bad indentation` | Tabulación o espacios desalineados | Use solo 2 espacios por nivel |
| `Could not resolve reference` | `$ref` mal escrito | Debe ser `'#/components/schemas/Nombre'`, con comillas y el nombre exacto |
| `should have required property 'description'` | Respuesta sin descripción | Toda respuesta necesita `description` |
| `path parameter must be required` | Falta `required: true` en un parámetro `in: path` | Agregarlo |
| `Declared path parameter needs to be defined` | La ruta tiene `{codigo}` pero no se declaró el parámetro | Agregar el parámetro en `parameters` |
| El campo aparece como opcional sin serlo | `required` quedó dentro de `properties` | `required` va al mismo nivel que `properties`, como lista |
| Código de respuesta rechazado | Se escribió `200:` sin comillas | Escribir `'200':` |

---

## 9. Lista de verificación antes de entregar

- [ ] El archivo está en `docs/api/openapi.yaml`.
- [ ] `info.title` lleva el nombre de su alojamiento.
- [ ] Hay un endpoint por cada caso de uso de su tabla.
- [ ] Ningún endpoint usa `DELETE` ni `PATCH` para cambiar estado.
- [ ] Cada transición de estado es un `PUT` con su propia ruta.
- [ ] Cada esquema de `components/schemas` coincide con un record de `application.dto`.
- [ ] Todos los endpoints documentan sus respuestas de error, no solo la exitosa.
- [ ] Cada `409` dice qué regla de negocio se viola.
- [ ] El dinero es entero (`int64`), las fechas usan `format: date`.
- [ ] Las reglas propias del equipo (L-20) aparecen en el contrato.
- [ ] Swagger Editor muestra cero errores.

---

## Referencias

- OpenAPI Initiative. (2021). *OpenAPI Specification 3.1.0*. https://spec.openapi.org/oas/v3.1.0
- Swagger Editor: https://editor.swagger.io/
