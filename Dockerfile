# ==================================================================
#  Stage 1: Resolve and download dependencies
# ==================================================================
FROM maven:3.9.6-eclipse-temurin-21 AS deps

WORKDIR /app

# Copy all pom.xml files first. This creates a cacheable layer for dependencies.
# If only source code changes, Docker will use the cache from this point.
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY auth-service/pom.xml ./auth-service/
COPY carts-service/pom.xml ./carts-service/
COPY catalog-service/pom.xml ./catalog-service/
COPY eureka-server/pom.xml ./eureka-server/
COPY listings-service/pom.xml ./listings-service/
COPY orders-service/pom.xml ./orders-service/
COPY products-recommendations-service/pom.xml ./products-recommendations-service/
COPY products-service/pom.xml ./products-service/
COPY reviews-service/pom.xml ./reviews-service/
COPY seller-daily-analytics-service/pom.xml ./seller-daily-analytics-service/
COPY users-service/pom.xml ./users-service/

# Download all dependencies
RUN mvn dependency:go-offline \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

# ==================================================================
#  Stage 2: Build the specific service using the cached dependencies
# ==================================================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Copy the pre-downloaded dependencies from the 'deps' stage.
COPY --from=deps /root/.m2/repository /root/.m2/repository

# Copy the pom.xml files again, followed by the source code.
# This ensures that we can build the project.
COPY . .

# Clean and package only the specified service and its dependencies (-am).
RUN mvn -e clean package -pl ${SERVICE_NAME} -am -DskipTests \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

RUN mvn -f ${SERVICE_NAME}/pom.xml clean package -DskipTests
# ==================================================================
#  Stage 3: Runtime
# ==================================================================
FROM eclipse-temurin:21-jre-jammy

ARG SERVICE_NAME

# Create a non-root user for better security
RUN groupadd -r appgroup && useradd -r -s /bin/false -g appgroup appuser
USER appuser

WORKDIR /home/appuser

COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]