package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

/**
 * Small shared helper for the one check both relationship management and
 * graph traversal need before doing anything else: does this id actually
 * exist as this label. A single indexed lookup (backed by the uniqueness
 * constraint on {@code id}), not a label scan.
 */
@Component
@RequiredArgsConstructor
public class EntityExistenceChecker {

    private final Neo4jClient neo4jClient;

    public boolean exists(EntityType entityType, String id) {
        return neo4jClient.query(
                        "MATCH (n:%s {id: $id}) RETURN count(n) > 0 AS exists".formatted(entityType.label()))
                .bindAll(Map.of("id", id))
                .fetchAs(Boolean.class)
                .one()
                .orElse(false);
    }

    public void requireExists(EntityType entityType, String id) {
        if (!exists(entityType, id)) {
            throw new EntityNotFoundException(entityType, id);
        }
    }
}
