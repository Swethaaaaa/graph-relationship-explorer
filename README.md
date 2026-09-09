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
- [ ] Phase 2 — Neo4j graph model and backend domain layer
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

The backend is scaffolded (`backend/pom.xml`, package structure, base
configuration) but has no domain model or endpoints yet — that's Phase 2.
