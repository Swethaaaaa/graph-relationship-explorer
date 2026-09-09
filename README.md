# Graph-Based Relationship Explorer

A full-stack application for modeling and interactively exploring relationships
between entities (Users, Companies, Teams, Skills, Projects, Technologies) in a
professional-network domain, backed by Neo4j and built with Spring Boot + React.

> **Status:** Under active, phased development. This README is a working
> document and will be expanded into the full project README in the final
> phase (docs, screenshots, setup instructions, Neo4j-vs-Postgres rationale,
> etc.). See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the current
> architecture and design rationale.

## Repository layout

```
graph-relationship-explorer/
├── backend/     Spring Boot 3 (Java 17) REST API + WebSocket + Neo4j access
├── frontend/    React + TypeScript SPA (added in Phase 5)
├── docs/        Architecture, data model, and design-decision docs
└── docker-compose.yml   Local Neo4j (backend/frontend added in Phase 9)
```

## Development phases

- [x] Phase 1 — Repository structure and architecture
- [x] Phase 2 — Neo4j graph model and backend domain layer
- [x] Phase 3 — REST APIs (entities + relationships)
- [ ] Phase 4 — Graph traversal (Cypher-based)
- [ ] Phase 5 — React frontend
- [ ] Phase 6 — Graph visualization
- [ ] Phase 7 — WebSockets (real-time updates)
- [ ] Phase 8 — Tests
- [ ] Phase 9 — Docker
- [ ] Phase 10 — README and polish

## Running what exists so far (Phase 1)

Start Neo4j locally:

```bash
docker compose up -d neo4j
```

Neo4j Browser: http://localhost:7474 (user `neo4j`, password from `.env` /
`.env.example`, default `changeme123`).

The domain model now exists: 6 node types, 6 relationship types (all with
properties), Spring Data Neo4j repositories, and a startup schema
initializer that creates uniqueness constraints and indexes. See
[docs/DATA_MODEL.md](docs/DATA_MODEL.md) for the full model and
[docs/NEO4J_VS_POSTGRES.md](docs/NEO4J_VS_POSTGRES.md) for why this is a
graph database problem, not a relational one.

## REST API (Phase 3)

CRUD-style entity endpoints and a generic relationship endpoint now exist:

- `POST /api/{users|companies|teams|skills|projects|technologies}` — create
- `GET /api/{resource}/{id}` — fetch by id (404 `ENTITY_NOT_FOUND` if missing)
- `GET /api/{resource}/search?q=&page=&size=` — paginated name search
- `POST /api/relationships` — create/update a typed, property-bearing
  relationship (`{"type": "USER_HAS_SKILL", "sourceId": "...", "targetId":
  "...", "properties": {"proficiencyLevel": "EXPERT", "yearsOfExperience": 8}}`)
- `DELETE /api/relationships/{elementId}` — delete a relationship

Every error response uses the same shape:
```json
{"timestamp": "...", "status": 404, "error": "ENTITY_NOT_FOUND", "message": "...", "path": "..."}
```

Run the backend and browse the live API docs:

```bash
docker compose up -d neo4j
cd backend && mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html · OpenAPI JSON: http://localhost:8080/v3/api-docs

## Tests

```bash
cd backend && mvn test                       # fast tests, no Docker needed
cd backend && mvn test -Dtest=GraphDomainModelIT   # Neo4j integration test, needs Docker
```

`UserControllerTest` exercises real HTTP dispatch (validation +
`GlobalExceptionHandler`) with the service layer mocked — no database
needed. `GraphDomainModelIT` needs a local Docker daemon (Testcontainers
starts a real Neo4j).
