package com.swetha.graphexplorer.domain.relationship;

import com.swetha.graphexplorer.domain.node.Technology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * PROJECT_USES_TECHNOLOGY: Project -> Technology.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsesTechnology {

    @RelationshipId
    private Long id;

    /** e.g. "primary backend language", "CI/CD", "data store". */
    private String usageContext;

    @TargetNode
    private Technology technology;
}
