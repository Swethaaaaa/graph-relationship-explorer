package com.swetha.graphexplorer.graph;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.enums.RelationshipType;
import com.swetha.graphexplorer.dto.response.GraphEdgeDto;
import com.swetha.graphexplorer.dto.response.GraphNodeDto;
import com.swetha.graphexplorer.dto.response.GraphResponse;
import com.swetha.graphexplorer.exception.InvalidGraphQueryException;
import com.swetha.graphexplorer.service.EntityExistenceChecker;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

/**
 * Generic entity-centered graph traversal: "everything within N hops of this
 * node." Implemented as two plain Cypher queries against Neo4j rather than
 * loading the graph into the JVM and walking it in Java:
 * <ol>
 *   <li>find every node reachable from the start node within {@code depth}
 *       hops (Neo4j's index-free adjacency makes this a pointer walk, not a
 *       series of index lookups);</li>
 *   <li>find every relationship whose <em>both</em> endpoints are in that
 *       node set — the induced subgraph, which is what a graph
 *       visualization actually wants (it also shows edges between two
 *       "sibling" nodes found at the same depth, not just star edges back
 *       to the start node).</li>
 * </ol>
 * Traversal depth is capped by {@code app.graph.max-traversal-depth}
 * (default 4) — an unbounded {@code MATCH (start)-[*]-(other)} on a large
 * graph is exactly the kind of query that can take down a database, so the
 * API refuses depths beyond a configured ceiling rather than trusting the
 * client.
 */
@Service
@RequiredArgsConstructor
public class GraphTraversalService {

    private final Neo4jClient neo4jClient;
    private final EntityExistenceChecker entityExistenceChecker;

    @Value("${app.graph.max-traversal-depth}")
    private int maxTraversalDepth;

    public GraphResponse expand(EntityType entityType, String id, int depth) {
        if (depth < 1 || depth > maxTraversalDepth) {
            throw new InvalidGraphQueryException(
                    "depth must be between 1 and %d, got %d".formatted(maxTraversalDepth, depth));
        }
        entityExistenceChecker.requireExists(entityType, id);

        List<GraphNodeDto> nodes = fetchReachableNodes(entityType, id, depth);
        Set<String> nodeIds = nodes.stream().map(GraphNodeDto::id).collect(Collectors.toSet());
        List<GraphEdgeDto> edges = fetchInducedEdges(nodeIds);

        return new GraphResponse(nodes, edges, depth);
    }

    private List<GraphNodeDto> fetchReachableNodes(EntityType entityType, String id, int depth) {
        String cypher = """
                MATCH (start:%s {id: $id})
                MATCH (start)-[*0..%d]-(reachable)
                RETURN DISTINCT reachable.id AS id, labels(reachable)[0] AS label, properties(reachable) AS properties
                """.formatted(entityType.label(), depth);

        return neo4jClient.query(cypher)
                .bindAll(Map.of("id", id))
                .fetch()
                .all()
                .stream()
                .map(GraphMappers::toNodeDto)
                .toList();
    }

    private List<GraphEdgeDto> fetchInducedEdges(Set<String> nodeIds) {
        if (nodeIds.isEmpty()) {
            return List.of();
        }
        String cypher = """
                MATCH (n1)-[r]->(n2)
                WHERE n1.id IN $nodeIds AND n2.id IN $nodeIds
                RETURN DISTINCT elementId(r) AS id, type(r) AS type, n1.id AS sourceId, n2.id AS targetId, properties(r) AS properties
                """;

        return neo4jClient.query(cypher)
                .bindAll(Map.of("nodeIds", nodeIds))
                .fetch()
                .all()
                .stream()
                .map(GraphMappers::toEdgeDto)
                .toList();
    }
}
