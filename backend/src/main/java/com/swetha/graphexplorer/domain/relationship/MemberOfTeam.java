package com.swetha.graphexplorer.domain.relationship;

import com.swetha.graphexplorer.domain.node.Team;
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
 * USER_MEMBER_OF_TEAM: User -> Team.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOfTeam {

    @RelationshipId
    private Long id;

    private String role;

    private LocalDate joinedDate;

    @TargetNode
    private Team team;
}
