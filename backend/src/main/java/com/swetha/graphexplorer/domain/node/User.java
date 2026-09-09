package com.swetha.graphexplorer.domain.node;

import com.swetha.graphexplorer.domain.StringUuidGenerator;
import com.swetha.graphexplorer.domain.relationship.CollaboratedWith;
import com.swetha.graphexplorer.domain.relationship.HasSkill;
import com.swetha.graphexplorer.domain.relationship.MemberOfTeam;
import com.swetha.graphexplorer.domain.relationship.WorkedOnProject;
import com.swetha.graphexplorer.domain.relationship.WorksAt;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("User")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "fullName", "email" })
public class User {

    @Id
    @GeneratedValue(StringUuidGenerator.class)
    private String id;

    private String fullName;

    private String email;

    private String title;

    private String bio;

    private String location;

    private Instant createdAt;

    @Builder.Default
    @Relationship(type = "USER_WORKS_AT_COMPANY", direction = Relationship.Direction.OUTGOING)
    private Set<WorksAt> companies = new HashSet<>();

    @Builder.Default
    @Relationship(type = "USER_MEMBER_OF_TEAM", direction = Relationship.Direction.OUTGOING)
    private Set<MemberOfTeam> teams = new HashSet<>();

    @Builder.Default
    @Relationship(type = "USER_HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<HasSkill> skills = new HashSet<>();

    @Builder.Default
    @Relationship(type = "USER_WORKED_ON_PROJECT", direction = Relationship.Direction.OUTGOING)
    private Set<WorkedOnProject> projects = new HashSet<>();

    @Builder.Default
    @Relationship(type = "USER_COLLABORATED_WITH_USER", direction = Relationship.Direction.OUTGOING)
    private Set<CollaboratedWith> collaborations = new HashSet<>();
}
