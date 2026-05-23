# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente y generamos el .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el ejecutable generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto estándar 8080. Railway lo detectará automáticamente.
EXPOSE 8080

# Opcional: nos aseguramos de que Spring Boot lea la variable PORT que inyecta Railway
ENV SERVER_PORT=${PORT:-8080}

# Comando de ejecución del servidor
ENTRYPOINT ["java", "-jar", "app.jar"]
