package com.swetha.graphexplorer.dto.request;

import com.swetha.graphexplorer.domain.enums.RelationshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Generic relationship-creation payload. {@code properties} is intentionally
 * a loose map because each {@link RelationshipType} has a different property
 * shape (job title + start date for WORKS_AT, proficiency level for
 * HAS_SKILL, etc.) — {@code RelationshipPropertyValidator} enforces the
 * correct shape per type at the service layer, which keeps this one request
 * DTO usable for all six relationship types instead of six near-identical
 * endpoints.
 */
public record CreateRelationshipRequest(
        @NotNull(message = "type is required")
        RelationshipType type,

        @NotBlank(message = "sourceId is required")
        String sourceId,

        @NotBlank(message = "targetId is required")
        String targetId,

        Map<String, Object> properties) {

    public Map<String, Object> propertiesOrEmpty() {
        return properties == null ? Map.of() : properties;
    }
}
