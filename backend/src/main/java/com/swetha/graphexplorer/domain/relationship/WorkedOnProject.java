package com.swetha.graphexplorer.domain.relationship;

import com.swetha.graphexplorer.domain.node.Project;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * USER_WORKED_ON_PROJECT: User -> Project.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkedOnProject {

    @RelationshipId
    private Long id;

    private String role;

    private LocalDate startDate;

    private LocalDate endDate;

    @TargetNode
    private Project project;
}
