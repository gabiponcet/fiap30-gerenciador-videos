# fiap30-gerenciador-videos

Sistema para gerenciamento de vídeos desenvolvido como parte de um desafio técnico (hackathon) da pós graduação de Arquitetura de Software da FIAP. O projeto tem como objetivo permitir a criação, exclusão e consulta de vídeos, e está preparado para deploy com Docker e automações via GitHub Actions.

## :computer: Tecnologias Utilizadas

- Java
- Gradle
- Docker
- GitHub Actions (CI/CD)
- Terraform (infraestrutura)

## :wrench: Como rodar o projeto localmente

### ⚙️ Pré-requisitos

- Java 17+
- Docker
- Git
- Gradle (ou use o wrapper incluso)

### 🚀 Passos

```bash
# Clone o repositório
$ git clone https://github.com/seu-usuario/fiap30-gerenciador-videos.git
$ cd fiap30-gerenciador-videos

# Construa a aplicação
$ ./gradlew build

# Rode com Docker
$ docker build -t gerenciador-videos .
$ docker run -p 8080:8080 gerenciador-videos
```

A API estará disponível em: `http://localhost:8080`

## :file_folder: Estrutura do Projeto

```
├── application/               # Casos de uso da aplicação (Application Layer)
├── domain/                   # Entidades e regras de negócio (Domain Layer)
├── infrastructure/           # Conectores externos e configurações (Infra Layer)
├── .github/workflows/        # Pipelines de CI/CD e Terraform
├── Dockerfile                # Configuração do container Docker
├── build.gradle              # Configuração do Gradle
```

## 🧱 Arquitetura do Projeto

O projeto segue uma abordagem inspirada em **Arquitetura em Camadas**, com boas práticas de **Domain-Driven Design (DDD)**, permitindo um código mais organizado, testável e preparado para evoluções futuras.

### 🗺️ Visão Geral das Camadas

```
             +------------------------+
             |      API / Web Layer   |
             |   (Controllers, DTOs)  |
             +------------------------+
                         ↓
             +------------------------+
             |  Application Layer     |
             | (UseCases, Commands)   |
             +------------------------+
                         ↓
             +------------------------+
             |    Domain Layer        |
             | (Entities, Aggregates) |
             +------------------------+
                         ↓
             +------------------------+
             | Infrastructure Layer   |
             | (DB, External Services)|
             +------------------------+
```

### 🧩 Camadas no Projeto

#### 1️⃣ **Camada de Aplicação (`application/`)**
Responsável pela lógica de orquestração e casos de uso da aplicação, como criar, editar ou excluir vídeos.

#### 2️⃣ **Camada de Domínio (`domain/`)**
Contém o núcleo do sistema: entidades, regras de negócio e contratos de repositórios. Essa camada é isolada de tecnologias externas.

#### 3️⃣ **Camada de Infraestrutura (`infrastructure/`)**
Implementa os contratos definidos no domínio, como acesso a banco de dados, serviços externos e configurações da aplicação.

#### 4️⃣ **Camada de API / Web**
É responsável por expor endpoints públicos da aplicação (REST). Pode estar incluída dentro de `infrastructure/` ou em outro módulo separado.

### 🧰 Suporte à Arquitetura

- **Gradle multi-módulo**, separando claramente responsabilidades
- **Docker**, para empacotar e executar a aplicação
- **GitHub Actions**, para automações de CI/CD
- **Terraform**, para provisionamento da infraestrutura

### 🌟 Vantagens

- Alta coesão e baixo acoplamento
- Facilidade para testes unitários
- Código preparado para crescer e se adaptar
