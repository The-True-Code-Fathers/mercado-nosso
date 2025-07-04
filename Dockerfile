FROM maven:3.9.6-eclipse-temurin-21 AS builder

ARG SERVICE_NAME
WORKDIR /app

COPY pom.xml .
COPY ${SERVICE_NAME}/pom.xml ./${SERVICE_NAME}/

RUN mvn -f ${SERVICE_NAME}/pom.xml dependency:go-offline \
    -Dmaven.repo.central=http://repo1.maven.org/maven2 \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

COPY ${SERVICE_NAME}/src ./${SERVICE_NAME}/src

RUN mvn -f ${SERVICE_NAME}/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]