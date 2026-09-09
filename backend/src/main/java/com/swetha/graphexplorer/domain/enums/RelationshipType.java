package com.swetha.graphexplorer.domain.enums;

/**
 * The relationship types that exist in the graph. The enum name is exactly
 * the Neo4j relationship type string (Cypher convention: SCREAMING_SNAKE_CASE),
 * so {@code RelationshipType.USER_HAS_SKILL.name()} can be interpolated
 * directly into Cypher without a client ever supplying a raw relationship
 * type string.
 */
public enum RelationshipType {
    USER_WORKS_AT_COMPANY,
    USER_MEMBER_OF_TEAM,
    USER_HAS_SKILL,
    USER_WORKED_ON_PROJECT,
    PROJECT_USES_TECHNOLOGY,
    USER_COLLABORATED_WITH_USER
}
