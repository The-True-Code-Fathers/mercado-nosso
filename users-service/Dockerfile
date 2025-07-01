FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY users-service/pom.xml ./users-service/

COPY users-service/src ./users-service/src

RUN mvn clean package -pl users-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS users-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/users-service/target/users-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]