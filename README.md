# Desafio SEPLAG 001/2026 — Back-End Java Sênior

Implementação de estudos do **Anexo II-A** do Edital de Processo Seletivo Conjunto nº 001/2026/SEPLAG. Este repositório contém apenas o escopo do desafio Back-End Java do edital; não representa inscrição ou entrega oficial (os prazos originais encerraram em fevereiro de 2026).

## Tecnologias

Java 21, Spring Boot, Spring Data JPA/Hibernate, PostgreSQL, Flyway, JWT, MinIO, WebSocket (STOMP), Swagger/OpenAPI, Maven e Docker Compose. O edital permite Spring Boot ou Quarkus e exige o conjunto de funcionalidades; PostgreSQL é a opção de banco relacional adotada aqui.

## Requisitos implementados no código

- API REST versionada: `/api/v1`, com POST, PUT e GET.
- Artistas do tipo `CANTOR` ou `BANDA` e álbuns em relação N:N; carga inicial dos exemplos do edital via Flyway.
- Paginação de álbuns; listagem de artistas filtrada por nome e ordenada ASC/DESC; álbuns por tipo de artista.
- JWT com token de acesso de **5 minutos** e renovação com refresh token (validade de 24 horas).
- Filtro de origem e CORS restritos às origens configuradas, limite de **10 requisições/minuto por usuário autenticado**.
- Upload de múltiplas capas de álbum (JPG, PNG ou WebP) para MinIO, com URLs pré-assinadas válidas por **30 minutos**.
- Endpoint de importação e sincronização de regionais, usando a API externa informada no edital. Novas regionais são inseridas; ausentes são inativadas; alteradas preservam o registro anterior inativo e criam um novo ativo.
- Notificação STOMP/WebSocket em `/topic/albuns` após commit de álbum novo.
- Spring Actuator `/actuator/health`, `/actuator/health/liveness` e `/actuator/health/readiness`.
- Testes unitários com JUnit 5 e Mockito. Swagger/OpenAPI e execução Docker Compose da API, banco e MinIO.

## Executar (Docker)

É necessário ter Docker e Docker Compose.

1. Copie `.env.example` para `.env`.
2. Configure `APP_AUTH_PASSWORD`, `MINIO_ROOT_PASSWORD` e gere `APP_JWT_SECRET` usando `openssl rand -base64 32` (guarde o resultado no `.env`).
3. Execute `docker compose up --build -d`.
4. Aguarde a inicialização dos serviços; consulte `http://localhost:8080/actuator/health`.
5. Swagger: `http://localhost:8080/swagger-ui.html`; console MinIO: `http://localhost:9001`.

As URLs pré-assinadas são emitidas usando `MINIO_PUBLIC_URL` (padrão: `http://localhost:9000`), para que o navegador consiga acessá-las fora da rede interna do Docker.

## Executar localmente (Maven)

Use JDK 21 e Maven. Inicie PostgreSQL e MinIO, configure as mesmas variáveis de `.env.example` no ambiente e execute `mvn spring-boot:run`. Use `mvn test` para os testes. Os scripts Flyway criam as tabelas e inserem os artistas e álbuns automaticamente.

## Autenticação

Solicite acesso:

```http
POST /api/v1/auth/login
Content-Type: application/json

{"usuario":"admin","senha":"SENHA_DO_AMBIENTE"}
```

Use `Authorization: Bearer SEU_ACCESS_TOKEN` em requisições protegidas. O token expira em 5 minutos; renove com:

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{"refreshToken":"SEU_REFRESH_TOKEN"}
```

O refresh token é válido por 24 horas. O limite de 10 requisições por minuto é aplicado aos endpoints da API protegidos; utilize intervalos ao testar as funcionalidades. A configuração de usuário/senha via ambiente é adequada apenas ao cenário demonstrativo deste desafio. Em implantação distribuída, seria necessário centralizar o controle de limite, que nesta implementação é por instância.

## Endpoints

| Método | Caminho | Função |
|---|---|---|
| POST | `/api/v1/auth/login` | Autenticar |
| POST | `/api/v1/auth/refresh` | Renovar JWT |
| POST | `/api/v1/artistas` | Cadastrar artista |
| PUT | `/api/v1/artistas/{id}` | Atualizar artista |
| GET | `/api/v1/artistas/{id}` | Obter artista |
| GET | `/api/v1/artistas?nome=serj&asc=true&pagina=0&tamanho=10` | Buscar e ordenar artistas |
| POST | `/api/v1/albuns` | Cadastrar álbum |
| PUT | `/api/v1/albuns/{id}` | Atualizar álbum |
| GET | `/api/v1/albuns/{id}` | Obter álbum |
| GET | `/api/v1/albuns?tipo=CANTOR&pagina=0&tamanho=10` | Paginação e filtro por cantor/banda |
| POST | `/api/v1/albuns/{albumId}/capas` | Multipart com parâmetro `arquivos` (uma ou mais imagens) |
| GET | `/api/v1/albuns/{albumId}/capas` | URLs de capas com validade de 30 minutos |
| GET | `/api/v1/regionais` | Listar regionais ativas |
| POST | `/api/v1/regionais/sincronizar` | Importar e sincronizar regionais |
| WS | `/ws`, tópico `/topic/albuns` | Notificar novos álbuns |
| GET | `/actuator/health/{liveness,readiness}` | Estado da aplicação |

Exemplo de criação de álbum:
```json
{"titulo":"Novo Álbum","artistasIds":[1,2]}
```

Exemplo de artista:
```json
{"nome":"Serj Tankian","tipo":"CANTOR"}
```

## Arquitetura

```text
Controller → Service → Repository → PostgreSQL
                  ↘ MinIO (capas)
                  ↘ WebSocket (evento de álbum criado)
                  ↘ API externa (sincronização de regionais)
```

Artistas e álbuns possuem relacionamento muitos-para-muitos na tabela `artista_album`. A tabela `regional` tem `id`, `nome`, `ativo` e `codigo_externo`: esse último campo permite associar cada versão do registro ao identificador retornado pela API externa, preservando histórico de alterações. A sincronização usa mapas para comparação linear em relação ao volume de registros.

## Decisões, limites e prioridade

A prioridade foi atender o domínio, as consultas REST, a autenticação e os mecanismos exigidos na rubrica do Anexo III; na sequência, integrar recursos de nível sênior. JWT usa uma senha de usuário única configurada por ambiente para manter o exemplo mínimo. CORS e a validação do cabeçalho `Origin` restringem origens de navegadores, mas **não são garantia de bloqueio de clientes HTTP sem cabeçalho Origin**; o JWT protege os endpoints. O rate limiter em memória é apropriado à execução com uma única instância prevista no Compose. Não há interface front-end porque o edital permite escolher apenas o Back-End.

**Validação:** a inclusão do código no repositório não comprova automaticamente seu funcionamento. Antes de qualquer entrega, execute `mvn test`, `docker compose up --build`, testes da API, WebSocket, URLs pré-assinadas e sincronização de regionais com o serviço externo disponível.

