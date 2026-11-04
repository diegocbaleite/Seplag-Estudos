# SEPLAG Estudos — API REST com Java e Spring Boot

Projeto pessoal desenvolvido **para estudos, prática e evolução no desenvolvimento backend com Java**. A aplicação gerencia artistas, álbuns e capas, reunindo autenticação, persistência de dados e integração com serviços externos em uma API REST.

O escopo foi inspirado no desafio Back-End Java do **Anexo II-A do Edital nº 001/2026/SEPLAG**, utilizado como referência para exercitar requisitos de um projeto prático.

> **Finalidade educacional:** este é um projeto independente de estudos e portfólio. Não possui vínculo oficial com a SEPLAG e não representa inscrição, participação ou entrega oficial no processo seletivo.

## Objetivos de aprendizado

- Praticar a construção de APIs REST com Java 21 e Spring Boot.
- Organizar o código em camadas, separando endpoints, regras de negócio e acesso a dados.
- Trabalhar com relacionamentos JPA, consultas, paginação e migrações de banco.
- Aplicar autenticação JWT, validação de dados e tratamento de erros.
- Integrar armazenamento de imagens com MinIO e notificações com WebSocket.
- Exercitar testes unitários, documentação da API e execução com Docker Compose.

## Tecnologias

| Área | Tecnologias |
|---|---|
| Linguagem e build | Java 21 e Maven |
| API e persistência | Spring Boot, Spring Data JPA/Hibernate e Bean Validation |
| Banco de dados | PostgreSQL e Flyway |
| Segurança | Spring Security e JWT |
| Armazenamento de imagens | MinIO |
| Notificações | WebSocket com STOMP |
| Documentação e monitoramento | Swagger/OpenAPI e Spring Boot Actuator |
| Testes | JUnit 5 e Mockito |
| Ambiente de execução | Docker e Docker Compose |

## Funcionalidades

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

## Executar pelo IntelliJ IDEA (Java 21)

1. Utilize a branch `main`: execute `git checkout main` e `git pull origin main`.
2. Abra o `pom.xml` como projeto Maven e configure o **Project SDK** como Java 21.
3. Em **Run > Edit Configurations**, escolha a classe principal **`br.com.seplag.SeplagApplication`**. Remova configurações antigas que iniciem `br.com.seplag.Application`.
4. Configure as variáveis de ambiente necessárias, sem gravar senhas no repositório: `APP_AUTH_PASSWORD`, `APP_JWT_SECRET` (Base64 de 32 bytes), `DB_URL`, `DB_USER`, `DB_PASSWORD`, `MINIO_URL`, `MINIO_ROOT_USER` e `MINIO_ROOT_PASSWORD`. Para o banco local existente, use as credenciais **do seu PostgreSQL** (os valores padrão do `application.yml` são apenas para o ambiente demonstrativo).
5. Inicie PostgreSQL e MinIO, depois execute `mvn clean test` e **Run 'SeplagApplication'**.
6. Confirme no console `Starting SeplagApplication` e `Found 4 JPA repository interfaces` antes de abrir `http://localhost:8080/swagger-ui/index.html`.

O projeto usa `src/main/resources/application.yml`. Não crie um `application.properties` com configurações duplicadas. A rota `/login` **não** é utilizada: a autenticação é por `POST /api/v1/auth/login`, seguida do botão **Authorize** no Swagger, colando apenas `accessToken`. Quando o Swagger estiver autorizado, o Curl de endpoints protegidos deverá apresentar o cabeçalho `Authorization: Bearer ...`. Tokens de acesso expiram em cinco minutos.

## Autenticação

Autentique-se com o usuário e a senha configurados no ambiente:

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

## Organização dos pacotes (arquitetura em camadas)

```text
src/main/java/br/com/seplag/
├── SeplagApplication.java
├── model/       # Entidades JPA: Artista, Album, CapaAlbum, Regional e TipoArtista
├── repository/  # Spring Data JPA
├── service/     # Regras de negócio, MinIO e regionais
├── controller/  # Endpoints REST versionados
├── dto/         # Objetos de entrada e saída
├── security/    # JWT, validação de origem e rate limit
├── config/      # Spring Security, WebSocket e OpenAPI
├── exception/   # Tratamento de erros
└── event/       # Notificação de álbum criado

src/test/java/br/com/seplag/
├── controller/
├── service/
└── security/
```

Os controllers recebem as requisições, os services concentram as regras de negócio e os repositories realizam o acesso ao banco de dados.

## Arquitetura

O fluxo principal segue **Controller → Service → Repository → PostgreSQL**.

Os serviços também integram o MinIO para armazenar capas, publicam eventos de novos álbuns para notificações via WebSocket e consultam a API externa para sincronizar regionais.

Artistas e álbuns possuem relacionamento muitos-para-muitos na tabela `artista_album`. A tabela `regional` tem `id`, `nome`, `ativo` e `codigo_externo`: esse último campo permite associar cada versão do registro ao identificador retornado pela API externa, preservando histórico de alterações. A sincronização usa mapas para comparação linear em relação ao volume de registros.

## Testes e validação

Execute os testes automatizados com JDK 21 e Maven:

```bash
mvn clean test
```

Para validar as integrações, inicie os serviços com Docker Compose e confira:

- Autenticação, renovação de tokens e acesso aos endpoints protegidos.
- Cadastro, atualização, filtros e paginação de artistas e álbuns.
- Upload de capas e acesso pelas URLs pré-assinadas.
- Notificações de novos álbuns no tópico WebSocket.
- Sincronização de regionais, quando a API externa estiver disponível.

Os testes unitários não substituem a validação das integrações com PostgreSQL, MinIO e o serviço externo.

## Decisões e limitações do estudo

- **Autenticação:** utiliza um usuário configurado por variáveis de ambiente para simplificar o cenário de estudo.
- **Limite de requisições:** o controle é mantido em memória por instância. Uma execução distribuída exigiria um mecanismo compartilhado.
- **Origens:** CORS e a verificação de `Origin` restringem chamadas de navegadores; a autenticação JWT protege os endpoints.
- **Integração externa:** a sincronização de regionais depende da disponibilidade e do formato de resposta do serviço consultado.
- **Escopo:** o projeto concentra-se no backend; as funcionalidades podem ser exploradas pelo Swagger ou por um cliente HTTP.

O repositório registra a aplicação prática dos conteúdos estudados e pode receber melhorias conforme o aprendizado avança.
