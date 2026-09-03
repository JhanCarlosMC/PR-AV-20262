**Programa de Ingeniería de Sistemas y Computación
Universidad del Quindío**

**Curso:** Programación Avanzada
**Guía:** 02
**Título:** Configuración del Entorno de Desarrollo
**Duración estimada:** 90 minutos
**Docente:** Jhan Carlos Martinez Ceballos
**Proyecto del semestre:** SGA — Sistema de Gestión de Alojamiento

---

# 1. Objetivo

Preparar el entorno de desarrollo para que, durante el resto del semestre, el esfuerzo se concentre en **modelar el negocio y construir la solución**, y no en resolver bloqueos de instalación.

Al terminar esta guía cada estudiante debe tener un computador capaz de compilar y ejecutar un backend en Java con Spring Boot, un frontend en React, y versionar ambos en un repositorio remoto.

> Las herramientas de esta guía **no son el objetivo del curso**. Son los medios para materializar el modelo del negocio. Ninguna de ellas resuelve un problema de diseño mal planteado.

---

# 2. Conceptos básicos

1. **Instalación de programas.** Proceso mediante el cual se configuran herramientas de software en un sistema operativo.
2. **Variables de entorno.** Valores que el sistema operativo pone a disposición de los programas. `PATH` indica dónde buscar ejecutables; `JAVA_HOME` indica dónde está instalado el JDK.
3. **Línea de comandos.** Interfaz de texto para interactuar con el sistema operativo. Es la forma en que se verifica que cada herramienta quedó bien instalada.
4. **Gestor de paquetes.** Programa que instala y actualiza otros programas (`npm` para JavaScript, `SDKMAN!` o `Homebrew` para herramientas de la JVM).

---

# 3. Contextualización teórica

Un **entorno de desarrollo** es el conjunto de herramientas que trabajan de forma integrada para construir, ejecutar y versionar una aplicación. El SGA es una aplicación web con dos piezas independientes que se comunican por HTTP:

| Pieza    | Tecnología                  | Herramientas necesarias             |
| -------- | --------------------------- | ----------------------------------- |
| Backend  | Java + Spring Boot + Gradle | JDK, IntelliJ IDEA                  |
| Frontend | React                       | Node.js, Visual Studio Code         |
| Ambas    | —                           | Git, cuenta de GitHub, cliente HTTP |

## 3.1 Herramientas y para qué sirven

**JDK (Java Development Kit).** Conjunto de programas y librerías para desarrollar en Java. En este curso se usa **JDK 25**, la versión LTS vigente. Spring Boot 4 admite desde Java 17, pero el curso estandariza en 25 para que todos compilen igual.

**Spring Boot.** Framework que simplifica la construcción de aplicaciones backend en Java. Provee dependencias preconfiguradas llamadas *starters*. No se instala: se declara como dependencia del proyecto.

**Gradle.** Herramienta de automatización de construcción. **No requiere instalación**: cada proyecto trae su propio *wrapper* (`gradlew`), que descarga la versión correcta automáticamente.

**IntelliJ IDEA Ultimate.** Entorno de desarrollo para el backend. La edición Ultimate es gratuita para estudiantes con correo institucional.

**Node.js y npm.** Entorno de ejecución de JavaScript fuera del navegador. React lo necesita para instalar dependencias y levantar el servidor de desarrollo. `npm` viene incluido con Node.

**React.** Librería para construir interfaces de usuario. **No se instala globalmente**: se crea por proyecto con una herramienta de andamiaje. En este curso se usa **Vite** como empaquetador y servidor de desarrollo.

**Visual Studio Code.** Editor liviano para el frontend.

**Git y GitHub.** Control de versiones distribuido y su plataforma de alojamiento. En este curso el **historial de commits es evidencia evaluable** de la participación de cada integrante del equipo.

**Cliente HTTP.** Herramienta para invocar la API sin necesidad de frontend. Será indispensable desde el Corte 2 y, en el Corte 4, es el medio para construir el **simulador de canal externo** que exige el proyecto.

**H2.** Base de datos relacional embebida. **No se instala**: viaja como dependencia del proyecto y arranca dentro de la aplicación.

---

# 4. Precauciones y recomendaciones

1. **Instale el JDK primero.** IntelliJ, Gradle y Spring Boot dependen de él.
2. **Evite rutas con espacios, tildes o caracteres especiales.** Una carpeta llamada `C:\Users\María José\Programación Avanzada` produce fallos difíciles de diagnosticar en Gradle. Use rutas simples como `C:\dev\` o `~/dev/`.
3. **Verifique cada herramienta apenas la instale.** Descubrir tres instalaciones malas al final es mucho más costoso que descubrir una a la vez.
4. **Si el comando no se reconoce después de instalar, cierre y vuelva a abrir la terminal.** Las variables de entorno solo se recargan al abrir una nueva sesión.
5. **No instale versiones diferentes a las indicadas** sin avisar al docente. Un equipo con versiones mezcladas pierde tiempo en errores que no son del proyecto.

---

# 5. Procedimiento de instalación

## 5.1 JDK 25

Descargue e instale una distribución de OpenJDK 25:

- [Adoptium Temurin](https://adoptium.net/es/temurin/releases/)
- [Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-25-ug/downloads-list.html)

**Windows.** Descargue el instalador `.msi` y marque las opciones de agregar al `PATH` y establecer `JAVA_HOME`.

**macOS.** Descargue el `.dmg` correspondiente a su arquitectura: **aarch64 / ARM64** para equipos con chip Apple (M1 en adelante), **x64** para equipos Intel. Instalar el paquete equivocado produce fallos de arranque poco claros.

Alternativa recomendada en macOS y Linux, si prefiere manejar varias versiones de Java:

```bash
# Con SDKMAN!
curl -s "https://get.sdkman.io" | bash
sdk install java 25-tem

# Con Homebrew (macOS)
brew install --cask temurin@25
```

**Linux.** Use el gestor de paquetes de su distribución o SDKMAN!.

**Verificación:**

```bash
java -version
javac -version
```

Ambos comandos deben reportar versión 25.

## 5.2 IntelliJ IDEA Ultimate

1. Cree una cuenta en [JetBrains](https://account.jetbrains.com/login) **con su correo institucional**, indicando que es estudiante.
2. Solicite la licencia gratuita en [Licencia para estudiantes](https://www.jetbrains.com/shop/eform/students).
3. Descargue e instale IntelliJ IDEA Ultimate desde [JetBrains Downloads](https://www.jetbrains.com/es-es/idea/download/).
4. Inicie sesión con la cuenta creada cuando el IDE lo solicite.

## 5.3 Git y cuenta de GitHub

1. Descargue e instale Git desde [git-scm.com](https://git-scm.com/downloads).
2. Cree una cuenta en [GitHub](https://github.com) si aún no la tiene. **Use un nombre de usuario profesional**: ese repositorio será parte de su portafolio.
3. Configure su identidad. Estos datos quedan grabados en cada commit y son los que permiten verificar la participación individual:

```bash
git config --global user.name "Su Nombre Completo"
git config --global user.email "sucorreo@uqvirtual.edu.co"
```

**Verificación:**

```bash
git --version
git config --global --list
```

## 5.4 Visual Studio Code

1. Descargue e instale desde [code.visualstudio.com](https://code.visualstudio.com/download).
2. Instale las siguientes extensiones:
   - **ESLint** — detecta errores y malas prácticas en JavaScript y TypeScript.
   - **Prettier** — formatea el código de manera uniforme en todo el equipo.
   - **ES7+ React/Redux snippets** — atajos para escribir componentes.
   - **GitLens** — visualiza el historial de cambios línea por línea.

## 5.5 Node.js y npm

1. Descargue la versión **LTS** desde [nodejs.org](https://nodejs.org/en/download/). No use la versión *Current*.
2. Instale con las opciones por defecto.

**Verificación:**

```bash
node -v
npm -v
```

> **No instale React de forma global.** A diferencia de Angular, React no tiene una CLI global. Cada proyecto se crea con `npm create vite@latest`, lo cual se hará en la guía correspondiente al frontend.

## 5.6 Cliente HTTP

Instale **uno** de los siguientes:

- [Postman](https://www.postman.com/downloads/) — el más difundido.
- [Bruno](https://www.usebruno.com/) — liviano y **guarda las peticiones como archivos dentro del repositorio**, lo cual facilita versionarlas y entregarlas.

> Para el Corte 4 deberá entregar el simulador de canal externo. Si elige Bruno, esa colección queda versionada junto al código y el entregable sale prácticamente solo.

## 5.7 H2 y otras piezas que no se instalan

No instale nada para lo siguiente. Llegan como dependencias del proyecto:

| Pieza | Cómo llega |
|---|---|
| Spring Boot | Dependencia declarada en `build.gradle` |
| Gradle | Wrapper incluido en el proyecto (`gradlew`) |
| H2 | Dependencia declarada en `build.gradle` |
| React | Se crea por proyecto con Vite |

---

# 6. Verificación del entorno

Ejecute en una terminal nueva y confirme que **todos** los comandos respondan:

| Comando | Salida esperada |
|---|---|
| `java -version` | Versión 25 |
| `javac -version` | Versión 25 |
| `echo $JAVA_HOME` (Windows: `echo %JAVA_HOME%`) | Ruta del JDK, no vacía |
| `git --version` | Versión de Git |
| `node -v` | Versión LTS |
| `npm -v` | Versión de npm |

Si alguno falla, resuélvalo **antes** de continuar con la Guía 03.

---

# 7. Errores frecuentes

| Síntoma | Causa habitual | Solución |
|---|---|---|
| `java: command not found` | El JDK no quedó en el `PATH` | Reinstale marcando la opción, o agregue la ruta manualmente |
| `java -version` reporta una versión distinta a la 25 | Hay otro JDK instalado antes en el `PATH` | Ajuste `JAVA_HOME` y el orden del `PATH`, o use SDKMAN! |
| La aplicación no arranca en macOS con chip Apple | Se instaló el JDK para arquitectura Intel | Reinstale la versión **aarch64 / ARM64** |
| `npm` falla con errores de permisos | Instalación global en carpeta protegida | No use `sudo`; reinstale Node con el instalador oficial |
| Los commits aparecen con otro autor | `user.email` mal configurado | Reconfigure con `git config --global` y verifique |
| Gradle falla con errores de ruta | La carpeta del proyecto tiene espacios o tildes | Mueva el proyecto a una ruta simple |

---

# 8. Evaluación o resultado

Al finalizar la guía cada estudiante debe demostrar:

1. Los seis comandos de la sección 6 ejecutándose correctamente.
2. IntelliJ IDEA Ultimate instalado y con la licencia de estudiante activa.
3. Visual Studio Code con las cuatro extensiones instaladas.
4. Cuenta de GitHub creada y Git configurado con nombre y correo institucional.
5. Un cliente HTTP instalado y abierto.

**Entregable:** una captura de pantalla de la terminal con la salida de los seis comandos de verificación.

---

# 9. Próxima actividad

Para prepararse para la **Guía 03 (Proyecto Spring Boot con Gradle)**, investigue:

1. **Gradle:** qué es un sistema de construcción y qué problema resuelve.
2. **DSL de Gradle:** diferencias entre la sintaxis Groovy y la sintaxis Kotlin, y por qué existen las dos.
3. **Spring Initializr:** para qué sirve y qué genera.
4. **Arquitectura hexagonal:** qué son los puertos y los adaptadores. Basta una lectura introductoria.

---

# 10. Referencias bibliográficas

1. Chacon, S., & Straub, B. (2014). *Pro Git* (2.ª ed.). Apress.
2. Gradle. (2026). *Gradle User Manual*. https://docs.gradle.org/current/userguide/userguide.html
3. JetBrains. (2026). *IntelliJ IDEA Documentation*. https://www.jetbrains.com/idea/documentation/
4. Microsoft. (2026). *Visual Studio Code Documentation*. https://code.visualstudio.com/docs
5. Node.js Foundation. (2026). *Node.js Documentation*. https://nodejs.org/en/docs/
6. Oracle. (2025). *Java SE Development Kit 25 Documentation*. https://docs.oracle.com/en/java/javase/25/
7. Stallings, W. (2017). *Operating Systems: Internals and Design Principles* (9.ª ed.). Pearson.
8. Walls, C. (2022). *Spring in Action* (6.ª ed.). Manning Publications.

---

> **Recuerda:** antes de escribir código, aprendemos a **entender el problema**. Las herramientas están aquí para ayudarnos, no para distraernos.
