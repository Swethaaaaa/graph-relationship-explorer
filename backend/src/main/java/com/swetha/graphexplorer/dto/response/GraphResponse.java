package com.swetha.graphexplorer.dto.response;

import java.util.List;

/**
 * The induced subgraph reachable from a starting entity within a given
 * traversal depth: every node encountered, and every relationship that
 * exists between two nodes in that set (not just the relationships that lie
 * on a shortest path to the start node).
 */
public record GraphResponse(
        List<GraphNodeDto> nodes,
        List<GraphEdgeDto> edges,
        int depth) {
}
