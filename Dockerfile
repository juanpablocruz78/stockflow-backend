# Usar imagen oficial de Java
FROM eclipse-temurin:21-jdk-alpine

# Crear directorio de trabajo
WORKDIR /app

# Copiar jar generado
COPY target/stockflow-backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]