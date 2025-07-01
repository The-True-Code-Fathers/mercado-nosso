FROM maven:3.9.6-eclipse-temurin-21 AS builder

ARG SERVICE_NAME
WORKDIR /app

COPY . .

RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]