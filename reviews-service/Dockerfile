FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY reviews-service/pom.xml ./reviews-service/

COPY reviews-service/src ./reviews-service/src

RUN mvn clean package -pl reviews-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS reviews-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/reviews-service/target/reviews-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]