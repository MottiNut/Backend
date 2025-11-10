# === Etapa 1: Construcción del .jar ===
FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /build
COPY mottinut/pom.xml .
COPY mottinut/src ./src

RUN mvn clean package -DskipTests

# === Etapa 2: Imagen final para ejecución ===
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app
COPY --from=build /build/target/mottinut-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
