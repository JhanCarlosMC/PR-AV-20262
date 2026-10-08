**Programa de Ingeniería de Sistemas y Computación Universidad del Quindío**

**Curso:** Programación Avanzada
**Guía:** 07 
**Título:** Diseño de APIs RESTful desde el Dominio
**Duración estimada:** 120 minutos
**Docente:** Jhan Carlos Martinez Ceballos
**Proyecto del semestre:** SGA — Sistema de Gestión de Alojamiento

---

# 1. Objetivo

En las guías anteriores construimos el interior del sistema: el dominio del SGA (Guías 04 y 05), sus pruebas (Guía 05.1) y los casos de uso que lo orquestan (Guía 06). Todo eso funciona, pero **nadie puede usarlo todavía**: no hay forma de pedirle una reserva desde afuera.

Esta guía diseña esa puerta de entrada.

El objetivo **no es implementar la API** —eso es la Guía 08—, sino aprender a:

- mapear las operaciones del dominio del SGA a endpoints REST, sin inventar operaciones nuevas,
- diseñar DTOs que transporten datos sin filtrar el dominio hacia afuera,
- escribir la especificación OpenAPI del SGA y validarla.

> En esta guía el único código nuevo son **records** (los DTOs) y un archivo **YAML**. No hay controllers, no hay Spring, no hay HTTP ejecutándose. Se diseña el contrato; mañana se implementa.

---

# 2. Conceptos básicos

1. **API.** Contrato que define cómo un sistema externo se comunica con el nuestro, sin conocer su implementación interna.
2. **REST.** Estilo arquitectónico donde todo es un **recurso** identificado por una URL, y las acciones se expresan con métodos HTTP.
3. **Recurso y endpoint.** El recurso es el concepto (`Reserva`); el endpoint es la URL que lo identifica (`/api/reservas/{codigo}`).
4. **DTO** (_Data Transfer Object_). Objeto diseñado para transportar datos entre la API y la capa de aplicación. No tiene comportamiento ni reglas de negocio.
5. **Bean Validation.** Anotaciones (`@NotNull`, `@Size`) que verifican la **forma** del mensaje recibido. No sustituyen las reglas del dominio.
6. **OpenAPI.** Estándar para describir una API REST de forma independiente del lenguaje, en un archivo YAML o JSON.
7. **Domain-First.** Enfoque donde la API emerge del modelo de dominio ya construido, y no al revés.

---

# 3. Contextualización teórica

## 3.1 ¿Qué es una API?

Una **API** (_Application Programming Interface_) es un contrato: define qué se puede pedir, cómo se pide y qué se recibe de vuelta.

Piense en la recepción del alojamiento. El huésped no entra a la oficina a revisar el calendario de ocupación: le pide algo al recepcionista, este consulta lo que debe consultar y devuelve una respuesta. El huésped nunca ve el procedimiento interno. La recepción **es** la API del alojamiento, y el sistema que diseñamos hoy hace exactamente ese papel para el frontend, para la app móvil y para los canales externos (7.8).

## 3.2 REST, recursos y URLs

En REST, un **recurso** es cualquier concepto que se pueda nombrar e identificar. Se accede mediante una URL:

|Recurso|URL (endpoint)|
|---|---|
|Todas las reservas|`/api/reservas`|
|Una reserva específica|`/api/reservas/RES-2026-00042`|
|El folio de esa reserva|`/api/reservas/RES-2026-00042/folio`|
|Todos los apartamentos|`/api/apartamentos`|

```
https://sga.ejemplo.co/api/reservas/RES-2026-00042/folio
└──── servidor ──────┘ └─ recurso ─┘ └── identidad ──┘ └ sub-recurso ┘
```

Observe dos decisiones del SGA que no son cosméticas:

- **Los recursos se nombran con el lenguaje ubicuo** (F-11): `/api/reservas`, `/api/apartamentos`, `/api/temporadas`. Nunca `/api/bookings` ni `/api/rooms`. Una URL en inglés en un negocio que habla español es una traducción de más, y el proyecto la evalúa.
- **La identidad en la URL es la del negocio**, no la de la base de datos: `RES-2026-00042` es el `CodigoReserva` que modelamos en la Guía 04, y `APT-301` es la `IdentificacionApartamento`. Ese código ya existía antes del software y sigue siendo válido aunque mañana se cambie de motor de persistencia.

## 3.3 Métodos HTTP

La URL dice **qué** recurso. El método dice **qué hacer** con él.

|Método|Acción|Ejemplo en el SGA|
|---|---|---|
|**GET**|Leer|`GET /api/reservas`|
|**POST**|Crear|`POST /api/reservas`|
|**PUT**|Reemplazar o ejecutar una operación completa|`PUT /api/reservas/{codigo}/confirmar`|
|**PATCH**|Actualización parcial|(esta API no lo usa; ver 4.4)|
|**DELETE**|Eliminar|(esta API no lo usa; las eliminaciones son lógicas, 12.4)|

## 3.4 Códigos de estado HTTP

El servidor responde con un número que indica el resultado:

|Rango|Significado|Códigos comunes|
|---|---|---|
|**2xx**|Éxito|`200 OK`, `201 Created`, `204 No Content`|
|**4xx**|Error del cliente|`400 Bad Request`, `403 Forbidden`, `404 Not Found`, `409 Conflict`|
|**5xx**|Error del servidor|`500 Internal Server Error`|

El proyecto exige códigos adecuados y errores **con significado de negocio**, no trazas técnicas (12.4). En la sección 4.6 se fija la convención del SGA.

## 3.5 JSON y anatomía de una petición

**JSON** es el formato de intercambio. Es texto plano, legible y directo:

```json
{
  "codigo": "RES-2026-00042",
  "estado": "PENDIENTE",
  "fechaEntrada": "2026-10-10",
  "fechaSalida": "2026-10-12",
  "canalOrigen": "PORTAL"
}
```

Una comunicación completa tiene dos partes:

**Petición:**

```
POST /api/reservas HTTP/1.1              ← método + URL
Host: localhost:8080                     ← servidor destino
Content-Type: application/json           ← formato del cuerpo

{                                        ← cuerpo (body) en JSON
  "identificacionApartamento": "APT-301",
  "fechaEntrada": "2026-10-10",
  "fechaSalida": "2026-10-12",
  "canalOrigen": "PORTAL"
}
```

**Respuesta:**

```
HTTP/1.1 201 Created                     ← código de estado
Location: /api/reservas/RES-2026-00042   ← URL del recurso creado

{
  "codigo": "RES-2026-00042",
  "estado": "PENDIENTE",
  "valorTotal": 960000
}
```

> **Fechas sin hora.** `fechaEntrada` y `fechaSalida` viajan como `"2026-10-10"`, no como `date-time`. La estancia se define con dos fechas sin hora y el intervalo es `[entrada, salida)` (3.1 del proyecto). Poner una hora en esos campos reintroduce en la API una ambigüedad que el dominio ya eliminó.

## 3.6 Domain-First frente a Contract-First

**Contract-First** (enfoque tradicional):

```
1. Diseñar la especificación OpenAPI
2. Generar interfaces y stubs
3. Implementar controllers
4. Crear servicios
5. Crear entidades
```

El problema: **la API termina dictando la estructura del dominio**. Los campos del JSON se vuelven los atributos de las clases, y el negocio se acomoda al contrato.

**Domain-First** (el enfoque del curso):

```
1. Modelar el dominio del SGA          (Guías 04 y 05)
2. Probarlo                            (Guía 05.1)
3. Crear los casos de uso              (Guía 06)
4. Mapear dominio → API                (ESTA GUÍA)
5. Diseñar los DTOs                    (ESTA GUÍA)
6. Documentar en OpenAPI               (ESTA GUÍA)
```

El beneficio: **el dominio dicta la estructura de la API**. Y algo más práctico: como las operaciones ya existen, esta guía no inventa nada. Solo traduce.

---

# 4. Parte 1: Del dominio del SGA a endpoints REST

## 4.1 Las operaciones que ya existen

No hay que decidir qué expone la API. Ya está decidido: es el catálogo de casos de uso de la Guía 06.

```java
// Capa de aplicación (Guía 06)
CrearReservaUseCase                → Reserva.crear(...)
ConfirmarReservaUseCase            → reserva.confirmar()
CancelarReservaUseCase             → reserva.cancelar(...)
RegistrarLlegadaUseCase            → reserva.registrarLlegada(...)
RegistrarSalidaUseCase             → reserva.registrarSalida(...)
DeclararNoShowUseCase              → reserva.declararNoShow(...)
ConsultarReservasPorEstadoUseCase  → reservaRepository.buscarPorEstado(...)
```

## 4.2 Mapeo a HTTP

|Caso de uso|Método|Endpoint|Razón|
|---|---|---|---|
|`CrearReservaUseCase`|POST|`/api/reservas`|Crea un recurso nuevo|
|`ConsultarReservasPorEstadoUseCase`|GET|`/api/reservas`|Lista recursos, con filtros|
|consultar una reserva|GET|`/api/reservas/{codigo}`|Obtiene un recurso|
|`ConfirmarReservaUseCase`|PUT|`/api/reservas/{codigo}/confirmar`|Operación de negocio|
|`CancelarReservaUseCase`|PUT|`/api/reservas/{codigo}/cancelar`|Operación de negocio|
|`RegistrarLlegadaUseCase`|PUT|`/api/reservas/{codigo}/registrar-llegada`|Operación de negocio|
|`RegistrarSalidaUseCase`|PUT|`/api/reservas/{codigo}/registrar-salida`|Operación de negocio|
|`DeclararNoShowUseCase`|PUT|`/api/reservas/{codigo}/no-show`|Operación de negocio|
|`ModificarReservaUseCase`|PUT|`/api/reservas/{codigo}`|Reemplaza los datos modificables (7.5, RN-14)|
|consultar el folio|GET|`/api/reservas/{codigo}/folio`|Sub-recurso de la reserva|
|registrar un pago|POST|`/api/reservas/{codigo}/folio/pagos`|Crea un movimiento en el folio (RN-15)|

> `VencerReservasPendientesUseCase` **no aparece**. No tiene endpoint porque no lo dispara un actor externo, sino el paso del tiempo (RN-21). Un caso de uso sin actor no necesita puerta de entrada.

## 4.3 Principios de mapeo

1. **Recursos = agregados.** Un agregado, un recurso principal. `Reserva` y `Apartamento` son recursos; `Ocupante` no, porque es una entidad interna de `Reserva` (Guía 05) y no tiene vida propia.
2. **El folio es la excepción que confirma la regla.** `Folio` es un agregado propio, pero pertenece a una sola reserva (7.7) y no se navega sin ella. Por eso se expone como sub-recurso: `/api/reservas/{codigo}/folio`. Ser agregado no obliga a ser recurso raíz; lo que decide es si el concepto tiene identidad propia **para quien consume la API**.
3. **Un endpoint por operación del negocio.** Si el dominio tiene una operación, la API tiene un endpoint.
4. **Verbos HTTP ≠ operaciones del dominio.** El SGA tiene más operaciones que verbos HTTP: para eso están los sub-recursos de acción (`/confirmar`, `/cancelar`).
5. **Nada de `DELETE`.** Ninguna reserva ni apartamento se elimina: las eliminaciones son lógicas (12.4) y una reserva termina en un estado terminal (sección 8). Si aparece un `DELETE` en su diseño, revise qué regla lo justifica.

## 4.4 Por qué no existe `PATCH /api/reservas/{codigo}/estado`

Es tentador resolver todas las transiciones con un solo endpoint que reciba el estado destino:

```http
PATCH /api/reservas/RES-2026-00042/estado
{ "nuevoEstado": "CANCELADA", "motivo": "El huésped cambió de planes" }
```

Parece económico: un endpoint en lugar de seis. Pero ese diseño **deshace en la API el trabajo hecho en el dominio**.

En la Guía 05 se eliminó `setEstado()` del agregado precisamente porque un cambio de estado nunca es solo un cambio de estado: cancelar exige aplicar la política congelada y registrar la retención en el folio (RN-13), registrar la llegada exige que el apartamento esté `PREPARADO` (RN-11), y declarar no-show solo es posible pasada la hora límite del día de entrada (L-15). Por eso el agregado expone `cancelar(...)`, `registrarLlegada(...)` y `declararNoShow(...)`, y no un `cambiarEstado(estado)` genérico.

Un endpoint `/estado` reintroduce ese setter por la puerta de atrás:

|Problema|Consecuencia|
|---|---|
|El cliente decide la transición|La API deja de decir qué operaciones existen; el frontend debe conocer el ciclo de vida interno de la reserva|
|Un solo DTO para todas las transiciones|`motivo` es obligatorio para cancelar pero no para confirmar: la validación no puede expresarse en el contrato|
|Un solo `operationId`|La documentación no dice qué se puede hacer, solo que "el estado cambia"|
|El controller necesita un `switch` sobre el estado recibido|Aparece lógica de negocio en el adaptador, justo lo que prohíben 12.1 y la Guía 08|

La alternativa es directa: **si el dominio tiene una operación, la API tiene un endpoint.**

```http
PUT /api/reservas/RES-2026-00042/confirmar         → reserva.confirmar()
PUT /api/reservas/RES-2026-00042/cancelar          → reserva.cancelar(motivo, fechaActual)
PUT /api/reservas/RES-2026-00042/no-show           → reserva.declararNoShow(fechaHoraActual)
```

Cancelar y declarar no-show terminan ambas la reserva y ambas liberan las noches (RN-12), pero **son dos intenciones distintas**, con disparadores distintos y consecuencias distintas según la política (RN-13). Son dos endpoints, por la misma razón por la que en la Guía 06 fueron dos casos de uso.

> **Consecuencia de diseño:** esta API no usa `PATCH`. Todas las operaciones del agregado son acciones del negocio con precondiciones propias, no actualizaciones parciales de campos. `PATCH` seguiría siendo correcto en una API donde sí se editan atributos sueltos —por ejemplo, el teléfono de un usuario (7.2)—.

## 4.5 Dos consultas que no son agregados

El SGA tiene dos operaciones centrales que **no corresponden a ningún agregado**: la búsqueda de disponibilidad y la cotización (7.4 y 7.5). Merecen su propia decisión de diseño.

**Búsqueda de disponibilidad.** Es una consulta con filtros sobre el inventario, así que es un `GET` con parámetros:

```http
GET /api/apartamentos/disponibles?fechaEntrada=2026-10-10&fechaSalida=2026-10-12&personas=4&page=0&size=10
```

**Cotización.** Aquí aparece un problema: para calcular el valor exacto no basta la cantidad de personas. Se necesita la **fecha de nacimiento de cada ocupante**, porque solo los facturables generan cargo (RN-06) y la condición se evalúa a la fecha de entrada (3.2). Una lista de fechas de nacimiento no cabe con elegancia en una cadena de consulta.

Por eso la cotización se envía con `POST`, aunque no cree nada:

```http
POST /api/cotizaciones
{
  "identificacionApartamento": "APT-301",
  "fechaEntrada": "2026-10-10",
  "fechaSalida": "2026-10-12",
  "ocupantes": [
    { "fechaNacimiento": "1990-05-12" },
    { "fechaNacimiento": "2019-03-02" }
  ]
}
```

Responde `200 OK`, no `201 Created`: no se creó ningún recurso, se calculó algo. Es una excepción consciente al uso habitual de `POST`, y así debe documentarse.

> **Cotizar no es reservar.** La cotización no retiene noches ni bloquea nada: es un cálculo (7.4). Entre cotizar y reservar puede entrar otra reserva por otro canal, y el sistema debe rechazarla en la creación (RN-01), no en la cotización.

## 4.6 Los códigos de estado del SGA

|Situación|Código|Ejemplo|
|---|---|---|
|Reserva creada|`201 Created` + header `Location`|`POST /api/reservas`|
|Operación ejecutada o consulta exitosa|`200 OK`|`PUT /api/reservas/{codigo}/confirmar`|
|El mensaje está mal formado o le falta un campo|`400 Bad Request`|`fechaEntrada` ausente|
|Sin autenticar|`401 Unauthorized`|Petición sin token (Guía 12)|
|Rol insuficiente o recurso ajeno|`403 Forbidden`|Un huésped consulta el folio de otro (7.2)|
|El identificador no existe|`404 Not Found`|`ReservaNoEncontradaException` (Guía 06)|
|Una regla del negocio lo impide|`409 Conflict`|Solapamiento (RN-01), capacidad (RN-02), transición inválida (RN-08)|

La distinción entre `400` y `409` es la que más se equivoca:

- **`400`** dice _"su mensaje está mal escrito"_: falta un campo, la fecha no tiene formato válido, la lista de ocupantes viene vacía. Lo detecta Bean Validation sobre el DTO.
- **`409`** dice _"su mensaje está bien escrito, pero el negocio no lo permite en este momento"_: el apartamento ya está vendido esas noches, la reserva ya está cancelada, el grupo excede la capacidad. Lo detecta el dominio.

> Esta es una **decisión del curso**, no la única posible: hay equipos que usan `422 Unprocessable Entity` para el segundo caso. Elija una convención, documéntela en el OpenAPI y manténgala en todo el proyecto. La Guía 08 implementa el manejador que traduce cada excepción a su código.

---

# 5. Parte 2: Diseño de DTOs

## 5.1 Por qué DTOs separados del dominio

1. **Estabilidad de la API.** Un cambio interno del dominio no rompe a quien consume la API.
2. **Protección del modelo.** `Reserva` tiene comportamiento y reglas; exponerla directamente invita a que el cliente construya un JSON con `estado: "FINALIZADA"` y se salte todo el ciclo de vida.
3. **Vistas distintas del mismo agregado.** El listado necesita seis campos; el detalle, veinte.
4. **Los tipos del dominio no viajan.** `CodigoReserva` es un `record` con validación de formato; hacia afuera es simplemente `"RES-2026-00042"`. `Dinero` no se serializa como objeto: viaja como un número entero de pesos.

## 5.2 Por qué records

Un DTO es un portador de datos inmutable, sin comportamiento. Eso lo hace un candidato natural para `record`:

```java
// Clase tradicional: mucho ruido
public class MiDTO {
    private String campo;
    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }
}

// Record: conciso, inmutable, con equals/hashCode/toString automáticos
public record MiDTO(String campo) {}
```

Jackson (serialización JSON) y Bean Validation soportan records completamente.

## 5.3 Request: crear una reserva

```java
package co.edu.uniquindio.sga.application.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear una reserva.
 * Mapea a: CrearReservaUseCase (Guía 06) → Reserva.crear(...)
 *
 * NO incluir: codigo, estado, valorTotal ni versión de la política.
 * El código lo genera el sistema, el estado nace PENDIENTE (7.5), y el valor
 * y la política se congelan al crear la reserva (RN-22). Nada de eso lo elige
 * el cliente.
 */
public record CrearReservaRequest(

    @NotBlank(message = "La identificación del apartamento es obligatoria")
    String identificacionApartamento,

    @NotNull(message = "La fecha de entrada es obligatoria")
    LocalDate fechaEntrada,

    @NotNull(message = "La fecha de salida es obligatoria")
    LocalDate fechaSalida,

    /** Opcional al crear; obligatoria para confirmar (RN-09, Guía 06 §4.2). */
    LocalTime horaEstimadaLlegada,

    @NotNull(message = "El canal de origen es obligatorio")
    CanalOrigen canalOrigen,

    @NotNull(message = "La reserva debe tener titular")
    @Valid
    OcupanteRequest titular,

    @NotEmpty(message = "La reserva debe tener al menos un ocupante")
    @Valid
    List<OcupanteRequest> ocupantes

) {}
```

```java
package co.edu.uniquindio.sga.application.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

/**
 * Ocupante que llega desde la API.
 *
 * NO incluir: edad ni "esFacturable". La edad se calcula, nunca se almacena
 * (3.2), y la condición de facturable la determina el dominio a la fecha de
 * entrada (RN-06). Si el cliente los enviara, el sistema tendría dos verdades.
 */
public record OcupanteRequest(

    @NotBlank(message = "El documento del ocupante es obligatorio")
    String documento,

    @NotBlank(message = "El nombre del ocupante es obligatorio")
    String nombre,

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento no puede ser futura")
    LocalDate fechaNacimiento

) {}
```

> **Lo que Bean Validation no puede hacer.** RN-03 (salida posterior a entrada) relaciona **dos campos**, y RN-04 (no reservar hacia el pasado) depende de la fecha actual. Ninguna anotación de campo las expresa bien, y aunque existiera una, seguirían siendo reglas del negocio: viven en `Estancia` y en `Reserva.crear(...)` desde la Guía 04. La validación en la interfaz es **adicional, nunca sustituta** (12.1). El DTO verifica que el mensaje esté bien escrito; el dominio verifica que lo pedido sea posible.

## 5.4 Request de acción: cancelar una reserva

```java
package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cancelar una reserva.
 * Mapea a: CancelarReservaUseCase (Guía 06)
 *
 * NO incluir: retención, devolución ni estado destino. La retención se calcula
 * con la versión de política congelada en la reserva (RN-13) y se registra como
 * cargo en el folio. El estado resultante lo determina el dominio.
 */
public record CancelarReservaRequest(

    @NotBlank(message = "El motivo de la cancelación es obligatorio")
    @Size(min = 5, max = 300, message = "El motivo debe tener entre 5 y 300 caracteres")
    String motivo

) {}
```

Compare este DTO con el que tendría un endpoint genérico de estado: aquí `motivo` es obligatorio **siempre**, porque el DTO sirve a una sola operación. En un `CambiarEstadoRequest` compartido por las seis transiciones, ningún campo podría marcarse obligatorio sin romper alguno de los casos, y la validación tendría que salir del contrato para vivir en el controller.

> **`confirmar` y `registrar-llegada` no necesitan DTO de entrada:** no reciben datos del cliente. Quién ejecuta la acción se obtiene del usuario autenticado (Guía 12), y la fecha actual la pone el servidor, nunca el cliente.

## 5.5 Response: detalle y resumen

```java
package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Vista completa de una reserva.
 * Usado en: GET /api/reservas/{codigo}
 */
public record ReservaDetalleResponse(
    String codigo,                          // "RES-2026-00042", no el objeto CodigoReserva
    EstadoReserva estado,
    CanalOrigen canalOrigen,
    ApartamentoResumenResponse apartamento, // resumen, no el agregado completo
    LocalDate fechaEntrada,
    LocalDate fechaSalida,
    int noches,
    LocalTime horaEstimadaLlegada,          // puede ser null hasta antes de confirmar
    OcupanteResponse titular,
    List<OcupanteResponse> ocupantes,
    int totalOcupantes,
    int ocupantesFacturables,
    long valorTotal,                        // pesos, sin decimales (3.4)
    String moneda,                          // "COP"
    String versionPoliticaCancelacion,      // la congelada en la reserva (RN-22)
    LocalDate fechaCreacion
) {}

/**
 * Vista breve de una reserva, para listados paginados.
 * Usado en: GET /api/reservas
 */
public record ReservaResumenResponse(
    String codigo,
    EstadoReserva estado,
    String identificacionApartamento,
    String nombreApartamento,
    LocalDate fechaEntrada,
    LocalDate fechaSalida,
    String titularNombre,
    int totalOcupantes,
    long valorTotal,
    CanalOrigen canalOrigen
) {}

/**
 * Ocupante tal como lo ve quien consume la API.
 * "facturable" lo calcula el dominio a la fecha de entrada (RN-06): es un
 * resultado, no un dato que alguien haya enviado.
 */
public record OcupanteResponse(
    String documento,
    String nombre,
    LocalDate fechaNacimiento,
    boolean facturable
) {}

// EstadoReserva y CanalOrigen NO necesitan DTO: son enum del dominio con un
// conjunto cerrado de valores. Se serializan como texto ("PENDIENTE", "PORTAL")
// y el cliente no necesita consultar un catálogo para conocerlos.
```

Dos decisiones que conviene defender en la sustentación:

- **`valorTotal` es un `long` de pesos**, no un `double`. El proyecto prohíbe `float` y `double` para dinero (3.4), y esa prohibición no se levanta al cruzar la frontera de la API: un `double` en el JSON reintroduce el error de redondeo que el dominio evitó.
- **El detalle no incluye el folio completo.** El folio tiene su propio endpoint. Un `GET /api/reservas/{codigo}` que arrastre todos los cargos y pagos obliga a calcular de más en cada consulta y mezcla dos preguntas distintas.

## 5.6 Response: cotización con desglose

El proyecto exige mostrar el **desglose noche por noche**: fecha, temporada, tarifa aplicada, ocupantes facturables y subtotal (7.4). El DTO lo refleja literalmente:

```java
package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/** Usado en: POST /api/cotizaciones */
public record CotizacionResponse(
    String identificacionApartamento,
    LocalDate fechaEntrada,
    LocalDate fechaSalida,
    int noches,
    int totalOcupantes,
    int ocupantesFacturables,
    List<NocheCotizadaResponse> desglose,
    long valorTotal,
    String moneda
) {}

public record NocheCotizadaResponse(
    LocalDate fecha,
    String temporada,                    // nombre de la temporada de esa noche
    long tarifaPorOcupanteFacturable,
    int ocupantesFacturables,
    long subtotal
) {}
```

El desglose no es un adorno: es lo que permite al huésped entender por qué una estancia que cruza dos temporadas no cuesta lo mismo todas las noches (RN-05). Si su equipo aplica descuentos o recargos propios (L-20), recuerde que el **redondeo se aplica al final del cargo, nunca noche por noche** (3.4).

## 5.7 Patrón de nomenclatura

|Tipo|Patrón|Ejemplo|
|---|---|---|
|Request (crear)|`Crear{Concepto}Request`|`CrearReservaRequest`|
|Request (acción)|`{Accion}{Concepto}Request`|`CancelarReservaRequest`|
|Response (detalle)|`{Concepto}DetalleResponse`|`ReservaDetalleResponse`|
|Response (lista)|`{Concepto}ResumenResponse`|`ReservaResumenResponse`|

Los nombres siguen siendo del negocio: `CrearReservaRequest`, no `CreateBookingRequest`. El sufijo técnico en inglés (`Request`, `Response`) se acepta porque es vocabulario del oficio, igual que `Repository` (Guía 03, sección 7).

## 5.8 Dónde viven los DTOs

```
src/main/java/co/edu/uniquindio/sga/
├── domain/                         ← Guías 04 y 05, intacto
│   ├── entity/
│   ├── valueobject/
│   ├── repository/
│   ├── service/
│   └── exception/
│
├── application/
│   ├── usecase/                    ← Guía 06
│   ├── exception/                  ← Guía 06
│   └── dto/                        ← NUEVO en esta guía
│       ├── request/
│       │   ├── CrearReservaRequest.java
│       │   ├── OcupanteRequest.java
│       │   ├── CancelarReservaRequest.java
│       │   └── CotizacionRequest.java
│       └── response/
│           ├── ReservaDetalleResponse.java
│           ├── ReservaResumenResponse.java
│           ├── OcupanteResponse.java
│           ├── ApartamentoResumenResponse.java
│           └── CotizacionResponse.java
│
└── infrastructure/                 ← Guía 08 en adelante
```

Dos precisiones sobre esa ubicación:

- **Los DTOs no son del dominio.** El paquete `domain` sigue sin conocerlos, y ninguno de ellos importa clases del dominio distintas de los `enum` cerrados (`EstadoReserva`, `CanalOrigen`).
- **Que el DTO viva en `application` no significa que el caso de uso lo reciba.** Los casos de uso de la Guía 06 siguen recibiendo tipos del dominio (`IdentificacionApartamento`, `Estancia`, `Ocupante`). La traducción DTO → dominio ocurre en el adaptador, con MapStruct, en la **Guía 08**.

## 5.9 Catálogo de DTOs pendientes

Los DTOs anteriores son el patrón completo: uno de creación, uno de acción, dos de respuesta y uno de consulta. El resto sigue el mismo molde y **es parte de la actividad**:

|DTO|Para|Punto a resolver|
|---|---|---|
|`RegistrarSalidaRequest`|`PUT /api/reservas/{codigo}/registrar-salida`|El cierre del folio es requisito salvo autorización del administrador (RN-17): ¿ese dato viaja en el DTO o es otro endpoint?|
|`ModificarReservaRequest`|`PUT /api/reservas/{codigo}`|Mismos campos que crear, menos el canal de origen, que nunca cambia. La diferencia de valor se registra como ajuste (RN-14) y el cliente no la envía|
|`RegistrarPagoRequest`|`POST /api/reservas/{codigo}/folio/pagos`|Medio de pago y fecha son obligatorios (RN-15). El saldo resultante no se envía: se calcula|
|`FolioResponse`|`GET /api/reservas/{codigo}/folio`|Cargos, pagos, saldo y si está cerrado. Recuerde que cargos y pagos no se editan ni se borran (RN-16)|
|`ApartamentoDetalleResponse`|`GET /api/apartamentos/{identificacion}`|Incluye capacidad, dotación, estado operativo e imágenes (7.3)|
|`PaginaResponse<T>`|Todos los listados|Paginación de **10 por página**, obligatoria en todo el proyecto (12.4)|

**Desafío opcional:** diseñe el contrato del **canal externo** (7.8 y 10.3.1). No es la misma API: tiene otro consumidor, se autentica con una credencial por canal y exige que el mismo mensaje recibido dos veces no cree dos reservas (RN-19). Piense qué campo del request permite cumplir esa regla y qué código de estado devuelve un conflicto (RN-18).

---

# 6. Parte 3: Especificación OpenAPI

> El archivo que se escribe a continuación es un documento de **diseño**: el plano de la API, escrito antes de implementarla. En la **Guía 08**, cuando existan los controllers, se configurará **Springdoc** para generar documentación interactiva desde las anotaciones del código.

## 6.1 Ubicación del archivo

```
sga-<nombre-del-alojamiento>/
├── docs/
│   └── api/
│       └── openapi.yaml    ← archivo de diseño de la API
├── src/
│   └── main/
└── build.gradle
```

`docs/api/` es una convención estándar para documentación técnica. El archivo no se compila: es un artefacto de diseño, y se versiona junto al código.

## 6.2 Estructura del archivo `openapi.yaml`

```yaml
openapi: 3.1.0
info:
  title: API SGA — Sistema de Gestión de Alojamiento
  description: |
    API RESTful para la gestión de un alojamiento turístico compuesto por
    apartamentos autónomos: disponibilidad, cotización, reservas, operación
    diaria y folio.

    **Dominio:** Gestión de alojamiento
    **Agregado principal:** Reserva
    **Unidad de venta:** el apartamento completo, por noche (F-01)
  version: 1.0.0
  contact:
    name: Programa de Ingeniería de Sistemas y Computación
    email: sistemas@uniquindio.edu.co

servers:
  - url: http://localhost:8080
    description: Servidor de desarrollo local

tags:
  - name: Disponibilidad
    description: Búsqueda de apartamentos disponibles y cotización de estancias
  - name: Reservas
    description: Ciclo de vida del agregado Reserva
  - name: Folios
    description: Cargos, pagos y saldo de cada reserva

paths:

  # ==================== DISPONIBILIDAD ====================

  /api/apartamentos/disponibles:
    get:
      tags: [Disponibilidad]
      summary: Buscar apartamentos disponibles
      description: |
        Devuelve los apartamentos que pueden venderse en todo el rango indicado:
        sin reservas activas que solapen (RN-01), sin bloqueos vigentes (RN-07),
        activos y con capacidad suficiente (RN-02).
      operationId: buscarApartamentosDisponibles
      parameters:
        - name: fechaEntrada
          in: query
          required: true
          schema: { type: string, format: date }
        - name: fechaSalida
          in: query
          required: true
          schema: { type: string, format: date }
        - name: personas
          in: query
          required: true
          description: Total de ocupantes, facturables y no facturables
          schema: { type: integer, minimum: 1 }
        - name: page
          in: query
          schema: { type: integer, default: 0 }
        - name: size
          in: query
          description: Tamaño de página. El proyecto fija 10 (12.4)
          schema: { type: integer, default: 10, maximum: 10 }
      responses:
        '200':
          description: Página de apartamentos disponibles
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaginaApartamentos'
        '400':
          description: Rango de fechas inválido

  /api/cotizaciones:
    post:
      tags: [Disponibilidad]
      summary: Cotizar una estancia
      description: |
        Calcula el valor de una estancia con su desglose noche por noche (7.4).
        Usa POST porque la composición del grupo incluye la fecha de nacimiento
        de cada ocupante, necesaria para determinar quiénes son facturables
        (RN-06). **No crea ningún recurso ni retiene noches:** responde 200.
      operationId: cotizarEstancia
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CotizacionRequest'
            example:
              identificacionApartamento: APT-301
              fechaEntrada: '2026-10-10'
              fechaSalida: '2026-10-12'
              ocupantes:
                - fechaNacimiento: '1990-05-12'
                - fechaNacimiento: '2019-03-02'
      responses:
        '200':
          description: Cotización calculada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CotizacionResponse'
        '404':
          description: El apartamento no existe

  # ==================== RESERVAS ====================

  /api/reservas:
    post:
      tags: [Reservas]
      summary: Crear una reserva
      description: |
        Crea una reserva en estado PENDIENTE (7.5), congela su valor y la
        versión vigente de la política de cancelación (RN-22) y abre su folio
        (F-08).
        Mapea a: `CrearReservaUseCase`
      operationId: crearReserva
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CrearReservaRequest'
            example:
              identificacionApartamento: APT-301
              fechaEntrada: '2026-10-10'
              fechaSalida: '2026-10-12'
              horaEstimadaLlegada: '15:00:00'
              canalOrigen: PORTAL
              titular:
                documento: '1094123456'
                nombre: Ana Ramírez
                fechaNacimiento: '1990-05-12'
              ocupantes:
                - documento: '1094123456'
                  nombre: Ana Ramírez
                  fechaNacimiento: '1990-05-12'
                - documento: 'TI1030998877'
                  nombre: Sofía Ramírez
                  fechaNacimiento: '2019-03-02'
      responses:
        '201':
          description: Reserva creada
          headers:
            Location:
              description: URI de la reserva creada
              schema:
                type: string
                example: /api/reservas/RES-2026-00042
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ReservaDetalleResponse'
        '400':
          description: Datos de entrada inválidos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: El apartamento no existe
        '409':
          description: |
            El negocio no permite la reserva: solapamiento con una reserva
            activa (RN-01), capacidad excedida (RN-02), bloqueo vigente (RN-07)
            o tiempo de preparación no respetado (RN-20)

    get:
      tags: [Reservas]
      summary: Listar reservas
      description: |
        Lista paginada, de la más reciente a la más antigua, con los filtros
        de 7.5. Un huésped solo puede listar las suyas (7.2).
      operationId: listarReservas
      parameters:
        - name: estado
          in: query
          schema:
            $ref: '#/components/schemas/EstadoReserva'
        - name: identificacionApartamento
          in: query
          schema: { type: string }
        - name: canalOrigen
          in: query
          schema:
            $ref: '#/components/schemas/CanalOrigen'
        - name: desde
          in: query
          description: Fecha de entrada mínima
          schema: { type: string, format: date }
        - name: hasta
          in: query
          description: Fecha de entrada máxima
          schema: { type: string, format: date }
        - name: page
          in: query
          schema: { type: integer, default: 0 }
        - name: size
          in: query
          schema: { type: integer, default: 10, maximum: 10 }
      responses:
        '200':
          description: Página de reservas
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PaginaReservas'

  /api/reservas/{codigo}:
    get:
      tags: [Reservas]
      summary: Consultar una reserva
      operationId: obtenerReserva
      parameters:
        - name: codigo
          in: path
          required: true
          description: Código de negocio de la reserva
          schema: { type: string, example: RES-2026-00042 }
      responses:
        '200':
          description: Detalle de la reserva
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ReservaDetalleResponse'
        '403':
          description: La reserva pertenece a otro titular (7.2)
        '404':
          description: No existe una reserva con ese código

  /api/reservas/{codigo}/confirmar:
    put:
      tags: [Reservas]
      summary: Confirmar una reserva
      description: |
        Mapea a: `ConfirmarReservaUseCase`

        **Precondiciones:**
        - La reserva debe estar en estado PENDIENTE (RN-08)
        - Debe tener registrada la hora estimada de llegada (RN-09)

        No requiere cuerpo: quién ejecuta la acción se obtiene del usuario
        autenticado.
      operationId: confirmarReserva
      parameters:
        - name: codigo
          in: path
          required: true
          schema: { type: string }
      responses:
        '200':
          description: Reserva confirmada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ReservaDetalleResponse'
        '404':
          description: No existe una reserva con ese código
        '409':
          description: Transición inválida (RN-08) o falta la hora estimada (RN-09)

  /api/reservas/{codigo}/cancelar:
    put:
      tags: [Reservas]
      summary: Cancelar una reserva
      description: |
        Mapea a: `CancelarReservaUseCase`

        Calcula la retención con la **versión de política congelada en la
        reserva** (RN-13), la registra en el folio y libera las noches de
        inmediato (RN-12).

        **Precondiciones:**
        - Estado PENDIENTE o CONFIRMADA, siempre antes del registro (7.5)
      operationId: cancelarReserva
      parameters:
        - name: codigo
          in: path
          required: true
          schema: { type: string }
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CancelarReservaRequest'
      responses:
        '200':
          description: Reserva cancelada
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ReservaDetalleResponse'
        '404':
          description: No existe una reserva con ese código
        '409':
          description: La reserva ya inició o está en un estado terminal (RN-08)

components:
  schemas:

    # ---------- Request ----------
    CrearReservaRequest:
      type: object
      required:
        - identificacionApartamento
        - fechaEntrada
        - fechaSalida
        - canalOrigen
        - titular
        - ocupantes
      properties:
        identificacionApartamento:
          type: string
          example: APT-301
        fechaEntrada:
          type: string
          format: date
        fechaSalida:
          type: string
          format: date
        horaEstimadaLlegada:
          type: string
          format: time
          description: Opcional al crear; obligatoria para confirmar (RN-09)
        canalOrigen:
          $ref: '#/components/schemas/CanalOrigen'
        titular:
          $ref: '#/components/schemas/OcupanteRequest'
        ocupantes:
          type: array
          minItems: 1
          items:
            $ref: '#/components/schemas/OcupanteRequest'

    OcupanteRequest:
      type: object
      required: [documento, nombre, fechaNacimiento]
      properties:
        documento:
          type: string
        nombre:
          type: string
        fechaNacimiento:
          type: string
          format: date
          description: Se envía la fecha, nunca la edad (3.2)

    CancelarReservaRequest:
      type: object
      required: [motivo]
      properties:
        motivo:
          type: string
          minLength: 5
          maxLength: 300

    CotizacionRequest:
      type: object
      required: [identificacionApartamento, fechaEntrada, fechaSalida, ocupantes]
      properties:
        identificacionApartamento:
          type: string
        fechaEntrada:
          type: string
          format: date
        fechaSalida:
          type: string
          format: date
        ocupantes:
          type: array
          minItems: 1
          items:
            type: object
            required: [fechaNacimiento]
            properties:
              fechaNacimiento:
                type: string
                format: date

    # ---------- Response ----------
    ReservaDetalleResponse:
      type: object
      properties:
        codigo:
          type: string
          example: RES-2026-00042
        estado:
          $ref: '#/components/schemas/EstadoReserva'
        canalOrigen:
          $ref: '#/components/schemas/CanalOrigen'
        apartamento:
          $ref: '#/components/schemas/ApartamentoResumenResponse'
        fechaEntrada:
          type: string
          format: date
        fechaSalida:
          type: string
          format: date
        noches:
          type: integer
        horaEstimadaLlegada:
          type: string
          format: time
        titular:
          $ref: '#/components/schemas/OcupanteResponse'
        ocupantes:
          type: array
          items:
            $ref: '#/components/schemas/OcupanteResponse'
        totalOcupantes:
          type: integer
        ocupantesFacturables:
          type: integer
        valorTotal:
          type: integer
          format: int64
          description: Pesos colombianos, sin decimales (3.4)
        moneda:
          type: string
          example: COP
        versionPoliticaCancelacion:
          type: string
        fechaCreacion:
          type: string
          format: date

    ReservaResumenResponse:
      type: object
      properties:
        codigo:
          type: string
        estado:
          $ref: '#/components/schemas/EstadoReserva'
        identificacionApartamento:
          type: string
        nombreApartamento:
          type: string
        fechaEntrada:
          type: string
          format: date
        fechaSalida:
          type: string
          format: date
        titularNombre:
          type: string
        totalOcupantes:
          type: integer
        valorTotal:
          type: integer
          format: int64
        canalOrigen:
          $ref: '#/components/schemas/CanalOrigen'

    OcupanteResponse:
      type: object
      properties:
        documento:
          type: string
        nombre:
          type: string
        fechaNacimiento:
          type: string
          format: date
        facturable:
          type: boolean
          description: Calculado a la fecha de entrada (RN-06)

    ApartamentoResumenResponse:
      type: object
      properties:
        identificacion:
          type: string
          example: APT-301
        nombre:
          type: string
        dormitorios:
          type: integer
        capacidad:
          type: integer
        valorEstimado:
          type: integer
          format: int64

    CotizacionResponse:
      type: object
      properties:
        identificacionApartamento:
          type: string
        fechaEntrada:
          type: string
          format: date
        fechaSalida:
          type: string
          format: date
        noches:
          type: integer
        totalOcupantes:
          type: integer
        ocupantesFacturables:
          type: integer
        desglose:
          type: array
          items:
            $ref: '#/components/schemas/NocheCotizadaResponse'
        valorTotal:
          type: integer
          format: int64
        moneda:
          type: string

    NocheCotizadaResponse:
      type: object
      properties:
        fecha:
          type: string
          format: date
        temporada:
          type: string
        tarifaPorOcupanteFacturable:
          type: integer
          format: int64
        ocupantesFacturables:
          type: integer
        subtotal:
          type: integer
          format: int64

    PaginaReservas:
      type: object
      properties:
        contenido:
          type: array
          items:
            $ref: '#/components/schemas/ReservaResumenResponse'
        paginaActual:
          type: integer
        totalPaginas:
          type: integer
        totalElementos:
          type: integer
          format: int64
        tamanoPagina:
          type: integer

    PaginaApartamentos:
      type: object
      properties:
        contenido:
          type: array
          items:
            $ref: '#/components/schemas/ApartamentoResumenResponse'
        paginaActual:
          type: integer
        totalPaginas:
          type: integer
        totalElementos:
          type: integer
          format: int64
        tamanoPagina:
          type: integer

    # ---------- Enumeraciones del dominio ----------
    EstadoReserva:
      type: string
      enum: [PENDIENTE, CONFIRMADA, EN_CURSO, FINALIZADA, CANCELADA, NO_SHOW]

    CanalOrigen:
      type: string
      enum: [PORTAL, DIRECTO, EXTERNO]

    EstadoOperativo:
      type: string
      enum: [PREPARADO, OCUPADO, PENDIENTE_PREPARACION, EN_PREPARACION, FUERA_DE_SERVICIO]
      # Todavía no lo usa ningún endpoint de este archivo: lo usarán los de
      # apartamentos y operación diaria, que son parte de la actividad.

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
          description: Mensaje con significado de negocio, nunca una traza técnica (12.4)
          example: El apartamento no está disponible en las fechas solicitadas
        path:
          type: string

  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

security:
  - bearerAuth: []
```

## 6.3 Validación en Swagger Editor

Para comprobar que la especificación es válida y ver cómo queda la API:

1. Abra [Swagger Editor](https://editor.swagger.io/).
2. Borre el contenido de ejemplo del panel izquierdo.
3. Pegue el contenido completo de su `docs/api/openapi.yaml`.
4. Revise el panel derecho.

Qué debe ver:

- los endpoints agrupados por **tags** (Disponibilidad, Reservas, Folios),
- cada endpoint expandible, con su método, parámetros y esquemas,
- los DTOs en la sección **Schemas**, al final,
- **cero errores** en el panel izquierdo.

> Si aparecen errores de sintaxis, casi siempre es la indentación. YAML es sensible a los espacios: use siempre 2 espacios, nunca tabulaciones.

## 6.4 Aplicación al proyecto final

Esta especificación es el contrato que su equipo va a implementar en el **Corte 2**, donde se exige disponibilidad, cotización y reservas **operando por API**. Dos consecuencias prácticas:

- Si su equipo definió reglas propias (L-20) que el cliente deba poder disparar o ver —un depósito reembolsable, una estancia mínima, un recargo por llegada nocturna—, esas reglas necesitan aparecer en el contrato: como un campo del DTO, como un endpoint nuevo o como un código `409` documentado.
- El frontend (Guías 14 en adelante) consumirá exactamente estos endpoints. Un contrato ambiguo hoy es un mes de correcciones en noviembre.

---

# 7. Parte 4: Actividad

## Objetivo

Diseñar y documentar la API del SGA que expone los casos de uso construidos en la Guía 06.

## Instrucciones

1. **Complete la tabla de mapeo** de la sección 4.2 con **todas** las operaciones del dominio de su equipo, incluidas las que surgen de sus reglas propias (L-20). Para cada una: método HTTP, endpoint y caso de uso que la atiende.
    
2. **Cree el paquete `application.dto`** con sus subpaquetes `request` y `response`.
    
3. **Implemente los DTOs mostrados en esta guía** (`CrearReservaRequest`, `OcupanteRequest`, `CancelarReservaRequest`, `ReservaDetalleResponse`, `ReservaResumenResponse`, `OcupanteResponse`, `CotizacionResponse`), adaptando los nombres a los conceptos de su alojamiento.
    
4. **Construya, sin ejemplo previo,** los DTOs de la tabla 5.9.
    
5. **Escriba el archivo `docs/api/openapi.yaml`** con todos los endpoints de su tabla del punto 1, no solo con los seis de esta guía.
    
6. **Valide el archivo en Swagger Editor** hasta que no quede ningún error, y adjunte una captura del resultado al entregable.
    
7. **Verifique que ningún DTO exponga tipos del dominio**: nada de `CodigoReserva`, `Estancia`, `Dinero` ni `Apartamento` en una firma de record. Los `enum` cerrados del dominio sí pueden viajar.
    
8. **El dominio y los casos de uso no se modifican** en esta guía. Si al diseñar un endpoint descubre que falta una operación, agréguela donde corresponde —dominio o caso de uso—, nunca en el DTO.
    

---

# 8. Precauciones y recomendaciones

1. **DTOs ≠ entidades.** Nunca exponga una entidad del dominio directamente en la API.
2. **El cliente no elige el estado.** Ningún request contiene `estado`, `nuevoEstado` ni `valorTotal`: eso lo determina el dominio.
3. **La validación del DTO no reemplaza la regla.** Bean Validation verifica la forma del mensaje; el dominio verifica lo que el negocio permite (12.1).
4. **Lenguaje ubicuo también en las URLs.** `/api/reservas`, no `/api/bookings` (F-11).
5. **Dinero sin decimales flotantes.** `long` o `BigDecimal` en el DTO, entero en el JSON (3.4).
6. **Fechas sin hora donde el negocio no tiene hora.** `format: date` para la estancia; `format: time` solo para la hora estimada de llegada.
7. **Paginación de 10 en todos los listados** (12.4). No la deje en 20 porque es el valor por defecto de Spring.
8. **Una sola convención de errores.** Decida entre `409` y `422` para las reglas violadas, documéntela y no la mezcle.
9. **Versione la API desde el inicio si va a hacerlo.** `/api/v1/reservas` es una decisión que casi no cuesta hoy y es cara después.

---

# 9. Verificación

Antes de cerrar la guía, confirme:

|#|Criterio|
|---|---|
|1|Existe la tabla de mapeo completa: cada caso de uso tiene método HTTP y endpoint|
|2|Los recursos usan el lenguaje ubicuo del proyecto y la identidad del negocio en la URL|
|3|No existe ningún endpoint genérico de cambio de estado|
|4|No existe ningún `DELETE` sin una regla que lo justifique|
|5|Existe `application/dto/request` y `application/dto/response`, con los DTOs de esta guía implementados como `record`|
|6|Ningún DTO expone tipos del dominio distintos de `enum` cerrados|
|7|Ningún request contiene estado, valor, retención ni edad: todo eso lo calcula el sistema|
|8|Los `Request` tienen anotaciones de Bean Validation coherentes con el proyecto|
|9|Existe `docs/api/openapi.yaml` con todos los endpoints diseñados|
|10|El archivo se valida en Swagger Editor **sin errores**|
|11|Los códigos de estado siguen la convención de la sección 4.6 y están documentados endpoint por endpoint|
|12|Todos los listados declaran paginación de 10 por página|
|13|`./gradlew build` termina sin errores|
|14|El trabajo está versionado, con commits de todos los integrantes|

---

# 10. Evaluación o resultado

Al finalizar esta guía, el estudiante debe:

1. Explicar la diferencia entre Domain-First y Contract-First, y por qué el curso usa el primero.
2. Mapear cualquier operación del dominio a un endpoint, justificando el método HTTP elegido.
3. Justificar por qué la API no tiene un endpoint genérico de cambio de estado.
4. Diseñar DTOs que no filtren el dominio ni permitan al cliente decidir lo que decide el negocio.
5. Distinguir cuándo un error es `400`, cuándo `404` y cuándo `409`.
6. Tener una especificación OpenAPI válida que describa la API completa de su alojamiento.

**Entregable:** enlace al repositorio con el paquete `application.dto` implementado y el archivo `docs/api/openapi.yaml`, más la captura de la validación en Swagger Editor.

---

# 11. Próxima actividad

En la **Guía 08: Controllers REST como Adaptadores** aprenderemos a:

- implementar los controllers que reciben estos DTOs y delegan en los casos de uso,
- traducir DTO ↔ dominio con **MapStruct**,
- manejar los errores de forma centralizada, traduciendo cada excepción a su código HTTP,
- generar la documentación interactiva con **Springdoc** y Swagger UI.

> El contrato que diseñó hoy es el que tendrá que cumplir mañana. Si un endpoint le costó documentar, probablemente no era una operación del negocio: revise si la intención estaba bien definida antes de implementarlo.

**Completar**

1. Los criterios de verificación de la sección 9.
2. Los DTOs del catálogo 5.9 y los endpoints faltantes del `openapi.yaml`.

**Leer**

1. Richardson, L., & Ruby, S. (2007). _RESTful Web Services_, capítulos 4 y 5: recursos y métodos uniformes.
2. Documentación de **Springdoc OpenAPI**: [springdoc.org](https://springdoc.org/).

**Investigar**

1. **MapStruct**: cómo se declara un mapper entre un DTO y un objeto del dominio.
2. **Bean Validation**: qué son las validaciones a nivel de clase y por qué permiten expresar reglas entre dos campos.
3. **Idempotencia**: por qué `PUT` debería poder repetirse sin efectos adicionales, y qué implica eso para RN-19 en el canal externo.

> Pregunta para pensar antes de la próxima clase: si un huésped envía dos veces la misma petición `POST /api/reservas` porque el navegador se colgó, ¿cuántas reservas debería tener el alojamiento al final? El proyecto ya fija la respuesta para el canal externo (RN-19). ¿Debería valer también para el portal?

---

# 12. Referencias bibliográficas

1. Fielding, R. T. (2000). _Architectural Styles and the Design of Network-based Software Architectures_. Tesis doctoral, University of California, Irvine.
2. OpenAPI Initiative. (2021). _OpenAPI Specification 3.1.0_. https://spec.openapi.org/oas/v3.1.0
3. Richardson, L., & Ruby, S. (2007). _RESTful Web Services_. O'Reilly Media.
4. Evans, E. (2003). _Domain-Driven Design: Tackling Complexity in the Heart of Software_. Addison-Wesley.
5. Vernon, V. (2013). _Implementing Domain-Driven Design_. Addison-Wesley.
6. Springdoc. (2025). _Springdoc OpenAPI Documentation_. https://springdoc.org/
7. Martinez Ceballos, J. C. (2026). _Proyecto Final — SGA: Sistema de Gestión de Alojamiento_, versión 2.0. Universidad del Quindío.

---

> **Recuerda:** una API que deja al cliente elegir el estado destino no expone un dominio; expone una base de datos con modales.