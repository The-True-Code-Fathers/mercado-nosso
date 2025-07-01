FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY catalog-service/pom.xml ./catalog-service/

COPY catalog-service/src ./catalog-service/src

RUN mvn clean package -pl catalog-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS catalog-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/catalog-service/target/catalog-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]