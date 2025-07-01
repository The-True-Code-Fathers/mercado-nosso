FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY products-service/pom.xml .
COPY products-service/src ./src

COPY products-service/src ./products-service/src

RUN mvn clean package -pl products-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS products-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/products-service/target/products-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]