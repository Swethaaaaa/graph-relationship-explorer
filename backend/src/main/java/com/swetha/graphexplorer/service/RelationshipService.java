package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.enums.RelationshipType;
import com.swetha.graphexplorer.dto.request.CreateRelationshipRequest;
import com.swetha.graphexplorer.dto.response.RelationshipResponse;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.exception.InvalidRelationshipException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates and deletes relationships directly via Cypher/{@link Neo4jClient}
 * rather than through the Spring Data Neo4j aggregate-root save path used by
 * {@code domain.node} entities. Relationships here are managed independently
 * of any one "owning" node, and node/relationship types are decided entirely
 * by the client-supplied {@link RelationshipType} enum, so hand-written
 * Cypher is simpler and cheaper than loading a whole aggregate just to add
 * one edge to a collection.
 */
@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final Neo4jClient neo4jClient;

    @Transactional
    public RelationshipResponse createRelationship(CreateRelationshipRequest request) {
        RelationshipType type = request.type();
        String sourceId = request.sourceId();
        String targetId = request.targetId();

        if (type.sourceType() == type.targetType() && sourceId.equals(targetId)) {
            throw new InvalidRelationshipException(
                    "A %s cannot have a %s relationship with itself".formatted(
                            type.sourceType().label(), type.name()));
        }

        requireExists(type.sourceType(), sourceId);
        requireExists(type.targetType(), targetId);

        Map<String, Object> properties = RelationshipPropertyValidator.normalize(type, request.propertiesOrEmpty());

        String cypher = """
                MATCH (source:%s {id: $sourceId})
                MATCH (target:%s {id: $targetId})
                MERGE (source)-[r:%s]->(target)
                SET r += $props
                RETURN elementId(r) AS id, properties(r) AS properties
                """.formatted(type.sourceType().label(), type.targetType().label(), type.name());

        Map<String, Object> row = neo4jClient.query(cypher)
                .bindAll(Map.of("sourceId", sourceId, "targetId", targetId, "props", properties))
                .fetch()
                .one()
                .orElseThrow(() -> new IllegalStateException("MERGE did not return the created relationship"));

        @SuppressWarnings("unchecked")
        Map<String, Object> returnedProperties = (Map<String, Object>) row.get("properties");

        return new RelationshipResponse(
                (String) row.get("id"), type, sourceId, targetId, returnedProperties);
    }

    @Transactional
    public void deleteRelationship(String relationshipId) {
        var summary = neo4jClient.query("MATCH ()-[r]->() WHERE elementId(r) = $id DELETE r")
                .bindAll(Map.of("id", relationshipId))
                .run();

        if (summary.counters().relationshipsDeleted() == 0) {
            throw new EntityNotFoundException("Relationship with id '%s' was not found".formatted(relationshipId));
        }
    }

    private void requireExists(EntityType entityType, String id) {
        boolean exists = neo4jClient.query(
                        "MATCH (n:%s {id: $id}) RETURN count(n) > 0 AS exists".formatted(entityType.label()))
                .bindAll(Map.of("id", id))
                .fetchAs(Boolean.class)
                .one()
                .orElse(false);
        if (!exists) {
            throw new EntityNotFoundException(entityType, id);
        }
    }
}
