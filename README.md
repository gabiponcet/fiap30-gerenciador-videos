
# fiap30-gerenciador-videos

Sistema para gerenciamento de vídeos desenvolvido como parte de um desafio técnico (hackathon) da pós-graduação de Arquitetura de Software da FIAP. O projeto tem como objetivo permitir a criação, exclusão e consulta de vídeos, e está preparado para deploy com Docker e automações via GitHub Actions.

## :computer: Tecnologias Utilizadas

- Java;
- Gradle;
- Docker;
- GitHub Actions (CI/CD);
- Terraform (infraestrutura);
- RabbitMQ (mensageria);
- AWS S3 (armazenamento de vídeos e imagens);
- Keycloak (autenticação e autorização).

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

### 🧩 Visão de Alto Nível do Sistema

O sistema é dividido em módulos que interagem por eventos e fila de mensagens:

- `Video Upload Service`: recebe, autentica e armazena os vídeos. Publica eventos na fila;
- `Video Processing Service`: escuta eventos, extrai imagens dos vídeos e salva no bucket;
- `RabbitMQ`: realiza a comunicação assíncrona entre os serviços;
- `AWS S3`: armazena os vídeos e os arquivos .zip com as imagens extraídas;
- `Keycloak`: faz a autenticação dos usuários via OAuth2.

### 🔗 Integrações Externas

- **Keycloak**: autentica e autoriza os usuários;
- **RabbitMQ**: troca de mensagens entre serviços (como eventos `VideoUploaded` ou `VideoProcessingFailed`);
- **AWS S3**: armazenamento de arquivos de vídeo e imagens extraídas.

### 🌟 Vantagens

- Alta coesão e baixo acoplamento;
- Fácil expansão por eventos (Event-Driven Architecture);
- Suporte a escalabilidade horizontal;
- Pronto para ambientes em nuvem (Cloud-Ready);
- Suporte a autenticação robusta com Keycloak;
- Comunicação resiliente com RabbitMQ.

## :bookmark_tabs: [Diagramas](./docs)

- [x] Diagrama de Componentes (Upload);
- [x] Diagrama de Containers (Sistemas);
- [x] Diagrama de Integração de Sistemas (Keycloak, S3, RabbitMQ).

*Todos os arquivos estão disponíveis na pasta [`docs/`](./docs).*
