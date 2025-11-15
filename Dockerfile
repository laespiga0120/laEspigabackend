# Etapa 1: Build - Usamos JDK 17 para compilar el código.
FROM openjdk:17-jdk-slim AS build

# Directorio de trabajo
WORKDIR /app

# Copiar Maven Wrapper y pom.xml (optimización del cache de dependencias)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código
COPY src src

# Compilar el proyecto y empaquetar el JAR
RUN ./mvnw clean package -DskipTests

# ----------------------------------------------------------------------

# Etapa 2: Runtime - Imagen ligera con solo el JRE para ejecución.
FROM openjdk:17-jre-slim

# Directorio de trabajo
WORKDIR /app

# Copia el JAR generado desde la etapa de build
# NOTA: El asterisco (*) funciona bien si solo hay un JAR en target/
COPY --from=build /app/target/*.jar app.jar

# Configuración de Puertos y Ejecución

# 1. EXPONER el puerto por defecto (solo documentación del contenedor)
EXPOSE 8080

# 2. Comando de Ejecución (CMD)
# La clave para Railway: usa '-Dserver.port=${PORT:-8080}'
# Esto le dice a Spring Boot que use la variable de entorno 'PORT' inyectada por Railway.
# Si 'PORT' no está definida (ej. ejecución local), por defecto usará '8080'.
CMD java -Dserver.port=${PORT:-8080} -jar app.jar
