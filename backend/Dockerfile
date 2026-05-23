# Etapa 1 (Build)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar configuración de dependencias primero para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2 (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto del servidor
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
