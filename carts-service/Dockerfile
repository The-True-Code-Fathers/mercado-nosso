FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY carts-service/pom.xml ./carts-service/

COPY carts-service/src ./carts-service/src

RUN mvn clean package -pl carts-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS carts-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/carts-service/target/carts-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]