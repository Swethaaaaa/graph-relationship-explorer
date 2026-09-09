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
- [x] Phase 4 — Graph traversal (Cypher-based)
- [x] Phase 5 — React frontend
- [x] Phase 6 — Graph visualization
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

## Graph traversal & relationship discovery (Phase 4)

- `GET /api/graph/{entityType}/{id}?depth=1..4` — the induced subgraph
  reachable from an entity within `depth` hops (nodes + every relationship
  between two nodes in that set). `entityType` is one of `user`, `company`,
  `team`, `skill`, `project`, `technology`. Depth is capped by
  `app.graph.max-traversal-depth` (default 4, env `GRAPH_MAX_DEPTH`) — a
  request beyond that is a 400 `INVALID_GRAPH_QUERY`, not an expensive query.
- `GET /api/users/{id}/connections` — a user's direct relationships, each
  with its type, direction, and properties.
- `GET /api/users/{id}/common-connections/{otherUserId}` — entities both
  users are connected to.
- `GET /api/users/{id}/shortest-path/{otherUserId}` — shortest path via
  Neo4j's `shortestPath()`, bounded by `app.graph.max-shortest-path-hops`
  (default 6). 404 `NO_PATH_FOUND` if the users aren't connected within that
  bound.

All of these are plain Cypher against `Neo4jClient` — no traversal or
shortest-path algorithm is implemented in Java. See
[docs/NEO4J_VS_POSTGRES.md](docs/NEO4J_VS_POSTGRES.md) for why.

## Tests

```bash
cd backend && mvn test                              # fast tests, no Docker needed
cd backend && mvn test -Dtest=GraphDomainModelIT     # Neo4j integration test (Phase 2 model), needs Docker
cd backend && mvn test -Dtest=GraphTraversalIT       # Neo4j integration test (Phase 4 traversal), needs Docker
```

`UserControllerTest` exercises real HTTP dispatch (validation +
`GlobalExceptionHandler`) with the service layer mocked — no database
needed. The two `*IT` tests need a local Docker daemon (Testcontainers
starts a real Neo4j); `mvn test` skips them automatically (Surefire's
default naming convention only picks up `*Test`), so the fast suite never
needs Docker.

## Frontend (Phase 5)

React 19 + TypeScript, scaffolded with Vite. Talks to the backend over
plain `fetch` (no axios — the request surface is small enough that a
library would add more weight than it saves).

- **Search bar** — pick an entity type, type a name, results update
  debounced (300ms) via `GET /api/{resource}/search`.
- **Entity details panel** — shows the selected entity's type, name, every
  other property, and its direct relationships (type, direction, the
  connected entity, clickable to pivot the selection). This reuses
  `GET /api/graph/{entityType}/{id}?depth=1` — the same endpoint the
  Phase 6 graph visualization will call — rather than a separate
  "get entity" + "get relationships" pair, and works identically for all
  six entity types (unlike `/api/users/{id}/connections`, which is
  User-only).
- **Graph panel** — a real force-directed graph (Phase 6, below) rendering
  the same `/api/graph` response.

Run it (needs the backend running too, for real data):

```bash
cd frontend && npm install && npm run dev
```

Vite proxies `/api` to `http://localhost:8080` in dev
([vite.config.ts](frontend/vite.config.ts)), so no CORS setup is needed
locally; `VITE_API_BASE_URL` (see `.env.example`) overrides that for a
production build.

```bash
cd frontend && npm run build   # tsc -b && vite build — verified clean
cd frontend && npm run lint    # oxlint — 3 non-blocking warnings, see below
```

I ran the dev server against the app with the backend unreachable (no
Docker/Neo4j in this environment) — the UI rendered correctly and surfaced
the request failure through the same error-handling path a real backend
error would take, which is what that check was for. I have not yet verified
the full flow against real data; do that once the backend is running:

```bash
docker compose up -d neo4j && (cd backend && mvn spring-boot:run) &
cd frontend && npm run dev
```

`oxlint` flags warnings (`set-state-in-effect`, `exhaustive-deps`) on each
of the three data-fetching effects (`SearchBar`, `useEntityDetails`,
`GraphView`). All are the standard "fetch in a `useEffect`, `setState` in
the callback" pattern — correct here, just newer lint rules nudging toward
a data-fetching library (e.g. TanStack Query) or `use()`. That would be a
reasonable follow-up, but is more machinery than this app's read-only query
set justifies today.

## Graph visualization (Phase 6)

[GraphView](frontend/src/components/GraphView.tsx) renders `/api/graph/{entityType}/{id}?depth=N`
using [`react-force-graph-2d`](https://github.com/vasturiano/react-force-graph)
(a canvas-based force layout on top of d3-force) — not a hand-rolled
rendering engine, per the project's own requirement.

- **Nodes, edges, relationship labels** — nodes are colored by entity type
  and labeled with their name; edges are drawn with directional arrowheads
  and their relationship type as a label at the midpoint (also shown on
  hover as a tooltip).
- **Zoom / pan / drag** — the library's default mouse-wheel zoom,
  click-drag pan, and node-drag interactions; nothing custom was needed.
- **Selecting nodes / expanding connections** — clicking a node calls the
  same `onSelect` callback the search results and the details panel's
  relationship links use, which re-centers the graph (and the details
  panel) on that node — "expanding connections" is implemented as
  re-centering the traversal on whatever you click, so all three panels
  (search, details, graph) always agree on the current selection.
- **Changing traversal depth** — a depth selector (1–4, matching the
  backend's default `app.graph.max-traversal-depth`) in the graph panel's
  toolbar, independent of the details panel's fixed depth-1 view.

**Verified with real interaction, not just a build check:** no Neo4j is
available in this sandbox, so I mocked `fetch` in a live browser session
(intercepting only `/api/users/search` and `/api/graph/user/*`, passing
everything else through) and drove the actual app: searched for "ali",
selected "Alice Smith", confirmed the details panel showed her properties
and 3 relationships, confirmed the graph rendered all 4 nodes correctly
colored/labeled with edge labels, then clicked "Acme Corp" in the
relationship list and confirmed both the details panel and the graph
correctly re-fetched for the new selection (and displayed the same
real error-handling path, `Request failed with status 502`, since Acme
Corp wasn't in the mock — proving the pivot logic runs the real code
path, not a scripted demo). Still unverified: the same flow against a real
backend/Neo4j, and direct click-to-select on a graph *node* specifically
(I verified pivoting via the details-panel link, which shares the same
`onSelect` handler as node clicks) — worth doing once Docker is available.
