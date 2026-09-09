package com.swetha.graphexplorer.dto.response;

import com.swetha.graphexplorer.domain.enums.RelationshipType;
import java.util.Map;

public record RelationshipResponse(
        String id,
        RelationshipType type,
        String sourceId,
        String targetId,
        Map<String, Object> properties) {
}
