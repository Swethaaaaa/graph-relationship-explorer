package com.swetha.graphexplorer.domain.enums;

/**
 * The node/entity types exposed by the graph API. Used to route
 * {@code /api/graph/{entityType}/{id}} requests to the right Neo4j label
 * without relying on client-supplied, unvalidated label strings.
 */
public enum EntityType {
    USER("User"),
    COMPANY("Company"),
    TEAM("Team"),
    SKILL("Skill"),
    PROJECT("Project"),
    TECHNOLOGY("Technology");

    private final String label;

    EntityType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
