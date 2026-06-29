# Etapa 1: Build com Maven
FROM maven:3.8.5-eclipse-temurin-11 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Imagem final com JDK
FROM eclipse-temurin:11-jdk
WORKDIR /app

COPY --from=build /app/target/LojadeCarro-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
