 **Programa de Ingeniería de Sistemas y Computación
Universidad del Quindío**

**Curso:** Programación Avanzada
**Guía:** 03
**Título:** Proyecto Spring Boot con Gradle
**Duración estimada:** 90 minutos
**Docente:** Jhan Carlos Martinez Ceballos
**Proyecto del semestre:** SGA — Sistema de Gestión de Alojamiento

---

# 1. Objetivo

En las guías anteriores entendimos **qué problema vamos a resolver** y **preparamos el entorno**. En esta guía damos el siguiente paso:

> crear la **estructura base del proyecto** sobre la cual se construirá el SGA durante todo el semestre.

El objetivo **no es aprender Spring Boot ni Gradle en profundidad**, sino:

- tener un proyecto que **compile y arranque**,
- entender **para qué existe cada archivo** que se generó,
- dejar lista una estructura de paquetes que respete la **arquitectura hexagonal**,
- y dejar el repositorio configurado, porque el historial de commits es evidencia evaluable.

> Al terminar esta guía el proyecto **no tiene funcionalidad**. Eso está bien: se está construyendo el terreno, no la casa.

---

# 2. Conceptos básicos

1. **Sistema de construcción.** Herramienta que automatiza compilar, resolver dependencias, ejecutar pruebas y empaquetar la aplicación. En este curso: **Gradle**.
2. **DSL (*Domain-Specific Language*).** Lenguaje especializado para una tarea concreta. Gradle usa un DSL para describir cómo se construye el proyecto, y ofrece dos sintaxis: **Groovy** y **Kotlin**.
3. **Dependencia.** Librería externa que el proyecto necesita. Se declara, no se descarga a mano.
4. **Starter.** Agrupación de dependencias de Spring Boot que habilita una capacidad completa (web, persistencia, validación) con una sola línea.
5. **Archivo de propiedades.** `application.properties` configura el comportamiento de la aplicación: puerto, base de datos, registro de eventos.
6. **H2.** Base de datos relacional embebida, que arranca dentro de la aplicación. Ideal para desarrollo y pruebas.

---

# 3. Contextualización teórica

## 3.1 Gradle y Maven

Una herramienta de construcción automatiza la compilación, la gestión de dependencias, la ejecución de pruebas y el empaquetado, y garantiza que el resultado sea **repetible**: cualquier integrante del equipo obtiene exactamente lo mismo.

En el ecosistema Java las dos herramientas dominantes son **Maven** y **Gradle**.

|                          | Maven             | Gradle                    |
| ------------------------ | ----------------- | ------------------------- |
| Archivo de configuración | `pom.xml`         | `build.gradle`            |
| Formato                  | XML               | DSL en Groovy o Kotlin    |
| Estilo                   | Convención rígida | Configuración programable |

En este curso se usa **Gradle**, y sus tres piezas principales son:

- **`build.gradle`** — dependencias, plugins y propiedades del proyecto.
- **`settings.gradle`** — nombre y módulos del proyecto.
- **`gradlew` / `gradlew.bat`** — el *wrapper*, que descarga y ejecuta la versión correcta de Gradle. **Gracias a él nadie necesita instalar Gradle.** Estos archivos **sí se versionan**.

## 3.2 Groovy DSL y Kotlin DSL: una advertencia importante

Gradle admite dos sintaxis para el mismo archivo de configuración:

| Sintaxis | Nombre del archivo |
|---|---|
| **Groovy DSL** | `build.gradle` |
| **Kotlin DSL** | `build.gradle.kts` |

Hacen lo mismo, pero **no son intercambiables**: la sintaxis cambia y los ejemplos de una no funcionan en la otra.

> **Atención.** Spring Initializr ofrece hoy **Gradle - Kotlin DSL** como opción por defecto. **Este curso usa Gradle - Groovy DSL.** Si genera el proyecto sin cambiar esa opción, obtendrá un `build.gradle.kts` y **ninguno de los ejemplos de las guías siguientes coincidirá con su archivo**. Verifíquelo antes de generar.

## 3.3 Spring Boot 4 y sus *starters* modulares

Spring Boot 4 reorganizó sus dependencias en módulos más pequeños y específicos. Esto tiene dos consecuencias prácticas que afectan directamente esta guía:

1. **Varios *starters* cambiaron de nombre.** El más notorio: `spring-boot-starter-web` pasó a llamarse **`spring-boot-starter-webmvc`**. La mayoría de tutoriales en internet siguen usando el nombre viejo.
2. **Cada *starter* tiene ahora su propio *starter* de pruebas.** Si el proyecto usa `spring-boot-starter-webmvc`, las pruebas necesitan `spring-boot-starter-webmvc-test`.
3. **Funciones que antes se activaban solas ahora deben declararse.** El caso típico es la consola web de H2, que requiere la dependencia `spring-boot-h2console`. Sin ella, la ruta `/h2-console` responde 404 aunque H2 esté funcionando.

> Consecuencia para el estudiante: **si copia un `build.gradle` de un tutorial de Spring Boot 3, no va a funcionar.** Use el de esta guía.

## 3.4 Por qué H2 en todo el curso

H2 es una base de datos relacional que se ejecuta **dentro** de la aplicación. Se eligió porque:

- No requiere instalación ni servidor aparte.
- Todos los equipos trabajan sobre exactamente el mismo motor.
- Arranca con datos de prueba cargados, lo que permite demostrar el sistema en cualquier computador.
- Elimina una fuente enorme de problemas ajenos al objetivo del curso.

Más adelante se verá que **cambiar H2 por otro motor no debería obligar a tocar el dominio**. Esa sustitución es, precisamente, la demostración práctica de para qué sirve la arquitectura hexagonal.

---

# 4. Precauciones y recomendaciones

1. **Verifique el JDK.** `java -version` debe reportar 25 antes de empezar.
2. **Seleccione Groovy DSL** en Spring Initializr. Ver sección 3.2.
3. **No cree el proyecto en una ruta con espacios o tildes.**
4. **Actualice Gradle cada vez que modifique `build.gradle`.** En IntelliJ, use el ícono del elefante o el botón de recarga que aparece flotando.
5. **La primera construcción es lenta.** Gradle descarga todas las dependencias. Es normal que tarde varios minutos y requiere conexión a internet.

---

# 5. Procedimiento

## 5.1 Generar el proyecto

Se usará [Spring Initializr](https://start.spring.io/), que genera una estructura correcta y evita errores de configuración.

Configure exactamente estos valores:

| Opción           | Valor                                     |
| ---------------- | ----------------------------------------- |
| **Project**      | **Gradle - Groovy**                       |
| **Language**     | Java                                      |
| **Spring Boot**  | 4.1.x (la versión estable más reciente)   |
| **Group**        | `co.edu.uniquindio`                       |
| **Artifact**     | `sga`                                     |
| **Name**         | `sga`                                     |
| **Description**  | `SGA - Sistema de Gestión de Alojamiento` |
| **Package name** | `co.edu.uniquindio.sga`                   |
| **Packaging**    | Jar                                       |
| **Java**         | 25                                        |

En **Dependencies**, agregue las siguientes cinco. La columna de la derecha explica para qué se necesita cada una **en este proyecto**, no en abstracto:

| Dependencia         | Para qué se necesita en el SGA                                                                                                                                                                                                                                                                                                                                                   |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Spring Web**      | Levanta el servidor y permite exponer la API REST. Es lo que hará posible que el frontend consulte disponibilidad, cree reservas y registre pagos. Sin esta dependencia el proyecto compila pero no atiende peticiones.                                                                                                                                                          |
| **Validation**      | Valida los datos que **entran** al sistema: que la fecha de salida venga informada, que el correo tenga formato válido, que el número de ocupantes sea positivo. Cuidado: valida el *formato* de la petición, **no las reglas del negocio**. Que un grupo no exceda la capacidad del apartamento lo decide el dominio, no esta dependencia.                                      |
| **Spring Data JPA** | Permite guardar y recuperar información de la base de datos sin escribir SQL a mano. Será la base de los adaptadores de persistencia: apartamentos, reservas, folios y sus movimientos.                                                                                                                                                                                          |
| **H2 Database**     | Base de datos relacional embebida que arranca dentro de la aplicación. Guarda los datos mientras el sistema está en ejecución y permite que todos los equipos trabajen sobre el mismo motor sin instalar nada.                                                                                                                                                                   |
| **Lombok**          | Genera automáticamente constructores, *getters*, *setters* y otros métodos repetitivos a partir de anotaciones, para reducir el código que no aporta lógica. **Advertencia:** más adelante se verá que abusar de Lombok en las clases del dominio es una de las formas más rápidas de terminar con un modelo anémico. Úselo con criterio, sobre todo fuera del paquete `domain`. |

Haga clic en **Generate**, descomprima el archivo en una ruta simple y confirme que existe un archivo llamado `build.gradle`. **Si aparece `build.gradle.kts`, vuelva al paso 1 y corrija la opción Project.**

## 5.2 Abrir el proyecto en IntelliJ IDEA

1. **File > Open**.
2. Seleccione la **carpeta** del proyecto (no el archivo `build.gradle`).
3. IntelliJ detectará que es un proyecto Gradle e iniciará la descarga de dependencias. Espere a que termine.
4. Confirme en **File > Project Structure > Project** que el SDK sea el 25.

## 5.3 Reconocer los archivos generados

Antes de tocar nada, conviene entender qué generó Initializr. Estos son los elementos que aparecen en el panel de proyecto de IntelliJ:

| Elemento | Para qué sirve |
|---|---|
| `src/main/java` | **El código de la aplicación.** Aquí vive todo lo que se va a escribir durante el semestre. |
| `SgaApplication.java` | Clase de arranque. Contiene el `main` que levanta la aplicación. |
| `src/main/resources` | Recursos que no son código: configuración, datos de prueba, archivos estáticos. |
| `application.properties` | **Configuración de la aplicación:** puerto, base de datos, registro de eventos. Se edita constantemente. |
| `src/test/java` | **El código de las pruebas.** No se empaqueta con la aplicación. Aquí irán las pruebas de las reglas del negocio. |
| `SgaApplicationTests.java` | Prueba generada por defecto. Solo verifica que la aplicación arranque. |
| `build.gradle` | **Definición del proyecto:** dependencias, plugins y versión de Java. |
| `settings.gradle` | Nombre del proyecto y, en proyectos grandes, sus módulos. |
| `gradlew` y `gradlew.bat` | El *wrapper*: permite construir el proyecto sin instalar Gradle. **Se versionan.** |
| `gradle/` | Configuración del wrapper, incluida la versión de Gradle que se usará. **Se versiona.** |
| `.gitignore` | Lista de archivos que Git debe ignorar. |
| `build/` | **Resultado de la compilación.** Se regenera solo. **No se versiona.** |
| `.gradle/` y `.idea/` | Carpetas de trabajo de Gradle y de IntelliJ. **No se versionan.** |
| `HELP.md` | Enlaces de ayuda generados por Initializr. Puede borrarse. |

Dos carpetas que Initializr crea vacías y que **no se usarán en este curso**: `resources/static` y `resources/templates`. Sirven para servir páginas desde el propio backend, y aquí la interfaz será una aplicación React independiente.

> Regla rápida para distinguir qué se versiona: si el archivo lo escribió una persona, va al repositorio; si lo generó una herramienta y se puede volver a generar, no.

## 5.4 Revisar el archivo `build.gradle`

Su archivo debe quedar equivalente a este. **Conserve el bloque `plugins` que generó Initializr con sus propias versiones**; lo que debe revisar con cuidado es el bloque `dependencies`.

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.1.0'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'co.edu.uniquindio'
version = '0.0.1-SNAPSHOT'
description = 'SGA - Sistema de Gestión de Alojamiento'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // Servidor web y API REST. En Spring Boot 4 este starter se llama
    // 'webmvc'; en versiones anteriores se llamaba 'web'.
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'

    // Acceso a la base de datos mediante JPA e Hibernate.
    // Base de los adaptadores de persistencia del SGA.
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    // Validación de los datos que entran al sistema (formato de la petición).
    // Las reglas del negocio NO viven aquí: viven en el dominio.
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Consola web de H2, disponible en /h2-console.
    // En Spring Boot 4 debe declararse de forma explícita.
    implementation 'org.springframework.boot:spring-boot-h2console'

    // Generación automática de getters, setters y constructores.
    // Se necesita en dos configuraciones: para compilar y para procesar anotaciones.
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Motor de la base de datos H2. Solo se requiere al ejecutar,
    // no al compilar: por eso es 'runtimeOnly'.
    runtimeOnly 'com.h2database:h2'

    // Soporte de pruebas. En Spring Boot 4 cada starter tiene su propio
    // starter de pruebas, y debe declararse uno por cada starter usado arriba.
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-validation-test'

    // Lanzador de JUnit necesario para ejecutar las pruebas desde Gradle.
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

**Notas sobre este archivo:**

- `spring-boot-starter-webmvc` es el nombre correcto en Spring Boot 4. **No use `spring-boot-starter-web`.**
- `spring-boot-h2console` habilita la consola web de H2. Sin esta línea la consola no existe.
- Los tres `testImplementation` corresponden, uno a uno, con los *starters* usados en la aplicación.
- Si al ejecutar las pruebas aparecen errores de clases de JUnit o AssertJ que no se encuentran, agregue temporalmente `testImplementation 'org.springframework.boot:spring-boot-starter-test-classic'` y consúltelo con el docente.

**Qué significa cada palabra antes del nombre de la dependencia.** No es decoración: define *cuándo* está disponible la librería.

| Configuración | Significado |
|---|---|
| `implementation` | Disponible al compilar y al ejecutar. Es el caso normal. |
| `compileOnly` | Solo al compilar. Lombok entra aquí porque desaparece una vez generado el código. |
| `runtimeOnly` | Solo al ejecutar. El controlador de H2 entra aquí porque el código nunca lo nombra directamente. |
| `annotationProcessor` | Se ejecuta **durante** la compilación para generar código a partir de anotaciones. |
| `testImplementation` | Solo disponible en el código de pruebas, no en la aplicación. |
| `testRuntimeOnly` | Solo al ejecutar las pruebas. |

> Declarar todo como `implementation` "porque así funciona" es un error frecuente: infla el artefacto final y lleva a producción librerías que solo servían para pruebas.

Después de guardar, **recargue Gradle**.

## 5.5 Configurar `application.properties`

Reemplace el contenido de `src/main/resources/application.properties` por:

```properties
spring.application.name=sga

# --- Base de datos H2 en memoria ---
spring.datasource.url=jdbc:h2:mem:sgadb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# --- Consola web de H2 ---
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# --- JPA ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# --- Servidor ---
server.port=8080
```

> `jdbc:h2:mem:sgadb` significa que la base vive **en memoria**: los datos se pierden al detener la aplicación. Es el comportamiento deseado durante el desarrollo, porque garantiza que el sistema siempre arranque desde un estado conocido.

## 5.6 Verificar la clase principal

Initializr genera la clase principal. Confirme que exista en `src/main/java/co/edu/uniquindio/sga/`:

```java
package co.edu.uniquindio.sga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SgaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaApplication.class, args);
    }
}
```

## 5.7 Ejecutar y verificar

1. Ejecute el método `main` de `SgaApplication`.
2. En la consola debe aparecer el banner de Spring y una línea indicando que Tomcat inició en el puerto 8080.
3. Abra en el navegador: `http://localhost:8080/h2-console`
4. Aparecerá un formulario de conexión. **Este es el punto donde falla la mayoría del curso.** El formulario trae por defecto la configuración guardada *Generic H2 (Embedded)*, cuya URL es `jdbc:h2:~/test`. Esa URL **no corresponde a la base de datos del proyecto** y produce el error `Database "..." not found, either pre-create it or allow remote database cre[[03-Proyecto-Spring-Boot-con-Gradle]]ation`.

   Diligencie así, borrando lo que venga por defecto:

   | Campo | Valor |
   |---|---|
   | Controlador | `org.h2.Driver` |
   | **URL JDBC** | **`jdbc:h2:mem:sgadb`** |
   | Nombre de usuario | `sa` |
   | Contraseña | *(vacía)* |

   Presione **Conectar**.

> Si vuelve a tocar el desplegable **Configuraciones guardadas**, el campo de URL JDBC se restablece al valor por defecto y el error reaparece. Corrija la URL **después** de elegir cualquier configuración, no antes.
>
> La URL debe coincidir **exactamente** con la propiedad `spring.datasource.url` de su `application.properties`. Si allí cambió el nombre de la base, use el suyo.

Si la consola conecta, el backend y la base de datos están funcionando.

> Es normal que `http://localhost:8080/` muestre una página de error. Todavía no existe ningún endpoint.

---

# 6. Estructura de paquetes orientada al dominio

En lugar de organizar el código por capas técnicas (`controller`, `service`, `repository`), organizaremos el proyecto **por conceptos del dominio**.

La estructura general será:

```
co.edu.uniquindio.sga
├── domain          # Reglas y conceptos del negocio
├── application     # Casos de uso
├── infrastructure  # Adaptadores técnicos (REST, persistencia)
```

> Esta estructura nos permitirá crecer el sistema sin perder claridad.

## 6.1 Crear la estructura de paquetes

Crea los siguientes paquetes:

- `domain`
- `application`
- `infrastructure`

Por ahora estarán vacíos. En las próximas guías comenzaremos a llenarlos **desde el dominio hacia afuera**.

---

# 7. Convención de idioma en el código

Para que todos los equipos y todas las guías hablen igual, el curso adopta esta convención:

| Elemento | Idioma | Ejemplo |
|---|---|---|
| Nombres de paquetes estructurales | Inglés | `domain`, `application`, `infrastructure` |
| **Conceptos del negocio** | **Español** | `Apartamento`, `Reserva`, `Folio`, `OcupanteFacturable` |
| Atributos y métodos del dominio | Español | `calcularValorEstancia()`, `fechaEntrada` |
| Palabras clave y anotaciones | Inglés (no se traducen) | `class`, `@Entity` |

**Razón:** el lenguaje ubicuo del proyecto está en español porque el negocio está en español. Traducir `apartamento` a `apartment` introduce una capa de interpretación entre lo que dice el cliente y lo que dice el código, y es justamente lo que la arquitectura busca evitar. Los paquetes estructurales se dejan en inglés porque son vocabulario técnico, no del negocio.

> No mezcle. `ReservaRepository` es aceptable; `ReservationRepositorio` no.

---

# 8. Configuración de Git y GitHub

## 8.1 Crear el repositorio

1. Un integrante del equipo crea el repositorio en GitHub con el nombre `sga-<nombre-del-alojamiento>`.
2. Agrega a los demás integrantes como colaboradores.
3. Desde IntelliJ: **Git > GitHub > Share Project on GitHub**.

| Opción | Valor |
|---|---|
| Repository name | `sga-<nombre-del-alojamiento>` |
| Description | `SGA - Sistema de Gestión de Alojamiento` |
| Visibility | Public |

## 8.2 Verificar el `.gitignore`

Initializr genera un `.gitignore` adecuado. Confirme que **no** se estén versionando:

```
build/
.gradle/
*.class
.idea/
*.iml
.env
application-local.properties
```

**Sí deben versionarse** `gradlew`, `gradlew.bat` y la carpeta `gradle/wrapper/`. Sin ellos, quien clone el repositorio no puede construir el proyecto.

> **Regla del curso:** ninguna clave, token ni credencial se versiona. Nunca. Este punto es evaluable y se revisa en cada corte.

## 8.3 Crear el README

Cree un archivo `README.md` en la raíz con:

- Nombre del alojamiento del equipo y una descripción breve.
- Integrantes del equipo.
- Requisitos para ejecutar (JDK 25).
- Instrucciones de ejecución.
- Estado actual del proyecto.

## 8.4 Convención de commits

El historial de commits es **evidencia evaluable** de la participación de cada integrante. Se exige:

- Commits **pequeños y frecuentes**, no un único commit gigante al final.
- Mensajes descriptivos en español, en infinitivo o presente: `Agregar entidad Apartamento con validación de capacidad`.
- **Todos los integrantes deben tener commits propios** desde su propia cuenta.

Mensajes como `cambios`, `avance`, `.` o `commit final` no se aceptan.

## 8.5 Primer commit

```bash
git add .
git commit -m "Crear estructura base del proyecto SGA con Spring Boot y Gradle"
git push origin main
```

---

# 9. Verificación

Antes de cerrar la guía, confirme:

| # | Criterio |
|---|---|
| 1 | El proyecto existe y el archivo de construcción se llama `build.gradle`, **no** `build.gradle.kts` |
| 2 | `./gradlew build` termina sin errores |
| 3 | La aplicación arranca y reporta Tomcat en el puerto 8080 |
| 4 | La consola de H2 abre y conecta con `jdbc:h2:mem:sgadb` |
| 5 | Los tres paquetes `domain`, `application` e `infrastructure` están creados |
| 6 | El repositorio existe en GitHub con todos los integrantes como colaboradores |
| 7 | `gradlew` y `gradle/wrapper/` están versionados; `build/` y `.idea/` no |
| 8 | El `README.md` está creado y diligenciado |

---

# 10. Errores frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| Los ejemplos de las guías no coinciden con mi archivo | Se generó con Kotlin DSL | Regenere el proyecto con **Gradle - Groovy** |
| `Could not find spring-boot-starter-web` | Nombre de Spring Boot 3 | Use `spring-boot-starter-webmvc` |
| `/h2-console` responde 404 | Falta la dependencia | Agregue `spring-boot-h2console` y recargue Gradle |
| `Database "/Users/.../test" not found, either pre-create it or allow remote database creation` | El formulario quedó con la URL por defecto `jdbc:h2:~/test`, que apunta a un archivo inexistente | Reemplace la URL JDBC por `jdbc:h2:mem:sgadb`. **No es un problema del proyecto**: la aplicación está bien |
| La consola de H2 no conecta | La JDBC URL no coincide con `spring.datasource.url` | Escriba exactamente `jdbc:h2:mem:sgadb` |
| Conecta pero no aparece ninguna tabla | Correcto por ahora: todavía no hay entidades JPA | Las tablas aparecerán a partir de la guía de persistencia |
| `Unsupported class file major version` | El SDK del proyecto no es el 25 | Ajuste en **Project Structure > Project** |
| Gradle no descarga nada | Sin conexión o proxy | Verifique la red e intente `./gradlew build --refresh-dependencies` |
| Otro integrante clona y no puede construir | No se versionó el wrapper | Versione `gradlew` y `gradle/wrapper/` |
| El puerto 8080 está ocupado | Otra aplicación lo usa | Cambie `server.port` en `application.properties` |

---

# 11. Evaluación o resultado

Al finalizar, el estudiante debe ser capaz de:

1. Generar un proyecto Spring Boot con Gradle usando Spring Initializr, eligiendo correctamente el DSL.
2. Explicar qué hace cada archivo generado y por qué el wrapper se versiona.
3. Identificar los *starters* correctos de Spring Boot 4 y reconocer por qué los ejemplos de la versión 3 fallan.
4. Configurar H2 y verificar su funcionamiento a través de la consola web.
5. **Crear y justificar la estructura de paquetes hexagonal**, explicando en qué dirección apuntan las dependencias.
6. Configurar el repositorio del equipo con las prácticas exigidas por el curso.

**Entregable:** enlace al repositorio en GitHub, con el primer commit realizado y el `README.md` diligenciado.

---

# 12. Próxima actividad

Para prepararse para la **Guía 04 (Modelado del Dominio I)**:

**Completar**
1. Los ocho criterios de verificación de la sección 9.
2. Un borrador de la **Ficha del Alojamiento** (Anexo A del documento del proyecto): nombre del alojamiento, cantidad de apartamentos y capacidades.

**Leer**
1. Evans, E. (2003). *Domain-Driven Design*, capítulo 2: "Communication and the Use of Language".
2. Vernon, V. (2013). *Implementing Domain-Driven Design*, capítulo 2: "Domains, Subdomains, and Bounded Contexts".

**Investigar**
1. **Entidad** y **objeto de valor**: en qué se diferencian.
2. **Invariante del dominio**: qué es y por qué una entidad nunca debería poder existir en estado inválido.
3. **Modelo de dominio anémico**: por qué Martin Fowler lo considera un antipatrón.

> Pregunta para pensar antes de la próxima clase: en el SGA, ¿`Estancia` (el rango de noches de una reserva) debería ser una entidad o un objeto de valor? ¿Por qué?

---

# 13. Referencias bibliográficas

1. Evans, E. (2003). *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley.
2. Gradle. (2026). *Gradle User Manual*. https://docs.gradle.org/current/userguide/userguide.html
3. H2 Database Engine. (2026). *H2 Database Engine Documentation*. https://www.h2database.com/html/main.html
4. Spring. (2025). *Modularizing Spring Boot*. https://spring.io/blog/2025/10/28/modularizing-spring-boot/
5. Spring. (2026). *Spring Boot 4.0 Migration Guide*. https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
6. Spring. (2026). *Spring Boot Reference Documentation*. https://docs.spring.io/spring-boot/
7. Spring. (2026). *Spring Data JPA Reference Documentation*. https://docs.spring.io/spring-data/jpa/
8. Spring Initializr. https://start.spring.io/
9. Walls, C. (2022). *Spring in Action* (6.ª ed.). Manning Publications.

---

> **Recuerda:** no estamos aprendiendo herramientas, estamos aprendiendo a **construir software que represente la realidad**.
