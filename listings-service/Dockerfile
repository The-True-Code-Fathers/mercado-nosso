FROM maven:3.9.9-openjdk-21 AS build
WORKDIR /app
COPY ../api-gateway/pom.xml .
RUN mvn dependency:go-offline
COPY ../api-gateway/src ./src
RUN mvn package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]