# Copilot Instructions for mercado-nosso

## Big Picture Architecture
- This is a multi-service marketplace system using Spring Boot microservices, Docker Compose, and Eureka for service discovery.
- Main services: `api-gateway`, `orders-service`, `users-service`, `listings-service`, `carts-service`, `reviews-service`, `recommendation-service`, `eureka-server`.
- Data sources: MongoDB (orders, listings), PostgreSQL (users), others as needed.
- Services communicate via HTTP REST, with adapters encapsulating inter-service calls (see `*ServiceAdapter` classes).
- Service URLs are managed via Eureka and/or `application.yml` configs.

## Developer Workflows
- **Build:** Use Maven (`mvn clean install`) in each service directory. Docker Compose builds all services together.
- **Run:** Use `docker-compose up --build` from the project root to start all services and dependencies.
- **Test:** Unit/integration tests are in each service's `src/test` directory. Run with `mvn test`.
- **Debug:** Logs are output to console; check each service's logs for errors. For PATCH/PUT/DELETE support in RestTemplate, ensure correct bean configuration (see below).

## Project-Specific Patterns & Conventions
- **Adapter Pattern:** All inter-service HTTP calls are encapsulated in `*ServiceAdapter` classes (e.g., `UsersServiceAdapter`). Controllers should call adapters, not RestTemplate directly.
- **RestTemplate PATCH Support:** Always configure RestTemplate with `HttpComponentsClientHttpRequestFactory` for PATCH requests:
  ```java
  @Bean
  public RestTemplate restTemplate() {
      return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
  }
  ```
- **DTOs:** Data transfer objects are in `adapters/in/dto` and `adapters/out/dto` folders per service.
- **Service Ports:** Domain logic is abstracted via ports/interfaces in `core/ports` and implemented in `core/usecases` or `adapters/out`.
- **Docker Networking:** Services communicate using Docker Compose service names (e.g., `http://users-service:8080`).
- **Service Startup:** Use `depends_on` in `docker-compose.yml` to control startup order.

## Integration Points & External Dependencies
- **Eureka:** All services register with `eureka-server` for service discovery.
- **MongoDB/PostgreSQL:** Defined in `docker-compose.yml` and used by respective services.
- **API Gateway:** Routes external requests to internal services.
- **Recommendation Service:** Python-based, communicates via REST API.

## Key Files & Directories
- `docker-compose.yml`, `docker-compose.override.yml`: Orchestration and service config.
- `api-gateway/`, `orders-service/`, etc.: Main service implementations.
- `src/main/java/com/mercadonosso/.../adapters/out/rest/*ServiceAdapter.java`: Inter-service communication pattern.
- `src/main/java/com/mercadonosso/.../config/BeanConfiguration.java`: RestTemplate bean config.
- `collections/`: Contains test and environment files for API endpoints.

## Example: Updating User Sales/Purchases
- Use `UsersServiceAdapter` in `orders-service` to PATCH user sales/purchases after order creation.
- Do not call RestTemplate directly in controllers; always use the adapter.

---

If any section is unclear or missing important project-specific details, please provide feedback to improve these instructions.


você vai ser me servo e meu professor senior, eu tenho esse repo que é um backedn spring java com eureka e um api-gateway, sao microsservicos containeres dentro um docker, agora eu to fazendo essa tela aqui: pode analisar meu repo INTEIRO, nao quero vc fazendo coisa sem eu pedir e fazendo coisa sozinho eu quero que vc me explique e me ajude a pensar e implementar, o user pode ser um vendedor ou nao, se ele for um vendedor ele tem os orders dele, e temos que puxar as informações das vendas e passar pra essa tela de dashboard