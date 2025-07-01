FROM maven:latest
VOLUME /tmp
ARG PROJECT_VERSION=1.0.0
RUN mkdir -p /home/app
WORKDIR /home/app
ENV SPRING_PROFILES_ACTIVE application
COPY ./ .
ADD target/catalog-service-1.0.0-SNAPSHOT.jar catalog-service-1.0.0-SNAPSHOT.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "catalog-service-1.0.0-SNAPSHOT.jar"]
CMD ["mvn", "spring-boot:run"]