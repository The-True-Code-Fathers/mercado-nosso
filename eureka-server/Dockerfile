FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/

COPY eureka-server/src ./eureka-server/src

RUN mvn clean package -pl eureka-server -am -DskipTests

FROM maven:3.9.6-eclipse-temurin-21 AS eureka-server
VOLUME /tmp
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=application
COPY --from=builder /app/eureka-server/target/eureka-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]