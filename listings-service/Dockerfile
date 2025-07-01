FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY listings-service/pom.xml ./listings-service/

COPY listings-service/src ./listings-service/src

RUN mvn clean package -pl listings-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS listings-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/listings-service/target/listings-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]