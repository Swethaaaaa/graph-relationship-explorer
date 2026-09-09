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
- [ ] Phase 3 — REST APIs (entities + relationships)
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
graph database problem, not a relational one. There are no REST endpoints
yet — that's Phase 3.

An integration test (`GraphDomainModelIT`) exercises the model against a
real Neo4j via Testcontainers, including the self-referential
`USER_COLLABORATED_WITH_USER` edge. It requires a local Docker daemon to run:

```bash
cd backend && mvn test -Dtest=GraphDomainModelIT
```
