package com.swetha.graphexplorer.dto.response;

import com.swetha.graphexplorer.domain.enums.EntityType;
import java.util.Map;

/**
 * One node in a graph API response. {@code name} is the node's
 * display-friendly label (User.fullName, or .name for every other type) so
 * the frontend never has to know per-entity-type which property to render.
 * {@code properties} carries everything else (minus {@code id}, which is
 * already the top-level field).
 */
public record GraphNodeDto(
        String id,
        EntityType type,
        String name,
        Map<String, Object> properties) {
}
