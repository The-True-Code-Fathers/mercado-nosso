FROM maven:3.9.6-eclipse-temurin-21 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Configuração de DNS para resolver problemas de rede
RUN echo "nameserver 8.8.8.8" > chmod +x /etc/resolv.conf
RUN echo "nameserver 8.8.4.4" >> chmod +x /etc/resolv.conf

# Copia todo o projeto (mais simples e eficiente)
COPY . .

# Configura Maven para usar repositório central e retry
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests \
    -Dmaven.repo.central=https://repo1.maven.org/maven2 \
    -Dmaven.wagon.http.retryHandler.count=3

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copia o JAR do serviço específico
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]