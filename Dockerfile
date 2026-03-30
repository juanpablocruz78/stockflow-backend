# Usar imagen oficial de Java
# ETAPA 1: Compilación (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copiar el pom y descargar dependencias (esto ayuda a la caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copiamos el JAR desde la etapa de compilación
COPY --from=build /app/target/stockflow-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]