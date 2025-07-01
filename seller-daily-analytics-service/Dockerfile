FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY seller-daily-analytics-service/pom.xml ./seller-daily-analytics-service/

COPY seller-daily-analytics-service/src ./seller-daily-analytics-service/src

RUN mvn clean package -pl seller-daily-analytics-service -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS seller-daily-analytics-service
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/seller-daily-analytics-service/target/seller-daily-analytics-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]