# ----------------------------------------------------------------------
# ETAPA 1: BUILD (Compilación)
# Utilizamos la imagen oficial y recomendada de Eclipse Temurin JDK 17.
# Esto soluciona el error "not found" que tenías con 'openjdk:17-jdk-slim'.
FROM eclipse-temurin:17-jdk-jammy AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar archivos esenciales para la compilación (pom.xml y Maven Wrapper)
# Este paso optimiza el cache de Docker.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código fuente
COPY src src

# Compilar el proyecto y empaquetar el JAR, saltando las pruebas
RUN ./mvnw clean package -DskipTests

# ----------------------------------------------------------------------

# ETAPA 2: RUNTIME (Ejecución)
# Usamos Eclipse Temurin JRE 17, una imagen ligera para la ejecución final.
FROM eclipse-temurin:17-jre-jammy

# Directorio de trabajo
WORKDIR /app

# Copia el JAR generado desde la etapa de build a la etapa final de runtime
COPY --from=build /app/target/*.jar app.jar

# 1. Documentación: Puerto de escucha por defecto de Spring Boot
EXPOSE 8080

# 2. COMANDO DE EJECUCIÓN (CMD)
# Esta línea es CRÍTICA para Railway:
# Usa -Dserver.port=${PORT:-8080} para forzar a Spring Boot a escuchar
# el puerto dinámico ($PORT) que Railway le asigna.
CMD java -Dserver.port=${PORT:-8080} -jar app.jar
