# Biblioteca API

API REST em Spring Boot para gerenciamento de Livros, Autores, Usuários e Empréstimos.

## Requisitos
- Docker & Docker Compose (recomendado) **ou** Java 17 + Maven instalado localmente.

## Rodando com Docker (recomendado)
1. Extraia este projeto (se estiver zipado).
2. No diretório raiz do projeto, execute:
```bash
docker compose up --build
```
3. A API ficará disponível em: `http://localhost:8080`
4. Swagger UI: `http://localhost:8080/swagger-ui.html`
5. PostgreSQL: `localhost:5432` (db `biblioteca`, user `postgres`, senha `postgres`)

## Rodando localmente (sem Docker)
1. Tenha Java 17 e Maven instalados.
2. Configure a conexão no arquivo `src/main/resources/application.yml` se necessário.
3. No diretório do projeto execute:
```bash
./mvnw spring-boot:run
```
ou
```bash
mvn spring-boot:run
```

## Testes
```bash
./mvnw -q -Dtest=SmokeTest test
```

## Endpoints principais
- `GET /livros`, `GET /livros/{id}`, `POST /livros`, `PUT /livros/{id}`, `DELETE /livros/{id}`
- `GET /autores`, `GET /autores/{id}`, `POST /autores`, `PUT /autores/{id}`, `DELETE /autores/{id}`
- `GET /usuarios`, `GET /usuarios/{id}`, `POST /usuarios`, `PUT /usuarios/{id}`, `DELETE /usuarios/{id}`
- `POST /emprestimos` body: `{"livroId":1,"usuarioId":1}`
- `PUT /emprestimos/{id}/devolucao`

## Observações
- Swagger (springdoc) disponibiliza documentação OpenAPI automaticamente.
- HATEOAS: respostas incluem links `self`, `update`, `delete` e links contextuais `emprestar`/`devolver`.
