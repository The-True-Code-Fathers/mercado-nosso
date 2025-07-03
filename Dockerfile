# =============================================
#  Estágio 1: Builder (Compilação e Pacote)
# =============================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

ARG SERVICE_NAME
WORKDIR /app

# 1. Copia os arquivos pom.xml
COPY pom.xml .
COPY ${SERVICE_NAME}/pom.xml ./${SERVICE_NAME}/

# 2. Baixa as dependências focando apenas no POM do serviço
#    A flag '-f' direciona o Maven, evitando o erro de "módulo não encontrado".
RUN mvn -f ${SERVICE_NAME}/pom.xml dependency:go-offline \
    -Dmaven.repo.central=http://repo1.maven.org/maven2 \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true

# 3. Copia o código-fonte do serviço específico
COPY ${SERVICE_NAME}/src ./${SERVICE_NAME}/src

# 4. Compila e empacota a aplicação
RUN mvn -f ${SERVICE_NAME}/pom.xml clean package -DskipTests

# =============================================
#  Estágio 2: Runtime (Imagem Final)
# =============================================
FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copia apenas o artefato final (JAR) do estágio de build
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]