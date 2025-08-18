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
  - `domain` (negócio puro): modelos (`Agenda`, `Session`, `Vote`, `VoteResult`) e portas (`AgendaPort`, `CpfValidationPort`).
  - `infrastructure` (adapters): mapeadores entidade⇄domínio, repositório reativo (`AgendaCycleRepository`) e adapters (`AgendaCycleAdapter`, `CpfValidationFakeClient`).
  - `api` (interface): controllers reativos (`HealthController`, `AgendaController`), DTOs e mapeadores (`AgendaMapper`, `SessionMapper`).
  - `config`: configuração de WebFlux, Mongo reativo e OpenAPI.
  - `common`: enums, validações e tratamento global de exceções reativas.
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
- `GET /api/v1/agenda/{id}` — busca por id interno
- `GET /api/v1/agenda/agenda/{agendaId}` — busca por `agendaId`
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

Suba apenas a infraestrutura necessária e rode o k6 via Compose:
```bash
docker compose up -d mongodb
docker compose run --rm \
  -e BASE_URL=http://host.docker.internal:8080/api/v1/agenda \
  k6 run /load-test/vote-load-test.js
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
```bash
./gradlew test
```

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

---
Autor: Maike Naysinger Borges, veja também a documentação no Swagger UI.


