# Why Neo4j instead of PostgreSQL

This is one of the questions this project is meant to give you a real,
defensible answer to. Short version: **the core feature of this app is
variable-depth relationship traversal and shortest-path finding, and that is
precisely the operation relational databases are structurally bad at.**

## What the core queries actually need

- "Show me everything connected to this user within 2 hops, across 6
  different relationship types."
- "What's the shortest path between these two users?"
- "What do these two users have in common?"

## How this would look in PostgreSQL

Relationships would live in join tables (`user_works_at_company`,
`user_has_skill`, `user_collaborated_with_user`, ...). A 2-hop traversal
across multiple relationship types means:

- A `UNION` or multiple `JOIN`s per hop, because each hop can go through a
  different join table depending on relationship type.
- The number of joins grows with traversal depth: depth 3 means potentially
  3+ chained joins per path, and the query plan has to be rewritten for every
  depth you want to support, or written generically with a recursive CTE.
- Recursive CTEs (`WITH RECURSIVE`) can express variable-depth traversal, but
  they still re-evaluate index lookups at every level and don't have a
  native concept of "shortest path" — you'd be hand-rolling BFS/Dijkstra in
  SQL, which is exactly the kind of thing the project spec says not to do in
  application code either.
- Every additional relationship type is a new join table and a more complex
  query, not a new line in a pattern.

## How this looks in Neo4j / Cypher

```cypher
MATCH (start:User {id: $id})-[*1..2]-(connected)
RETURN start, connected
```

This is one pattern, not N joins. Neo4j stores relationships as first-class,
directly-traversable pointers between nodes (an "index-free adjacency"
structure) — walking from one node to its neighbors is a constant-time
pointer follow, not an index lookup into a join table, regardless of how many
relationship types exist in the graph or how large the total graph is.
Traversal cost scales with the size of the *matched subgraph*, not the size
of the *whole dataset* — which is exactly the "don't load everything into
memory" requirement this project has.

Shortest path is a first-class primitive:

```cypher
MATCH p = shortestPath((a:User {id: $id1})-[*..6]-(b:User {id: $id2}))
RETURN p
```

## Where PostgreSQL would actually be a fine (or better) choice

To be fair to relational databases: if this app were mostly "list users,
filter by a couple of columns, occasionally join one level to their company,"
PostgreSQL would be simpler to operate, has better tooling, and would be the
right default choice. Graph databases win specifically when the *traversal
depth is unknown or multi-hop* and *relationship types are heterogeneous* —
which is exactly this app's core feature, not an incidental one. Using Neo4j
for a CRUD app with no real traversal need would itself be the wrong
trade-off — the point isn't "graph DB is always better," it's "pick the
storage model that matches the access pattern."

## What Cypher actually is, for anyone unfamiliar

Cypher is Neo4j's declarative query language, built around drawing the
pattern you want to match using ASCII-art-like syntax:
`(node)-[:RELATIONSHIP_TYPE]->(otherNode)`. You describe the *shape* of the
data you want (a node connected to another node by a relationship type,
possibly repeated `*1..3` times for variable depth), and the query planner
figures out how to walk the graph to find matches — the same declarative
mental model as SQL, but the "join" is expressed as an edge in a pattern
instead of a `JOIN ... ON` clause matching foreign keys.

## How indexes/constraints help in this model

Constraints (e.g. uniqueness on `User.email`) both enforce data integrity and
create an index, so the *entry point* into a traversal — "find the User
node(s) to start from" — is an indexed lookup, not a label scan. Once you're
at a starting node, traversal to neighbors doesn't need an index at all
(index-free adjacency); indexes only matter for the initial lookup and for
any additional filtering inside a `WHERE` clause during traversal. See
`DATA_MODEL.md` for the exact constraints/indexes applied.
