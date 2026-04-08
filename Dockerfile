# ===== Stage 1: Build con Maven =====
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Descarga dependencias primero (cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compila el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Stage 2: Imagen final liviana =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]
