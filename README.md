## Voting Solution — Sistema de Votação Reativo

### Bem vindo ao readme deste projeto. Para mais informações sobre o meu perfil profissional, acesse https://naysinger.tech

Este é um serviço backend reativo para gerenciamento de agendas (pautas), sessões de votação e votos, pensado para alto tráfego e concorrência. Implementado com Spring WebFlux e MongoDB reativo.

### Tecnologias envolvidas
- **Linguagem/Runtime**: Java 21, Gradle Wrapper
- **Framework**: Spring Boot 3.5 (WebFlux, Validation)
- **Persistência**: Spring Data MongoDB Reactive
- **Documentação**: springdoc-openapi (Swagger UI)
- **Reatividade**: Project Reactor, Netty
- **Infra local**: Docker, MongoDB, Mongo Express
- **Testes**: JUnit 5, Reactor Test, Testcontainers (MongoDB)
- **Carga**: k6 (via Docker)

### Arquitetura proposta
- **Estilo**: Arquitetura Hexagonal (Ports & Adapters) com reatividade end-to-end.
- **Estrutura de Diretórios**:

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── br
│   │   │       └── com
│   │   │           └── naysinger
│   │   │               ├── VotingSolutionApplication.java  // Ponto de entrada
│   │   │               ├── api                             // Camada de API (Controllers, DTOs, Mappers)
│   │   │               │   ├── controller
│   │   │               │   │   └── v1
│   │   │               │   │       └── AgendaController.java
│   │   │               │   ├── dto
│   │   │               │   └── mapper
│   │   │               ├── common                          // Código comum (Enums, Exceptions, Paginação)
│   │   │               │   ├── enums
│   │   │               │   ├── exception
│   │   │               │   └── pagination
│   │   │               ├── config                          // Configurações do Spring (Mongo, Swagger, WebFlux)
│   │   │               ├── domain                          // Core da aplicação (Modelos de domínio e Ports)
│   │   │               │   ├── model
│   │   │               │   └── port
│   │   │               ├── infrastructure                  // Adapters de infraestrutura (MongoDB, Clientes HTTP)
│   │   │               │   ├── adapter
│   │   │               │   ├── entity
│   │   │               │   ├── mapper
│   │   │               │   └── repository
│   │   │               └── service                         // Orquestração da lógica de negócio
│   │   └── resources
│   │       └── application-local.yml
│   └── test
│       ├── java
│       │   └── br
│       │       └── com
│       │           └── naysinger
│       │               ├── integration
│       │               │   └── AgendaControllerIntegrationTest.java
│       │               └── service
│       │                   └── AgendaServiceTest.java
│       └── resources
│           └── application-test.yml
├── build.gradle.kts                        // Build script do Gradle
├── gradlew                                 // Gradle Wrapper
├── docker-compose.yml                      // Orquestração de contêineres Docker
├── init-mongo                              // Scripts de inicialização do MongoDB
│   └── create-collection.js
└── load-test                               // Testes de carga com k6
    └── vote-load-test.js
```

- **Modelo de dados**: Agregado `Agenda` contendo uma lista de `Session`, cada uma com seus `Votes`. Otimizado para consultas e gravações no agregado (trade-off: crescimento do documento vs. simplicidade e atomicidade do uso típico).

### Por que WebFlux e MongoDB para alta concorrência
- **WebFlux (não-bloqueante)**: thread model orientado a eventos com backpressure permite servir muitas conexões simultâneas com menor consumo de recursos sob I/O intensivo (CPU bound → escalar com mais instâncias).
- **MongoDB reativo**: driver non-blocking, alta taxa de escrita/leitura e flexibilidade de schema para evolução do domínio de votação. Modelo de documento se alinha ao agregado de domínio (`Agenda`→`Session`→`Vote`).
- **Resultado**: melhor utilização de CPU/memória em cenários com muita espera por I/O (DB/rede), latência consistente sob pico e caminho claro para escalabilidade horizontal.

### Cockpit (execução local)
Pré-requisitos: Java 21, Docker e Docker Compose.

1) Subir infraestrutura (MongoDB e Mongo Express)
```bash
docker compose up -d mongodb mongo-express
```
- MongoDB: `localhost:27017` (user: `admin`, pass: `voting123`, db: `voting-solution`)
- Mongo Express: `http://localhost:8081` (user: `admin`, pass: `admin`)

2) Executar a aplicação com perfil local
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

3) Endpoints úteis
- Health: `GET /api/v1/health` (Endoint de liveness check)
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/api-docs`

### Endpoints principais (v1)
- `POST /api/v1/agenda` — cria agenda (opcionalmente já com sessão)
- `GET /api/v1/agenda/{agendaId}` — busca por `agendaId`
- `GET /api/v1/agenda/session/{sessionId}` — busca por `sessionId`
- `GET /api/v1/agenda` — lista agendas
- `GET /api/v1/agenda/active` — agendas com sessões ativas
- `POST /api/v1/agenda/{agendaId}/sessions` — cria sessão para agenda
- `POST /api/v1/agenda/session/{sessionId}/vote` — registra voto
- `POST /api/v1/agenda/session/{sessionId}/close` — fecha sessão
- `POST /api/v1/agenda/{agendaId}/close` — fecha agenda e sessões abertas
- `GET /api/v1/agenda/session/{sessionId}/result` — resultado quando sessão fechada

Exemplos rápidos:
```bash
# Criar agenda com sessão para começar em 2 min, duração 3 min
curl -sS -X POST http://localhost:8080/api/v1/agenda \
  -H 'Content-Type: application/json' \
  -d '{
    "title":"Assembleia 01",
    "description":"Votação de pauta X",
    "createdBy":"admin",
    "sessionStartTime":"'$(date -v+2M +%Y-%m-%dT%H:%M:%S)'",
    "sessionDurationMinutes":3
  }'

# Votar
curl -sS -X POST http://localhost:8080/api/v1/agenda/session/<SESSION_ID>/vote \
  -H 'Content-Type: application/json' \
  -d '{"userId":"u1","cpf":"12345678909","voteType":"YES"}'

# Fechar sessão e consultar resultado
curl -sS -X POST http://localhost:8080/api/v1/agenda/session/<SESSION_ID>/close
curl -sS http://localhost:8080/api/v1/agenda/session/<SESSION_ID>/result
```

### Teste de carga (k6)
O projeto inclui um cenário k6 em `load-test/vote-load-test.js` que:
- Cria uma agenda com sessão futura curta no `setup()`
- Dispara votos contínuos na mesma sessão, com CPFs válidos gerados
- Gera relatórios (`/load-test/summary.html` e `/load-test/summary.json`)

Com o container MongoDB rodando, aplique o comando k6 abaixo no terminal:
```bash
docker compose run --rm -e BASE_URL=http://host.docker.internal:8080/api/v1/agenda k6 run /load-test/vote-load-test.js
```

Observação: no script atual, `RATE`, `TIME_UNIT` e `DURATION` estão fixos no código (não lidos de env). O comando acima funciona, mas esses valores só terão efeito após adaptar o script para ler variáveis de ambiente.

Saída:
- Resumo no stdout
- Artefatos em `load-test/summary.html` e `load-test/summary.json`

#### Exemplo de relatório (HTML) do k6

![Exemplo de relatório do k6](/load-test/report_example.png)

### Regras de negócio aplicadas
- **Criação de sessão**: início deve ser no futuro e duração mínima de 1 minuto.
- **Somente uma sessão ativa** por agenda; se existir sessão aberta/não expirada, bloquear nova criação.
- **Janela de votação**: só é permitido votar se a sessão já começou e ainda não expirou (status `OPEN` e no intervalo).
- **Unicidade por CPF na sessão**: um mesmo CPF não pode votar duas vezes na mesma sessão (retorna 409 – `DuplicateCpfException`).
- **Fechamento**: é possível fechar sessão específica ou fechar a agenda (encerra todas as sessões abertas). Não é permitido fechar agenda já fechada.
- **Resultado**: só disponível para sessões fechadas; retorna totais e vencedor (`SIM`, `NAO` ou `EMPATE`).
- **Validação de CPF (fake)**: adapter simula latência e respostas; CPFs não aptos/ inválidos retornam 404.
- **Tratamento de erros**: payloads de erro padronizados via `GlobalExceptionHandler` com `timestamp`, `status`, `error` e `message`.

### Configurações relevantes (perfil local)
- Arquivo: `src/main/resources/application-local.yml`
  - Pool e timeouts do Mongo reativo ajustáveis
  - Logging detalhado para desenvolvimento
- Conexão Mongo: `mongodb://admin:voting123@localhost:27017/voting-solution?authSource=admin`
- Observação: o Spring Data criará automaticamente a coleção caso não exista.

### Como rodar testes
Para executar os testes, é necessário ter o Java 17 ou superior instalado.

```bash
./gradlew test
```

Os seguintes testes foram criados para garantir a qualidade e o comportamento esperado da aplicação:

- **Testes de Integração**:
    - `AgendaControllerIntegrationTest`: Testa os endpoints da API, simulando requisições HTTP e validando as respostas. Utiliza o Testcontainers para levantar um banco de dados MongoDB em um contêiner Docker, garantindo um ambiente de teste isolado e consistente.
- **Testes de Unidade**:
  - `AgendaServiceTest`: Testa a lógica de negócio do `AgendaService` de forma isolada, utilizando mocks para simular as dependências externas.
- **Testes de Contexto**:
  - `VotingSolutionApplicationTests`: Testa se o contexto da aplicação Spring sobe corretamente.


### Dicas de operação e desempenho
- Escale horizontalmente múltiplas instâncias (stateless) atrás de um balanceador.
- Monitore tempos de resposta do driver reativo e latência do Mongo; ajuste pool/timeouts conforme tráfego.
- Se o volume de votos por sessão crescer muito, considerar particionar votos em coleção própria com agregações ou usar padrões como outbox/event streaming para contabilização.
- Considere idempotência por `userId`/`cpf`/`sessionId` em chamadas externas e limites de taxa.

### Roadmap (sugestões)
- Idempotência e proteção contra replays
- Índices compostos/TTL para sessões expiradas
- Métricas Prometheus + dashboards
- Cache de validação de CPF (com TTL) ou client real
- Suporte a leitura de RATE/TIME_UNIT/DURATION via env no script k6

### Débito Técnico
- **Autenticação**: A API atualmente não possui um sistema de autenticação e autorização, sendo um ponto crítico para ambientes produtivos.
- **Paginação**: A classe de paginação foi criada em `common/pagination`, mas ainda não foi implementada na lógica dos retornos das listagens, o que pode causar problemas de performance com grandes volumes de dados.
- **Índices do Mongo**: O MongoDB performa bem em cenários de baixa volumetria, mas para garantir a performance com massividade de dados, é necessário criar índices simples e compostos nas coleções.
- **Testes**: A cobertura de testes pode ser melhorada, principalmente nos cenários de exceção e validação de dados de entrada.

---
Autor: Maike Naysinger Borges, veja também a documentação no Swagger UI.


