package com.swetha.graphexplorer.domain.enums;

/**
 * The relationship types that exist in the graph. The enum name is exactly
 * the Neo4j relationship type string (Cypher convention: SCREAMING_SNAKE_CASE),
 * so {@code RelationshipType.USER_HAS_SKILL.name()} can be interpolated
 * directly into Cypher without a client ever supplying a raw relationship
 * type string — Jackson only deserializes a request into one of these enum
 * constants in the first place, so there is no injection surface.
 * <p>
 * Each type also carries the node labels it connects, so relationship
 * creation can validate/build Cypher patterns generically instead of a
 * hand-written branch per type at the persistence layer.
 */
public enum RelationshipType {
    USER_WORKS_AT_COMPANY(EntityType.USER, EntityType.COMPANY),
    USER_MEMBER_OF_TEAM(EntityType.USER, EntityType.TEAM),
    USER_HAS_SKILL(EntityType.USER, EntityType.SKILL),
    USER_WORKED_ON_PROJECT(EntityType.USER, EntityType.PROJECT),
    PROJECT_USES_TECHNOLOGY(EntityType.PROJECT, EntityType.TECHNOLOGY),
    USER_COLLABORATED_WITH_USER(EntityType.USER, EntityType.USER);

    private final EntityType sourceType;
    private final EntityType targetType;

    RelationshipType(EntityType sourceType, EntityType targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public EntityType sourceType() {
        return sourceType;
    }

    public EntityType targetType() {
        return targetType;
    }
}
