package com.swetha.graphexplorer.domain.relationship;

import com.swetha.graphexplorer.domain.node.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * USER_COLLABORATED_WITH_USER: User -> User (self-referential).
 * Stored as a single directed edge; application code that needs an
 * undirected "did these two ever collaborate" check queries the
 * relationship in either direction (see CollaborationRepository, Phase 4).
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratedWith {

    @RelationshipId
    private Long id;

    /** Free-text description of the shared context, e.g. a project name. */
    private String context;

    private Integer collaborationCount;

    @TargetNode
    private User collaborator;
}
