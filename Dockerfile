# Etapa de build con Maven y JDK 17
FROM openjdk:17-jdk-slim AS build

# Directorio de trabajo
WORKDIR /app

# Copiar Maven Wrapper y pom.xml primero (para cachear dependencias)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código
COPY src src

# Compilar el proyecto y empaquetar el JAR
RUN ./mvnw clean package -DskipTests

# Etapa final: imagen ligera con solo el JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render expone dinámicamente el puerto en la variable de entorno PORT
EXPOSE 8080

# Ejecutar la aplicación usando la variable de puerto de Render
ENTRYPOINT ["java", "-jar", "app.jar"]
