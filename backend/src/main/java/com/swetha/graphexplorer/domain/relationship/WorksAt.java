package com.swetha.graphexplorer.domain.relationship;

import com.swetha.graphexplorer.domain.node.Company;
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
 * USER_WORKS_AT_COMPANY: User -> Company.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorksAt {

    @RelationshipId
    private Long id;

    private String jobTitle;

    private LocalDate startDate;

    private boolean current;

    @TargetNode
    private Company company;
}
