package com.swetha.graphexplorer.dto.response;

import java.util.List;

/**
 * An ordered path (as opposed to {@link GraphResponse}'s unordered
 * subgraph): {@code nodes} and {@code edges} are in walk order from the
 * source entity to the target entity.
 */
public record ShortestPathResponse(
        int length,
        List<GraphNodeDto> nodes,
        List<GraphEdgeDto> edges) {
}
