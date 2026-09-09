package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.response.ConnectionResponse;
import com.swetha.graphexplorer.dto.response.GraphNodeDto;
import com.swetha.graphexplorer.dto.response.ShortestPathResponse;
import com.swetha.graphexplorer.graph.UserDiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Relationship-discovery endpoints scoped to a single user, kept separate
 * from {@link UserController} (plain entity CRUD) so each controller has
 * one reason to change.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Relationship Discovery")
public class UserDiscoveryController {

    private final UserDiscoveryService userDiscoveryService;

    @Operation(summary = "List a user's direct (1-hop) relationships")
    @GetMapping("/{id}/connections")
    public List<ConnectionResponse> connections(@PathVariable String id) {
        return userDiscoveryService.connections(id);
    }

    @Operation(summary = "List entities both users are connected to")
    @GetMapping("/{id}/common-connections/{otherUserId}")
    public List<GraphNodeDto> commonConnections(@PathVariable String id, @PathVariable String otherUserId) {
        return userDiscoveryService.commonConnections(id, otherUserId);
    }

    @Operation(summary = "Find the shortest path between two users")
    @GetMapping("/{id}/shortest-path/{otherUserId}")
    public ShortestPathResponse shortestPath(@PathVariable String id, @PathVariable String otherUserId) {
        return userDiscoveryService.shortestPath(id, otherUserId);
    }
}
