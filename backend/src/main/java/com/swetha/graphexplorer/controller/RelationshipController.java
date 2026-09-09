package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateRelationshipRequest;
import com.swetha.graphexplorer.dto.response.RelationshipResponse;
import com.swetha.graphexplorer.service.RelationshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
@Tag(name = "Relationships")
public class RelationshipController {

    private final RelationshipService relationshipService;

    @Operation(summary = "Create (or update, if one already exists) a relationship between two entities")
    @PostMapping
    public ResponseEntity<RelationshipResponse> createRelationship(
            @Valid @RequestBody CreateRelationshipRequest request) {
        RelationshipResponse response = relationshipService.createRelationship(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete a relationship by its Neo4j element id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRelationship(@PathVariable String id) {
        relationshipService.deleteRelationship(id);
    }
}
