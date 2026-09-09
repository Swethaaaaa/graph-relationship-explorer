package com.swetha.graphexplorer.domain.relationship;

import com.swetha.graphexplorer.domain.enums.ProficiencyLevel;
import com.swetha.graphexplorer.domain.node.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * USER_HAS_SKILL: User -> Skill.
 */
@RelationshipProperties
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HasSkill {

    @RelationshipId
    private Long id;

    private ProficiencyLevel proficiencyLevel;

    private Integer yearsOfExperience;

    @TargetNode
    private Skill skill;
}
