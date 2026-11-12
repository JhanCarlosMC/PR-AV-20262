# ---------- Etapa 1: compilación ----------
# JDK 25 completo: coincide con el toolchain de build.gradle (languageVersion 25).
# El wrapper usa Gradle 9.7.1, que soporta Java 25.
FROM eclipse-temurin:25-jdk AS build

# Carpeta de trabajo dentro del contenedor.
WORKDIR /app

# Copia el contenido de sga/ (el Root Directory de Render) menos lo que liste .dockerignore.
COPY . .

# chmod +x: el bit de ejecución de gradlew no siempre sobrevive a Windows/Git.
# bootJar: solo el jar ejecutable. "build" generaría además el -plain.jar y
#          el COPY *.jar de la etapa 2 encontraría dos archivos.
# --no-daemon: en un build de una sola vez el daemon solo consume memoria.
# -x test: SeguridadIntegrationTest y las demás necesitan el secreto local,
#          que no entra a la imagen. Las pruebas se corren antes del push.
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# ---------- Etapa 2: ejecución ----------
# Solo el JRE: sin compilador, sin Gradle, sin código fuente. Imagen más pequeña.
FROM eclipse-temurin:25-jre
WORKDIR /app

# Del resultado de la etapa 1 solo se rescata el jar.
COPY --from=build /app/build/libs/*.jar app.jar

# La JVM lee JAVA_TOOL_OPTIONS al arrancar.
#   MaxRAMPercentage=70 → heap ≤ 70 % de los 512 MB del plan gratuito.
#   UseSerialGC         → recolector de un hilo: menos memoria, adecuado a 0.1 CPU.
#   Xss512k             → pila de 512 KB por hilo; Tomcat crea muchos hilos.
#   user.timezone       → hora de Colombia. El contenedor corre en UTC y las
#                         reglas del alojamiento (entrada 15:00, salida 11:00,
#                         no-show 22:00) quedarían cinco horas corridas.
#                         Es el mismo -Duser.timezone de sga/docs/despliegue.md.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k -Duser.timezone=America/Bogota"

# Documenta el puerto local. En Render manda PORT (server.port=${PORT:8080}).
EXPOSE 8080

# Forma de arreglo: java es el proceso principal y recibe la señal de apagado de Render.
# Con WORKDIR /app, la H2 en archivo (./data/sgadb) queda en /app/data/sgadb.
ENTRYPOINT ["java", "-jar", "app.jar"]