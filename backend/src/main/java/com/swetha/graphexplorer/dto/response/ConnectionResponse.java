package com.swetha.graphexplorer.dto.response;

import com.swetha.graphexplorer.domain.enums.RelationshipType;
import java.util.Map;

/**
 * One direct (1-hop) relationship a user has, from the user's point of view.
 */
public record ConnectionResponse(
        RelationshipType relationshipType,
        Direction direction,
        Map<String, Object> relationshipProperties,
        GraphNodeDto connectedEntity) {

    public enum Direction {
        OUTGOING,
        INCOMING
    }
}
