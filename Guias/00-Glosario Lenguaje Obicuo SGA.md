# Glosario del Lenguaje Ubicuo — SGA: Sistema de Gestión de Alojamiento

**Curso:** Programación Avanzada — Universidad del Quindío **Docente:** Jhan Carlos Martinez Ceballos **Proyecto:** SGA — Sistema de Gestión de Alojamiento

> Este glosario es la **referencia oficial** del lenguaje del dominio del proyecto. Los nombres que aparezcan en el modelo, el código, la API, la documentación y la sustentación deben salir de aquí (elemento fijo **F-11**).
> 
> Cada equipo puede **agregar** términos propios de su variante de alojamiento. **No puede renombrar ni eliminar** los de este documento. Los términos agregados se registran en la sección final.

---

## Conceptos centrales

### Alojamiento

**Definición:** El negocio completo: el conjunto de apartamentos que están bajo una misma administración, con sus horarios, normas, servicios y parámetros de operación.

**Sinónimos aceptados:** Aparthotel **No usar:** Hotel, Propiedad, Establecimiento, Tenant

**Precisión importante:** el sistema administra **un único alojamiento** (F-13). No es un _marketplace_ ni una plataforma multipropietario. Cuando el código habla del alojamiento, habla de un único registro de configuración.

---

### Apartamento

**Definición:** La unidad vendible. Vivienda autónoma e identificada, con sus propios dormitorios, cocina, baño y zona social, que se entrega **en exclusiva** a un grupo de huéspedes.

**Sinónimos aceptados:** Unidad
**No usar:** Habitación, Cuarto, Cama, Room, Suite

**Regla que sostiene el término:** la unidad de venta es el apartamento completo (**F-01**). Los dormitorios no se venden por separado. Este es el error de modelado más costoso que puede cometer un equipo, porque contamina el cálculo de disponibilidad, el de tarifas y el de capacidad.

**Ejemplo de uso en código:**

```java
Apartamento apartamento = new Apartamento(
    new IdentificacionApartamento("APT-301"), "Balcón del Cocora", 2, 4);
```

---

### Dormitorio

**Definición:** Espacio para dormir dentro de un apartamento.

**No usar:** Habitación reservable, Room

**Naturaleza:** es un **atributo descriptivo** del apartamento, útil para que el huésped decida. Nunca es reservable ni facturable por separado.

---

### Capacidad

**Definición:** Número máximo de personas que admite un apartamento.

**No usar:** Aforo máximo sugerido, Cupo recomendado

**Naturaleza:** es un **tope rígido, sin excepciones** (**F-03**, **RN-02**). Todos los ocupantes cuentan para la capacidad, sean facturables o no.

**Ejemplo de uso en código:**

```java
if (!apartamento.admite(ocupantes.size())) {
    throw new ReglaDominioException(
        "El número de ocupantes excede la capacidad del apartamento");
}
```

---

### Noche

**Definición:** La unidad mínima de venta. Una noche corresponde a la ocupación de un apartamento durante una fecha del calendario.

**No usar:** Día, Jornada

**Regla que sostiene el término:** se vende la **noche-apartamento**. Del 10 al 12 de diciembre son **dos** noches —la del 10 y la del 11—, porque la noche de la fecha de salida no se ocupa ni se cobra.

---

### Estancia

**Definición:** Rango continuo de noches en que un apartamento queda ocupado por una reserva. Se expresa con dos fechas sin hora: **fecha de entrada** y **fecha de salida**.

**Sinónimos aceptados:** Rango de la reserva 
**No usar:** Periodo, Fechas, Rango de días, Stay

**Naturaleza:** es un **objeto de valor**, no una entidad. No tiene identidad propia; si cambian sus fechas, es otra estancia. Lo que tiene identidad es la reserva que la contiene.

**Reglas que encapsula:**

- El intervalo es cerrado en la entrada y abierto en la salida: `[entrada, salida)`.
- Toda estancia tiene al menos una noche (**RN-03**).
- Dos estancias **se solapan** si comparten al menos una noche: `entrada_A < salida_B` y `entrada_B < salida_A`.

**Ejemplo de uso en código:**

```java
Estancia estancia = new Estancia(
    LocalDate.of(2026, 12, 10), LocalDate.of(2026, 12, 12));

estancia.noches();                      // 2
estancia.seSolapaCon(otraEstancia);     // regla RN-01
```

---

### Reserva

**Definición:** Compromiso de ocupar un apartamento durante una estancia, para un conjunto definido de ocupantes.

**Sinónimos aceptados:** — 
**No usar:** Booking, Orden, Pedido, Ticket, Solicitud, Registro

**Naturaleza:** es la **entidad central** del sistema. Tiene identidad propia (su código), un ciclo de vida definido, un valor congelado y un folio asociado.

**Ejemplo de uso en código:**

```java
Reserva reserva = Reserva.crear(
    codigo, apartamento, estancia, titular, ocupantes,
    CanalOrigen.PORTAL, fechaActual);
```

---

### Código de reserva

**Definición:** Identificador único e inmutable de una reserva.

**Formato:** `RES-YYYY-NNNNN` **Ejemplo:** `RES-2026-00042`

**Reglas:**

- `YYYY`: año de creación.
- `NNNNN`: secuencia incremental de cinco dígitos.
- No cambia nunca. Es la identidad de la entidad y la base de su `equals`.

---

### Titular

**Definición:** Persona responsable de la reserva y de su pago.

**Sinónimos aceptados:** Responsable de la reserva **No usar:** Cliente, Comprador, Owner

**Precisiones:**

- El titular es siempre un **ocupante facturable** de la reserva.
- Es un concepto **del negocio**: existe aunque nunca use el sistema. Una reserva creada por recepción o por un canal externo tiene titular sin cuenta de usuario.

---

### Ocupante

**Definición:** Persona incluida en la reserva, de la que se registra su **fecha de nacimiento**.

**No usar:** Huésped (ese es el rol de acceso), Persona, Guest

**Precisiones:**

- De cada ocupante se registra la fecha de nacimiento, **nunca la edad**: la edad se calcula, porque de lo contrario el dato envejece solo.
- Todos los ocupantes cuentan para la capacidad.

**Ejemplo de uso en código:**

```java
ocupante.edadA(estancia.fechaEntrada());
ocupante.esFacturableEn(estancia, umbralEdadFacturable);
```

---

### Ocupante facturable

**Definición:** Ocupante que, **a la fecha de entrada de la estancia**, alcanza o supera el umbral de edad configurado. Solo estos generan cargo.

**No usar:** Adulto, Persona que paga, Pax

**Regla que sostiene el término (RN-06):** capacidad y facturación son **dos cuentas distintas sobre las mismas personas**. Un ocupante que no es facturable ocupa cupo pero no genera cargo. Quien cumple años durante la estancia **no cambia de condición a mitad de camino**: la condición se evalúa una sola vez, a la fecha de entrada.

---

### Umbral de edad facturable

**Definición:** Edad a partir de la cual un ocupante genera cargo.

**Naturaleza:** es **configuración del alojamiento** (**L-09**), no una constante del código. Cada equipo define su valor y lo justifica en la Ficha del Alojamiento.

**Criterio evaluable:** si para cambiar este valor hay que recompilar, el diseño está mal.

---

### Temporada

**Definición:** Periodo del calendario con tarifas propias.

**No usar:** Season, Periodo tarifario, Rango de precios

**Reglas:**

- Las temporadas **no pueden solaparse entre sí**.
- Una misma estancia puede cruzar varias temporadas y se liquida **noche por noche**.

---

### Temporada base

**Definición:** Temporada que cubre todas las fechas no asignadas a otra temporada. Es **obligatoria** (**F-05**).

**Para qué existe:** garantiza que ninguna fecha reservable quede sin temporada, y por tanto sin tarifa.

---

### Tarifa

**Definición:** Valor **por ocupante facturable, por noche**, para un apartamento en una temporada.

**No usar:** Precio, Precio por noche, Precio del apartamento

**Precisión que evita el error más común:** la tarifa **no** es el valor de la noche del apartamento. Es el valor de una persona facturable durante una noche. El valor de la noche depende de cuántas personas facturables lleguen.

**Regla:** todo apartamento activo debe tener tarifa en **todas** las temporadas.

---

### Cotización

**Definición:** Cálculo del valor de una estancia antes de reservar, con su desglose noche por noche: fecha, temporada, tarifa aplicada, ocupantes facturables y subtotal.

**No usar:** Presupuesto, Quote, Simulación de precio

**Regla que la define (RN-05):** el valor de la estancia es la suma, noche por noche, de la tarifa vigente del apartamento en la temporada de esa noche, multiplicada por el número de ocupantes facturables.

---

### Disponibilidad

**Definición:** Resultado del cálculo que responde a la pregunta **"¿puedo vender estas noches?"** para un apartamento y un rango de fechas.

**No usar:** Libre, Vacante, Desocupado, Availability

**Un apartamento está disponible para un rango si, en todas las noches de ese rango:**

1. no tiene reservas activas que solapen,
2. no tiene bloqueos que solapen,
3. está activo,
4. su capacidad alcanza para el grupo,
5. y se respeta el tiempo de preparación respecto de la estancia anterior.

**Distinción obligatoria:** disponibilidad, estado operativo y bloqueo son **tres conceptos distintos**. Confundirlos es un error de modelado, no de redacción.

|Concepto|Naturaleza|Responde a|
|---|---|---|
|**Disponibilidad**|Cálculo sobre un rango de fechas|¿Puedo vender estas noches?|
|**Estado operativo**|Condición física presente|¿Puedo entregar este apartamento ahora?|
|**Bloqueo**|Registro con rango y motivo|¿Hay una decisión administrativa que lo impide?|

---

### Estado operativo

**Definición:** Condición física presente del apartamento.

**Valores:** `PREPARADO`, `OCUPADO`, `PENDIENTE_PREPARACION`, `EN_PREPARACION`, `FUERA_DE_SERVICIO`

**Transiciones permitidas:**

```
PREPARADO → OCUPADO                              (registro del grupo)
OCUPADO → PENDIENTE_PREPARACION                  (salida del grupo)
PENDIENTE_PREPARACION → EN_PREPARACION → PREPARADO   (personal de servicio)
cualquier estado no ocupado → FUERA_DE_SERVICIO  (solo administrador)
```

**Regla (RN-11):** un apartamento solo puede recibir un grupo si su estado operativo es `PREPARADO`.

**En código:** `enum EstadoOperativo`, con el método `permiteRegistro()`.

---

### Bloqueo

**Definición:** Registro administrativo, con rango de fechas y motivo, que impide vender un apartamento durante esas noches.

**No usar:** Cierre, Inhabilitación, Mantenimiento (ese es un _motivo_, no el concepto)

**Reglas:**

- No puede registrarse un bloqueo sobre noches que ya tengan reservas activas.
- Un apartamento bloqueado no aparece como disponible (**RN-07**).
- Un bloqueo **no** cambia por sí solo el estado operativo, ni al revés.

---

### Tiempo de preparación

**Definición:** Horas requeridas entre la salida de un grupo y la entrada del siguiente.

**No usar:** Limpieza, Turnover, Aseo

**Regla (RN-20):** se aplica entre la hora de salida y la hora de entrada del mismo día. Si el tiempo configurado excede la ventana entre ambas horas, el apartamento **no puede recibir una entrada el mismo día de una salida**, y esa noche queda fuera de la disponibilidad.

---

### Registro

**Definición:** Momento en que el grupo toma posesión del apartamento.

**Equivalente en inglés:** _check-in_ **No usar en código:** Check-in, CheckIn, ingreso

**Precondiciones (RN-10, RN-11):** reserva en `CONFIRMADA`, fecha de entrada alcanzada y apartamento en estado `PREPARADO`.

**Efectos:** la reserva pasa a `EN_CURSO` y el apartamento a `OCUPADO`.

---

### Salida

**Definición:** Momento en que el apartamento se libera y queda pendiente de preparación.

**Equivalente en inglés:** _check-out_ **No usar en código:** Check-out, CheckOut, egreso

**Efectos:** la reserva pasa a `FINALIZADA` y el apartamento a `PENDIENTE_PREPARACION`. El cierre del folio es requisito, salvo autorización del administrador.

**Precisión:** la **salida anticipada** de un grupo ya registrado **no es una cancelación**. Se maneja como salida y la política de cancelación no aplica.

---

### No-show

**Definición:** Situación en que el titular no se presenta antes de la hora límite del día de entrada.

**Sinónimos aceptados:** — **No usar:** Plantón, Ausencia, Cancelación tácita

**Efectos:** se aplica la consecuencia definida en la política congelada y se libera la disponibilidad de inmediato.

---

### Política de cancelación

**Definición:** Regla única del alojamiento que define retenciones y devoluciones según la antelación con que se cancele, y la consecuencia del no-show.

**Naturaleza:** es **versionada**. Al actualizarla se conserva la versión anterior, y cada reserva queda ligada a la versión vigente **al momento de su creación**.

**Regla (RN-13):** la retención se determina por la **versión congelada en la reserva**, no por la vigente al momento del hecho.

---

### Congelamiento

**Definición:** Acción de fijar, al crear la reserva, dos cosas que no volverán a cambiar solas: el **valor calculado** con su desglose noche por noche, y la **versión de la política de cancelación** vigente en ese momento.

**No usar:** Snapshot, Foto, Copia

**Regla (RN-22):** un cambio posterior de tarifas o de política **no altera** las reservas ya creadas. Solo una modificación explícita de la reserva recalcula el valor, y la diferencia se registra como un ajuste en el folio.

---

### Folio

**Definición:** La cuenta de la reserva: acumula cargos y pagos, y determina el saldo.

**Sinónimos aceptados:** Cuenta de la reserva **No usar:** Factura, Cuenta de cobro, Invoice, Bill

**Precisiones:**

- Un folio pertenece a **una sola reserva**.
- Se abre **al crear la reserva**, con el cargo de alojamiento ya calculado. Eso permite registrar el anticipo antes de la llegada.
- No puede cerrarse con saldo distinto de cero sin **autorización explícita registrada** del administrador (**RN-17**).

---

### Cargo

**Definición:** Concepto que suma al folio.

**Tipos:** alojamiento, servicio adicional, ajuste por modificación, penalidad por cancelación o no-show.

**No usar:** Ítem, Línea, Concepto de cobro

---

### Pago

**Definición:** Abono registrado contra un folio, con su **medio** y su **fecha**.

**No usar:** Abono a cuenta, Transacción, Payment

**Precisiones:**

- Un mismo folio admite pagos parciales y medios distintos.
- **El sistema registra movimientos de dinero; no los ejecuta.** Una devolución se calcula y se deja registrada; el desembolso ocurre fuera del sistema.

---

### Saldo

**Definición:** Diferencia entre la suma de cargos y la suma de pagos de un folio.

**No usar:** Deuda, Balance, Total pendiente

**Valores posibles:** positivo (el huésped debe), cero, o negativo (saldo a favor del huésped).

---

### Canal

**Definición:** Origen de la reserva.

**Valores:** `PORTAL` (el huésped reserva por sí mismo), `DIRECTO` (la recepción registra una reserva recibida por teléfono, mensajería o presencialmente), `EXTERNO` (una plataforma de terceros la envía por integración).

**No usar:** Fuente, Origen web, Source

**Importancia:** los tres canales venden sobre **el mismo inventario físico** (**F-10**). El canal es inmutable: una reserva no cambia de canal después de creada.

**En código:** `enum CanalOrigen`, con el método `exigeIdentificadorExterno()`.

---

### Conflicto de canal

**Definición:** Reserva externa rechazada por colisionar con una reserva vigente, registrada para revisión del administrador.

**No usar:** Error de integración, Choque, Overbooking

**Regla (RN-18):** una reserva externa en conflicto **se rechaza y se registra; nunca sobrescribe** la reserva vigente.

---

### Novedad

**Definición:** Reporte de un daño, faltante o situación en un apartamento, con fecha, autor, descripción y gravedad.

**No usar:** Incidencia, Reporte, Issue, Ticket

---

### Dinero

**Definición:** Valor monetario en pesos colombianos, sin decimales.

**Reglas (definición 3.4 del proyecto):**

- Moneda única: **COP**.
- Se representa con un tipo de **precisión exacta**. **Prohibido `float` y `double`.**
- El redondeo al peso más cercano se aplica **al final** del cálculo de cada cargo, nunca noche por noche.
- Admite valores negativos: los ajustes por modificación y los saldos a favor lo son.

---

## Ciclo de vida de la reserva

**Definición:** Secuencia de estados por los que transita una reserva desde su creación hasta su terminación. Es **el mismo para todos los equipos** (**F-06**).

|Estado|Significado|¿Retiene disponibilidad?|
|---|---|---|
|`PENDIENTE`|Creada, aún no confirmada. Vence al cumplirse el plazo de confirmación.|Sí|
|`CONFIRMADA`|En firme.|Sí|
|`EN_CURSO`|El grupo realizó el registro y ocupa el apartamento.|Sí|
|`FINALIZADA`|El grupo salió.|No|
|`CANCELADA`|Terminada antes del registro.|No|
|`NO_SHOW`|El titular no se presentó antes de la hora límite.|No|

**Transiciones válidas.** Toda transición no listada debe ser **rechazada por el dominio** (**RN-08**).

```
PENDIENTE  → CONFIRMADA     (confirmación, con anticipo si el equipo lo exige)
PENDIENTE  → CANCELADA      (cancelación o vencimiento del plazo de confirmación)
CONFIRMADA → EN_CURSO       (registro del grupo)
CONFIRMADA → CANCELADA      (cancelación)
CONFIRMADA → NO_SHOW        (vencida la hora límite del día de entrada)
EN_CURSO   → FINALIZADA     (salida del grupo)
```

**Reserva activa:** la que está en `PENDIENTE`, `CONFIRMADA` o `EN_CURSO`. **Estados terminales:** `FINALIZADA`, `CANCELADA` y `NO_SHOW`.

---

## Verbos del dominio

Los nombres de los métodos salen de esta lista. Si una operación del sistema no se puede nombrar con uno de estos verbos, probablemente el concepto está mal ubicado.

|Verbo|Significado en el negocio|Sujeto que lo ejecuta|
|---|---|---|
|**Cotizar**|Calcular el valor de una estancia antes de reservar, con desglose|Huésped o recepcionista|
|**Reservar**|Crear el compromiso de ocupar un apartamento. La reserva nace `PENDIENTE`|Huésped, recepcionista o canal externo|
|**Confirmar**|Dejar la reserva en firme|Recepcionista o administrador|
|**Modificar**|Cambiar fechas, ocupantes o apartamento de una reserva no iniciada. Revalida todo y genera un ajuste|Recepcionista o administrador|
|**Cancelar**|Terminar la reserva antes del registro, aplicando la política congelada|Titular, recepcionista o administrador|
|**Declarar no-show**|Registrar que el titular no llegó antes de la hora límite|Recepcionista o administrador|
|**Registrar**|Entregar el apartamento al grupo (_check-in_)|Recepcionista|
|**Registrar la salida**|Liberar el apartamento (_check-out_)|Recepcionista|
|**Bloquear**|Impedir la venta de un apartamento en un rango, con motivo|Administrador|
|**Preparar**|Llevar el apartamento de `PENDIENTE_PREPARACION` a `PREPARADO`|Personal de servicio|
|**Liquidar**|Calcular el valor noche por noche de una estancia|Sistema|
|**Cargar**|Agregar un cargo al folio|Recepcionista o sistema|
|**Registrar un pago**|Abonar contra el folio, con medio y fecha|Recepcionista|
|**Cerrar el folio**|Dar por terminada la cuenta de la reserva|Recepcionista o administrador|
|**Reportar una novedad**|Dejar constancia de un daño o situación|Personal de servicio o recepcionista|

---

## Roles

Son conceptos de **acceso**, no del negocio. Un titular puede existir sin ser usuario.

### Huésped

Usuario que busca, cotiza, reserva por el canal **portal**, cancela sus propias reservas y consulta sus folios. **Nunca** accede a reservas, folios ni datos de otros huéspedes.

### Recepcionista

Usuario que consulta disponibilidad, crea reservas por el canal **directo** a nombre de un titular que puede no tener cuenta, modifica reservas no iniciadas, confirma, cancela, declara no-show, registra llegadas y salidas, y mueve el folio.

### Administrador

Todas las acciones del recepcionista, más la configuración del alojamiento, los apartamentos, los bloqueos, las temporadas, las tarifas, la política de cancelación, los canales, los usuarios y los reportes. Es el único que puede autorizar el cierre de un folio con saldo pendiente.

### Personal de servicio

Usuario que consulta los apartamentos pendientes de preparación, mueve el estado operativo entre `PENDIENTE_PREPARACION`, `EN_PREPARACION` y `PREPARADO`, y reporta novedades. **No puede** declarar `FUERA_DE_SERVICIO` ni registrar bloqueos: eso es decisión administrativa.

---

### Persona y usuario: una distinción que hay que sostener

|Concepto|Naturaleza|Existe sin el sistema|
|---|---|---|
|**Titular**, **ocupante**|Conceptos del negocio|Sí|
|**Usuario**|Concepto de acceso: credenciales y rol|No|

**Consecuencia de diseño:** el dominio **no depende** del concepto de usuario. La autenticación pertenece a la infraestructura. Si su clase `Reserva` importa algo relacionado con seguridad, el modelo está mal.

---

## Anti-patrones (términos a EVITAR)

|No usar|Usar|
|---|---|
|"Habitación", "cuarto", "cama"|"Apartamento"|
|"Room", "Booking", "Guest", "User"|"Apartamento", "Reserva", "Ocupante", "Usuario"|
|"Cliente"|"Titular"|
|"Adulto"|"Ocupante facturable"|
|"Precio por noche"|"Tarifa" (valor por ocupante facturable por noche)|
|"Check-in" / "Check-out" en el código|"Registro" / "Salida"|
|"Factura", "cuenta de cobro"|"Folio"|
|"Libre", "vacante"|"Disponible"|
|"Limpieza", "turnover"|"Tiempo de preparación"|
|"Status"|"Estado" (de la reserva) o "Estado operativo" (del apartamento)|
|"Cancelar" para una salida anticipada|"Registrar la salida"|
|"Eliminar" un apartamento o un usuario|"Desactivar" (la eliminación es lógica)|
|"Editar" un cargo o un pago|"Registrar un movimiento inverso"|

---

## Reglas de negocio clave

Las 22 reglas invariantes están en la sección 9 del documento del proyecto. Estas son las que más veces se violan por usar mal el lenguaje:

1. **Un apartamento no puede tener dos reservas activas solapadas**, sin importar el canal (RN-01). Es la razón de existir del sistema.
2. **La capacidad es un tope rígido, sin excepciones** (RN-02).
3. **Toda estancia tiene al menos una noche**, y la noche de salida no se ocupa ni se cobra (RN-03).
4. **No se crean reservas hacia el pasado** (RN-04).
5. **Se cobra por ocupante facturable, por noche**, liquidando noche por noche según la temporada (RN-05).
6. **Todos los ocupantes cuentan para la capacidad; solo los facturables generan cargo** (RN-06).
7. **La reserva solo transita entre los estados permitidos** (RN-08).
8. **Toda reserva que deja de estar activa libera sus noches de inmediato** (RN-12).
9. **La retención se calcula con la política congelada en la reserva**, no con la vigente (RN-13).
10. **Los cargos y pagos no se modifican ni se eliminan:** toda corrección es un movimiento inverso (RN-16).
11. **La combinación canal + identificador externo es única:** un mismo mensaje recibido dos veces no crea dos reservas (RN-19).
12. **El valor y la política quedan congelados al crear la reserva** (RN-22).

---

## Uso en código

**Buenas prácticas:**

```java
// Correcto: usa el lenguaje ubicuo
estancia.noches();
estancia.seSolapaCon(otraEstancia);
apartamento.admite(totalOcupantes);
apartamento.puedeRecibirGrupo();
ocupante.esFacturableEn(estancia, umbralEdadFacturable);
reserva.confirmar(fechaActual);
reserva.cancelar(motivo, fechaActual);
reserva.registrarLlegada(fechaActual);
folio.registrarPago(new Pago(monto, MedioPago.EFECTIVO, fecha));

// Incorrecto: términos técnicos genéricos o en inglés
booking.update(newStatus);
room.setAvailable(false);
reserva.process();
apartamento.checkIn();

// Incorrecto: cambio de estado genérico.
// Es un setEstado() con mejor nombre: deja que quien llama decida la transición
// y hace que las reglas de cada operación queden fuera del dominio.
reserva.cambiarEstado(EstadoReserva.EN_CURSO);

// Incorrecto: mezcla de idiomas
class ReservationRepositorio { }
reserva.getStatus();
```

**Convención de idioma del curso:**

|Elemento|Idioma|Ejemplo|
|---|---|---|
|Paquetes estructurales|Inglés|`domain`, `application`, `infrastructure`|
|**Conceptos del negocio**|**Español**|`Apartamento`, `Reserva`, `Folio`, `Ocupante`|
|Atributos y métodos del dominio|Español|`fechaEntrada`, `esFacturableEn()`|
|Palabras clave y anotaciones|Inglés (no se traducen)|`class`, `record`, `@Entity`|

---

## Términos propios del equipo

Cada equipo agrega aquí el vocabulario de **su** variante de alojamiento, tomado de la Ficha del Alojamiento (Anexo A) y de sus tres reglas propias (L-20). Los términos agregados son tan obligatorios como los de este documento y se evalúan en la sustentación.

|Término|Definición|No usar|Regla o funcionalidad que lo origina|
|---|---|---|---|
|||||
|||||
|||||

_Ejemplos de términos que suelen aparecer, según la variante elegida: `Depósito reembolsable`, `Cupo de parqueadero`, `Mascota autorizada`, `Estancia mínima`, `Recargo por llegada nocturna`, `Cupón`, `Huésped frecuente`._

---

**Última actualización:** Septiembre 2026 **Versión:** 1.0 **Responsable:** Docente del espacio académico Programación Avanzada — Universidad del Quindío **Documento fuente:** _Proyecto Final — SGA: Sistema de Gestión de Alojamiento_, versión 2.0, secciones 3, 4, 8 y 9.