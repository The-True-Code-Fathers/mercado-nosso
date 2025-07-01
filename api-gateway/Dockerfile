FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/

COPY api-gateway/src ./api-gateway/src

RUN mvn clean package -pl api-gateway -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS api-gateway
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]