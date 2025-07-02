FROM maven:3.9.6-eclipse-temurin-21 AS builder

ARG SERVICE_NAME
WORKDIR /app

COPY . .


RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests \
    -Dmaven.repo.central=http://repo1.maven.org/maven2 \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copia o JAR do serviço específico
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]