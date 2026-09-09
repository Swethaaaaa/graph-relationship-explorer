package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.dto.response.GraphResponse;
import com.swetha.graphexplorer.graph.GraphTraversalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
@Tag(name = "Graph Traversal")
public class GraphController {

    private final GraphTraversalService graphTraversalService;

    @Operation(summary = "Expand the subgraph reachable from an entity within a given hop depth")
    @GetMapping("/{entityType}/{id}")
    public GraphResponse expand(
            @PathVariable EntityType entityType,
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int depth) {
        return graphTraversalService.expand(entityType, id, depth);
    }
}
