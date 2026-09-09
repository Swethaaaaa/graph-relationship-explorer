package com.swetha.graphexplorer.graph;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.enums.RelationshipType;
import com.swetha.graphexplorer.dto.response.GraphEdgeDto;
import com.swetha.graphexplorer.dto.response.GraphNodeDto;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts raw Cypher result rows (plain {@code Map<String, Object>}, as
 * returned by {@code Neo4jClient.query(...).fetch()}) into API DTOs. Shared
 * by every service in this package so the "how do we turn a Neo4j record
 * into a GraphNodeDto" logic exists in exactly one place.
 */
final class GraphMappers {

    private GraphMappers() {
    }

    static GraphNodeDto toNodeDto(Map<String, Object> row) {
        @SuppressWarnings("unchecked")
        Map<String, Object> rawProperties = (Map<String, Object>) row.get("properties");
        return toNodeDto((String) row.get("id"), (String) row.get("label"), rawProperties);
    }

    static GraphNodeDto toNodeDto(String id, String label, Map<String, Object> rawProperties) {
        Map<String, Object> properties = new LinkedHashMap<>(rawProperties);
        properties.remove("id");

        String name = displayName(label, properties);

        return new GraphNodeDto(id, EntityType.fromLabel(label), name, properties);
    }

    static GraphEdgeDto toEdgeDto(Map<String, Object> row) {
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) row.get("properties");
        return new GraphEdgeDto(
                (String) row.get("id"),
                RelationshipType.valueOf((String) row.get("type")),
                (String) row.get("sourceId"),
                (String) row.get("targetId"),
                properties);
    }

    private static String displayName(String label, Map<String, Object> properties) {
        Object name = "User".equals(label) ? properties.get("fullName") : properties.get("name");
        return name == null ? "" : name.toString();
    }
}
