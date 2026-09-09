# Graph Data Model

## Nodes

| Label | Key properties | Notes |
|---|---|---|
| `User` | `id` (UUID, PK), `email` (unique), `fullName`, `title`, `bio`, `location`, `createdAt` | |
| `Company` | `id` (UUID, PK), `name` (unique), `industry`, `website`, `size`, `foundedYear` | `size` ∈ `STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE` |
| `Team` | `id` (UUID, PK), `name`, `description`, `department` | |
| `Skill` | `id` (UUID, PK), `name` (unique), `category` | e.g. "Programming Language", "Cloud" |
| `Project` | `id` (UUID, PK), `name`, `description`, `status`, `startDate`, `endDate` | `status` ∈ `PLANNED, ACTIVE, ON_HOLD, COMPLETED` |
| `Technology` | `id` (UUID, PK), `name` (unique), `category` | e.g. "Language", "Framework", "Database" |

All `id` values are application-generated UUID strings (not Neo4j's internal
element id), so they're stable identifiers safe to expose in REST URLs —
internal ids can be reused/renumbered by the database and should never leak
into an API contract.

## Relationships

| Type | Direction | Properties | Meaning |
|---|---|---|---|
| `USER_WORKS_AT_COMPANY` | `User -> Company` | `jobTitle`, `startDate`, `current` | Employment. A user can have multiple (job history); `current` marks the active one. |
| `USER_MEMBER_OF_TEAM` | `User -> Team` | `role`, `joinedDate` | Team membership. |
| `USER_HAS_SKILL` | `User -> Skill` | `proficiencyLevel`, `yearsOfExperience` | `proficiencyLevel` ∈ `BEGINNER, INTERMEDIATE, ADVANCED, EXPERT`. |
| `USER_WORKED_ON_PROJECT` | `User -> Project` | `role`, `startDate`, `endDate` | Project contribution. |
| `PROJECT_USES_TECHNOLOGY` | `Project -> Technology` | `usageContext` | e.g. "primary backend language". |
| `USER_COLLABORATED_WITH_USER` | `User -> User` | `context`, `collaborationCount` | Self-referential. Stored as one directed edge per pair; queries that need an undirected "did A and B ever work together" check match the relationship in either direction (`(a)-[:USER_COLLABORATED_WITH_USER]-(b)`, no arrow). |

Every relationship carries properties, which is exactly the kind of thing a
graph database makes cheap and a join table in a relational database makes
awkward — see `NEO4J_VS_POSTGRES.md`.

## Why Company/Team/Skill/Technology don't hold back-references

`Company`, `Team`, `Skill`, and `Technology` are modeled as plain nodes with
no `@Relationship` fields of their own. All edges are owned by the side that
is conceptually "the actor" (`User` for people-relationships, `Project` for
`USES_TECHNOLOGY`). This keeps the object graph acyclic in the Java mapping
layer (no accidental deep/circular fetches when Spring Data Neo4j loads a
`Company`), while the underlying Neo4j relationships are still fully
traversable in either direction via Cypher — object-mapping direction and
graph-traversal direction are independent concerns. Queries like "which users
work at this company" are implemented as repository/Cypher queries, not as a
mapped Java collection on `Company`.

## Example: how the required traversal paths actually connect

The spec's example paths (`User → Company → Team → Project → Technology`,
`User → Skill → Other Users`) don't require a direct `Company -> Team` edge.
`User` is the hub: `Company`, `Team`, `Skill`, and `Project` all connect
through a shared `User` node, and `Project` connects onward to `Technology`.
A depth-limited Cypher traversal centered on a `User` naturally reaches all
of these without inventing relationship types beyond the six specified.

## Constraints

Applied at startup by `GraphSchemaInitializer` (idempotent, `IF NOT EXISTS`):

- Uniqueness (and therefore an index) on `id` for every node label — enforces
  that generated UUIDs never collide and gives O(log n) lookup by id.
- Uniqueness on `User.email`, `Company.name`, `Skill.name`, `Technology.name`
  — these are the natural business keys used by search/seed/upsert logic.

`Team.name` and `Project.name` are **not** unique constraints: two different
companies can reasonably have a team or project with the same name, and
`Team` has no direct `Company` edge in this model to scope the uniqueness to.
This is a known simplification — see "Future improvements" in the top-level
README.

## Indexes

- Non-unique indexes on `User.fullName`, `Team.name`, `Project.name` for
  search-by-name performance (backing the search endpoints in Phase 3).
- Every uniqueness constraint above is itself backed by an index, so
  point-lookups by email/name are indexed lookups, not label scans.
