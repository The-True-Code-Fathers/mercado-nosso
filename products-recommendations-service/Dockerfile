FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY products-recommendations-service/pom.xml ./products-recommendations-service/

COPY products-recommendations-service/src ./products-recommendations-service/src

RUN mvn clean package -pl products-recommendations-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS products-recommendations-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/products-recommendations-service/target/products-recommendations-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]