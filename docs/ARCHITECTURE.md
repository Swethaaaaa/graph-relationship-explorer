# Architecture

## 1. What this system is

Graph-Based Relationship Explorer is a full-stack application for modeling and
exploring relationships between entities in a professional network domain
(Users, Companies, Teams, Projects, Skills, Technologies). The core value
proposition is **traversal**: "show me how X connects to Y" and "what's the
shortest path between two people." That is a graph problem, not a rows-and-joins
problem, which is why Neo4j is the primary datastore (see `NEO4J_VS_POSTGRES.md`).

## 2. High-level components

```
┌─────────────────────┐        REST + WebSocket        ┌──────────────────────┐
│   React + TS SPA     │ <-----------------------------> │  Spring Boot backend  │
│   (graph viz, search) │                                 │  (single deployable)  │
└─────────────────────┘                                  └───────────┬───────────┘
                                                                       │ Bolt protocol
                                                                       │ (Cypher)
                                                             ┌─────────▼─────────┐
                                                             │       Neo4j         │
                                                             │  (graph database)   │
                                                             └─────────────────────┘
```

## 3. Why a single Spring Boot service, not "real" microservices

The prompt for this project mentions "microservice-style backend architecture."
Deploying actual separate microservices (separate deployables, service
discovery, inter-service network calls, distributed transactions) for a domain
this small would be **over-engineering** — it adds operational complexity
(multiple JVMs, a service registry or gateway, network failure modes) without
a corresponding real requirement (no independent scaling need, no separate
team ownership, no polyglot requirement).

Instead, this backend is a **single deployable Spring Boot application with a
microservice-style internal architecture**: strict layering, package-by-feature
boundaries, and no leakage of persistence types across layers, so that any
module (e.g. graph traversal, or relationship management) could be extracted
into its own service later with minimal rework. This is the same reasoning a
senior engineer applies in a real job: start with a modular monolith, extract
services when you have an actual scaling or ownership reason to.

**Interview answer:** "I built it as a single well-layered service because the
domain didn't justify the operational overhead of real microservices, but I
designed the package boundaries (controller/service/repository/graph) so each
concern is independently testable and could be split out later if, say, graph
traversal needed to scale independently of entity CRUD."

## 4. Backend layering

```
com.swetha.graphexplorer
├── controller/     REST endpoints. Thin — validates input via DTOs, delegates to service.
├── service/        Business logic, transaction boundaries.
├── graph/          Traversal-specific logic (expand, shortest path, common connections) using Cypher.
├── repository/     Spring Data Neo4j repositories + custom @Query Cypher.
├── domain/
│   ├── node/           @Node entities (User, Company, Team, Skill, Project, Technology)
│   └── relationship/   @RelationshipProperties entities (typed, property-bearing edges)
├── dto/
│   ├── request/     Validated inbound payloads
│   └── response/    Outbound payloads — entities never cross the controller boundary
├── mapper/          Entity <-> DTO conversion
├── exception/       Domain exceptions + centralized @ControllerAdvice handler
├── websocket/        STOMP config + event broadcasting
├── config/           CORS, caching, OpenAPI, Neo4j schema bootstrap
└── seed/              Demo data generation on startup
```

Rules enforced by this layering:
- Controllers never touch `domain` or `repository` types directly — only DTOs.
- Business logic lives in `service`/`graph`, never in controllers or mappers.
- `repository` is the only layer allowed to hold Cypher/Spring Data Neo4j imports.

## 5. Request flow example

`GET /api/graph/user/{id}?depth=2`

1. `GraphController` validates path/query params, calls `GraphTraversalService`.
2. `GraphTraversalService` calls a `graph` package service that runs a
   variable-length Cypher traversal (`MATCH (n)-[*1..2]-(m)`) against Neo4j.
3. Neo4j executes the traversal server-side and returns only the matched
   subgraph (nodes + relationships) — the application never loads the whole
   graph into memory.
4. Results are mapped to a `GraphResponse` DTO (nodes/edges/metadata) and
   returned as JSON.

## 6. Frontend

React + TypeScript SPA (Vite). Talks to the backend over REST for
search/CRUD/traversal, and over a WebSocket (STOMP over SockJS) for live
updates. Graph rendering uses an existing library (not hand-rolled) — decided
in Phase 6.

## 7. Cross-cutting concerns

| Concern | Approach |
|---|---|
| Validation | Bean Validation (`jakarta.validation`) on request DTOs |
| Error handling | Centralized `@ControllerAdvice`, consistent JSON error shape |
| Pagination | Spring Data `Pageable` on search endpoints |
| Caching | Caffeine, applied to read-heavy/expensive traversal queries (Phase 4) |
| API docs | springdoc-openapi / Swagger UI |
| Real-time | Spring WebSocket (STOMP) broadcasting entity/relationship change events |
| Testing | JUnit 5 + Mockito for units, Testcontainers(Neo4j) for integration |

## 8. What's deliberately NOT included

- No Spring Security / auth layer implemented (explained as a "how would you
  secure this" interview answer instead — see README) — adding it would be a
  large, separate concern (JWT/OAuth, user management) that isn't the point of
  this portfolio project.
- No distributed tracing / service mesh — not applicable to a single service.
- No manual graph algorithms in Java — traversal, shortest path, and common
  connections are pushed down to Cypher/Neo4j, which is the whole point of
  using a graph database.
