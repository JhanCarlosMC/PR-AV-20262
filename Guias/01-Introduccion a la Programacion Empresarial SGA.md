**Programa de Ingeniería de Sistemas y Computación\
Universidad del Quindío**

**Curso:** Programación Avanzada\
**Guía:** 01\
**Título:** Introducción a la Programación Empresarial\
**Duración estimada:** 90 minutos\
**Proyecto del curso:** SGA — Sistema de Gestión de Apartamentos\
**Docente:** Jhan Carlos Martinez Ceballos

---

# Introducción a la Programación Empresarial

## 1. ¿De qué trata realmente este curso?

Cuando pensamos en programar, muchas veces imaginamos escribir código que **funcione**: que compile, que muestre resultados, que responda a una petición. En este curso vamos un paso más allá.

Aquí aprenderás a construir software que:

- represente correctamente **un problema real**,
- aplique **reglas del mundo real**,
- y pueda mantenerse y evolucionar con el tiempo.

Antes de hablar de bases de datos, APIs o frameworks, nos vamos a hacer una pregunta clave:

> **¿Qué problema estamos resolviendo y cuáles son sus reglas?**

Esa pregunta es el corazón de la programación empresarial.

---

## 2. ¿Qué es una aplicación empresarial?

Una **aplicación empresarial** es un sistema de software que apoya las actividades de una organización. No se trata solo de guardar datos, sino de **tomar decisiones correctamente**.

Ojo con una confusión frecuente: "empresarial" no significa *grande* ni *corporativo*. El criterio es **para quién**: estas aplicaciones resuelven necesidades de una **organización** —una universidad, una alcaldía, una ONG, un hotel familiar— y no de personas aisladas.

Ejemplos de aplicaciones empresariales:

- Sistemas académicos universitarios
- Plataformas de gestión de solicitudes
- Sistemas de facturación
- Aplicaciones de recursos humanos
- Sistemas de gestión hotelera

Estas aplicaciones comparten algo en común:

- tienen **reglas**
- tienen **procesos**
- tienen **roles** (no todos los usuarios pueden hacer lo mismo)
- y tienen **consecuencias si se usan mal**

Esa última característica es la que marca la diferencia real: si el sistema se equivoca, **alguien pierde dinero, tiempo o un derecho**.

---

## 3. El problema que abordaremos en el curso

Durante el semestre trabajaremos sobre un problema real y cercano:

> El **SGA — Sistema de Gestión de Apartamentos**, para administrar un aparthotel del departamento del Quindío.

Un aparthotel no alquila habitaciones ni camas: alquila **apartamentos autónomos**, cada uno con su cocina, su baño y su acceso independiente.

El sistema deberá permitir, entre otras cosas:

- consultar la disponibilidad de apartamentos en un rango de fechas
- registrar la estancia de unos huéspedes
- llevar la cuenta de sus consumos
- aplicar tarifas según la época del año
- gestionar cancelaciones

Estas operaciones:

- tienen un **ciclo de vida**
- pasan por **estados**
- deben cumplir **reglas claras**

Por ejemplo:

- un apartamento no puede tener dos estancias que se solapen en el tiempo
- no se pueden alojar más personas de las que caben en el apartamento
- una cuenta ya cerrada no admite nuevos consumos
- no todas las cancelaciones son gratuitas

Este conjunto de reglas es lo que llamamos **el dominio del problema**.

> **Una advertencia desde ya.** En un semestre anterior, un equipo tomó la información del alojamiento de su perfil en una plataforma de reservas y concluyó que se alquilaban habitaciones compartidas. Era falso: el perfil correspondía a otro alojamiento parecido. El costo no fue una frase mal escrita, fue un **modelo entero construido sobre una unidad de venta que no existía**. Entender el problema no es leer una página web: es preguntarle a quien sabe.

---

## 4. El dominio: el corazón del sistema

El **dominio** es el conocimiento propio del problema que queremos resolver. Existía antes de que existiera el sistema, y seguiría existiendo si el sistema se apagara: el aparthotel funcionaba con un cuaderno y un tablero mucho antes de que llegáramos nosotros.

En nuestro caso, el dominio incluye conceptos como:

- Apartamento
- Estancia
- Titular y ocupantes
- Temporada y tarifa
- Cuenta y consumos
- Disponibilidad y bloqueo
- Política de cancelación

Lo importante es entender que:

> **El software debe adaptarse al dominio, no al revés.**

Si el código no refleja estos conceptos, el sistema será difícil de entender, mantener y evolucionar. Y algo peor: será difícil **conversar sobre él** con quien conoce el negocio.

---

## 5. Pensar primero en reglas, no en tecnología

En muchos cursos se empieza así:

- elegir una base de datos
- definir tablas
- crear endpoints

En este curso el orden será diferente:

1. Entender el problema
2. Identificar las reglas
3. Modelar los conceptos
4. Luego elegir la tecnología

Esto nos permite separar dos tipos de complejidad:

| Complejidad técnica | Complejidad del dominio |
| ------------------- | ----------------------- |
| Frameworks          | Reglas del negocio      |
| Bases de datos      | Estados y decisiones    |
| APIs                | Qué se puede y qué no   |

Primero resolvemos la **complejidad del dominio**.

Hay un detalle del oficio que conviene saber desde ahora: las reglas de negocio **casi nunca están escritas**. Suelen estar en la cabeza de quien lleva años en el cargo, en un archivo de Excel que nadie toca, o enterradas en el código de un sistema anterior. Por eso el trabajo empieza **preguntando**, no programando.

---

## 6. El lenguaje del dominio importa

Cuando las personas que conocen el problema hablan, usan palabras específicas:

- estancia
- titular
- temporada
- bloquear
- cancelar

En este curso usaremos esas mismas palabras:

- en las clases
- en los diagramas
- y también **en el código**

A esto se le llama **lenguaje compartido** (o *lenguaje ubicuo*).

Ejemplo:

```java
estancia.registrarOcupante(ocupante);
apartamento.bloquear(rango, MotivoBloqueo.MANTENIMIENTO);
cuenta.agregarConsumo(consumo);
```

El código debe poder leerse casi como una frase.

**Glosario preliminar del SGA** (crecerá durante el semestre):

| Término | Significado |
| ------- | ----------- |
| Apartamento | Unidad autónoma que se alquila. Nunca "habitación" ni "cama" |
| Titular | Persona responsable de la estancia y de su pago |
| Ocupante | Persona que se aloja en el apartamento |
| Estancia | Permanencia de unos ocupantes en un apartamento entre dos fechas |
| Temporada | Período del año que determina la tarifa aplicable |
| Cuenta | Registro acumulado de los consumos de una estancia |
| Bloqueo | Indisponibilidad deliberada de un apartamento |

F�jate en que **titular** y **ocupante** no son lo mismo: una empresa puede ser titular de una estancia en la que se alojan tres empleados. Confundirlos en el código es un error caro.

> **Decisión del curso:** como el dominio se conversa en español, **el código también se escribe en español**. En la industria es igualmente válido escribirlo en inglés manteniendo un glosario de mapeo (*estancia → stay*); lo que no es válido es mezclar los dos idiomas sin criterio.

---

## 7. ¿Cómo interactúan los usuarios con el sistema?

Una aplicación empresarial normalmente se divide en dos grandes partes:

- **Cliente:** donde el usuario interactúa (interfaz)
- **Servidor:** donde viven las reglas y decisiones

El cliente envía solicitudes y el servidor responde.

Más adelante aprenderemos a usar APIs y servicios web para esto, pero por ahora quédate con esta idea:

> **Las decisiones importantes siempre viven en el servidor.**

¿Por qué? Porque el cliente es territorio del usuario: puede inspeccionarse o modificarse. Si la regla "no se pueden solapar dos estancias" solo existe en el formulario del navegador, la regla no existe.

---

## 8. Separar responsabilidades (sin memorizar arquitectura)

En un sistema bien diseñado:

- unas partes reciben solicitudes
- otras toman decisiones
- otras guardan información

No necesitas memorizar nombres ni diagramas todavía. Lo importante es entender que:

> **No todo el código hace lo mismo, y mezclar responsabilidades genera problemas.**

¿Qué problemas, concretamente? Cuando la lógica del negocio queda repartida entre controladores, servicios y consultas, aparecen duplicaciones, errores difíciles de rastrear y —el síntoma más claro de todos— **miedo a tocar el código**.

Durante el curso iremos descubriendo esta separación de forma natural.

---

## 9. Qué NO se espera que entiendas aún

Es completamente normal que en este punto no entiendas todavía:

- Spring Boot
- Bases de datos
- APIs REST
- Arquitecturas
- Frameworks frontend

Todo eso se construirá paso a paso.

Este núcleo temático también incluye **arquitecturas empresariales, microservicios, patrones de diseño y stacks de desarrollo**. No aparecen en esta guía porque tienen su propio espacio más adelante: primero necesitas el problema para que esos temas tengan sentido.

Lo único importante ahora es que empieces a pensar en:

> **problemas, reglas y decisiones**

---

## 10. Tecnologías que usaremos (más adelante)

A lo largo del curso utilizaremos herramientas modernas para implementar nuestras ideas:

- Java y Spring Boot
- Gradle como herramienta de construcción
- Base de datos embebida H2
- React en el frontend
- Git y GitHub para el control de versiones

Estas tecnologías **no son el objetivo**, son los medios. Todas van a cambiar en los próximos años; lo que aprendas sobre cómo pensar un dominio, no.

---

## 11. Qué aprenderás al final del curso

Al finalizar el curso serás capaz de:

- entender un problema empresarial
- identificar sus reglas
- modelar conceptos importantes
- construir un backend que tome decisiones correctamente
- conectar ese backend con una interfaz de usuario

Más importante aún:

> **sabrás cómo pensar antes de programar.**

---

## 12. Próxima actividad

Para la siguiente guía trabajaremos el **entorno de desarrollo**.

Por ahora reflexiona:

- ¿Qué reglas crees que tiene una estancia en un aparthotel?
- ¿Qué cosas NO deberían permitirse nunca?
- Si un huésped llama a las once de la noche pidiendo quedarse una noche más, ¿qué tendría que verificar el sistema antes de decir que sí?

Estas preguntas serán la base de todo el curso.

---

**Bienvenidos al curso de Programación Avanzada.** Aquí no solo escribimos código: **construimos sistemas que entienden el mundo real.**

---

## Bibliografía y referencias

**Bibliografía del espacio académico**

- Blancarte I., Oscar J. (2016). *Introducción a los patrones de diseño: un enfoque práctico*. CreateSpace Independent Publishing Platform.
- Blancarte I., Oscar J. (2020). *Introducción a la arquitectura de software: un enfoque práctico*. CreateSpace Independent Publishing Platform.
- Halpin, Terry (2015). *Object-Role Modeling Workbook: Data Modeling Exercises using ORM*. Technics Publications.
- Newman, Sam (2014). *Building Microservices: Designing Fine-Grained Systems*. O'Reilly Media.
- Northwood, Chris (2018). *The Full Stack Developer*. Apress.
- Richardson, Leonard (2014). *RESTful Web APIs: Services for a Changing World*. O'Reilly Media.
- Ruíz, Carlos (2018). *Spring Boot & Angular: Desarrollo de WebApps Seguras*. 0xWord.

**Referencias de los conceptos de esta guía**

- Evans, Eric (2003). *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley. — Origen de las nociones de **dominio** y **lenguaje ubicuo**.
- Fowler, Martin (2002). *Patterns of Enterprise Application Architecture*. Addison-Wesley. — Referencia sobre dónde ubicar la lógica de negocio dentro de una aplicación empresarial.
- Cockburn, Alistair (2005). *Hexagonal Architecture (Ports and Adapters)*. — Base de la separación de responsabilidades que veremos más adelante.

**Lecturas complementarias en línea**

- ¿Qué es una aplicación empresarial? — https://habitatweb.mx/que-es-una-aplicacion-empresarial
- Enterprise software (Wikipedia) — https://en.wikipedia.org/wiki/Enterprise_software
- Diseño guiado por el dominio (Wikipedia) — https://es.wikipedia.org/wiki/Dise%C3%B1o_guiado_por_el_dominio
- Qué es Domain-Driven Design y cómo aplicarlo — https://profile.es/blog/que-es-domain-driven-design-como-aplicarlo/
- Lenguaje ubicuo para DDD — https://gist.github.com/codewithleader/a847f587eb517fe79c2d874f5c365ded
- Reglas de negocio (Wikipedia) — https://es.wikipedia.org/wiki/Reglas_de_negocio
- Reglas de negocio: administrando la operación — https://sg.com.mx/revista/15/reglas-negocio-administrando-la-operacion-reglas

**Documentos del curso**

- Sílabo de Programación Avanzada, Universidad del Quindío. Formato M-DO-04-F-21, versión 03. Núcleo Temático 1.
- Glosario de Lenguaje Ubicuo — Proyecto SGA.
