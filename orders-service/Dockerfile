FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY orders-service/pom.xml ./orders-service/

COPY orders-service/src ./orders-service/src

RUN mvn clean package -pl orders-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS orders-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/orders-service/target/orders-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]