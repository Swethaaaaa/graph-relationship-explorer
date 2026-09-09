package com.swetha.graphexplorer.graph;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.enums.RelationshipType;
import com.swetha.graphexplorer.dto.response.ConnectionResponse;
import com.swetha.graphexplorer.dto.response.GraphEdgeDto;
import com.swetha.graphexplorer.dto.response.GraphNodeDto;
import com.swetha.graphexplorer.dto.response.ShortestPathResponse;
import com.swetha.graphexplorer.exception.InvalidGraphQueryException;
import com.swetha.graphexplorer.exception.NoPathFoundException;
import com.swetha.graphexplorer.service.EntityExistenceChecker;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

/**
 * "Relationship discovery" queries scoped to a User: direct connections,
 * connections shared with another user, and the shortest path to another
 * user. All three are single Cypher statements — in particular, shortest
 * path uses Neo4j's built-in {@code shortestPath()} function rather than a
 * hand-rolled BFS in Java, which is both simpler and lets the database use
 * its own optimized traversal instead of pulling the whole neighborhood
 * into the JVM first.
 */
@Service
@RequiredArgsConstructor
public class UserDiscoveryService {

    private final Neo4jClient neo4jClient;
    private final EntityExistenceChecker entityExistenceChecker;

    @Value("${app.graph.max-shortest-path-hops:6}")
    private int maxShortestPathHops;

    public List<ConnectionResponse> connections(String userId) {
        entityExistenceChecker.requireExists(EntityType.USER, userId);

        String cypher = """
                MATCH (u:User {id: $id})-[r]-(other)
                RETURN type(r) AS relType,
                       CASE WHEN startNode(r) = u THEN 'OUTGOING' ELSE 'INCOMING' END AS direction,
                       properties(r) AS relProps,
                       other.id AS otherId, labels(other)[0] AS otherLabel, properties(other) AS otherProps
                """;

        return neo4jClient.query(cypher)
                .bindAll(Map.of("id", userId))
                .fetch()
                .all()
                .stream()
                .map(row -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> relProps = (Map<String, Object>) row.get("relProps");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> otherProps = (Map<String, Object>) row.get("otherProps");
                    GraphNodeDto other = GraphMappers.toNodeDto(
                            (String) row.get("otherId"), (String) row.get("otherLabel"), otherProps);
                    return new ConnectionResponse(
                            RelationshipType.valueOf((String) row.get("relType")),
                            ConnectionResponse.Direction.valueOf((String) row.get("direction")),
                            relProps,
                            other);
                })
                .toList();
    }

    public List<GraphNodeDto> commonConnections(String userId, String otherUserId) {
        requireDistinctUsers(userId, otherUserId);
        entityExistenceChecker.requireExists(EntityType.USER, userId);
        entityExistenceChecker.requireExists(EntityType.USER, otherUserId);

        String cypher = """
                MATCH (a:User {id: $id1})-[]-(common)-[]-(b:User {id: $id2})
                WHERE common.id <> $id1 AND common.id <> $id2
                RETURN DISTINCT common.id AS id, labels(common)[0] AS label, properties(common) AS properties
                """;

        return neo4jClient.query(cypher)
                .bindAll(Map.of("id1", userId, "id2", otherUserId))
                .fetch()
                .all()
                .stream()
                .map(GraphMappers::toNodeDto)
                .toList();
    }

    public ShortestPathResponse shortestPath(String userId, String otherUserId) {
        requireDistinctUsers(userId, otherUserId);
        entityExistenceChecker.requireExists(EntityType.USER, userId);
        entityExistenceChecker.requireExists(EntityType.USER, otherUserId);

        String cypher = """
                MATCH (a:User {id: $id1}), (b:User {id: $id2})
                MATCH path = shortestPath((a)-[*..%d]-(b))
                RETURN [n IN nodes(path) | {id: n.id, label: labels(n)[0], properties: properties(n)}] AS nodes,
                       [r IN relationships(path) | {id: elementId(r), type: type(r), sourceId: startNode(r).id, targetId: endNode(r).id, properties: properties(r)}] AS edges,
                       length(path) AS length
                """.formatted(maxShortestPathHops);

        Map<String, Object> row = neo4jClient.query(cypher)
                .bindAll(Map.of("id1", userId, "id2", otherUserId))
                .fetch()
                .one()
                .orElseThrow(() -> new NoPathFoundException(
                        "No path found between users '%s' and '%s' within %d hops"
                                .formatted(userId, otherUserId, maxShortestPathHops)));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodeRows = (List<Map<String, Object>>) row.get("nodes");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> edgeRows = (List<Map<String, Object>>) row.get("edges");

        List<GraphNodeDto> nodes = nodeRows.stream().map(GraphMappers::toNodeDto).toList();
        List<GraphEdgeDto> edges = edgeRows.stream().map(GraphMappers::toEdgeDto).toList();
        int length = ((Number) row.get("length")).intValue();

        return new ShortestPathResponse(length, nodes, edges);
    }

    private void requireDistinctUsers(String userId, String otherUserId) {
        if (userId.equals(otherUserId)) {
            throw new InvalidGraphQueryException("userId and otherUserId must be different users");
        }
    }
}
