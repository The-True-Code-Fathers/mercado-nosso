# 🛒 Mercado Nosso

Marketplace multi-serviços construído com arquitetura de microsserviços usando Spring Boot, Eureka para service discovery, e Docker Compose para orquestração. Cada serviço é containerizado e se comunica via HTTP REST, seguindo padrões de Adapter para integração.

## 🧩 Principais Serviços

- 🚪 **api-gateway**: Roteia requisições externas para os microsserviços internos.
- 📦 **orders-service**: Gerencia pedidos e vendas.
- 👤 **users-service**: Gerencia usuários (vendedores e compradores).
- 🏷️ **listings-service**: Gerencia anúncios de produtos.
- 🛍️ **carts-service**: Gerencia carrinhos de compra.
- ⭐ **reviews-service**: Gerencia avaliações de produtos.
- 🤖 **recommendation-service**: Serviço Python para recomendações.
- 🧭 **eureka-server**: Service discovery para todos os microsserviços.

## 🛠️ Stacks Utilizadas

- ☕ **Java (Spring Boot)**: Backend dos microsserviços.
- 🅰️ **Angular**: Frontend web do marketplace.
- 🐍 **Python**: Serviço de recomendação.
- 🍃 **MongoDB**: Persistência para pedidos e anúncios.
- 🐘 **PostgreSQL**: Persistência para usuários.
- 🐳 **Docker & Docker Compose**: Orquestração dos serviços.
- 🧭 **Eureka**: Service discovery.
- 📦 **Maven**: Build e gerenciamento de dependências.

## 🚀 Como Rodar o Projeto

1. **Pré-requisitos**:
   - 🐳 Docker e Docker Compose instalados
   - ☕ Java 17+ instalado (para builds locais)
   - 🐍 Python 3.8+ instalado (para recommendation-service local)

2. **Build dos Serviços** (opcional, se quiser buildar manualmente):
   ```zsh
   cd <serviço>
   mvn clean install
   ```

3. **Subir todos os serviços com Docker Compose**:
   ```zsh
   docker-compose up --build
   ```

4. **Acessar o sistema**:
   - 🅰️ Frontend Angular: `http://localhost:4200`
     [Repositório do frontend Angular](https://github.com/The-True-Code-Fathers/mercado-nosso-frontend)
