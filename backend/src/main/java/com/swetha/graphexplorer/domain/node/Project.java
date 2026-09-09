package com.swetha.graphexplorer.domain.node;

import com.swetha.graphexplorer.domain.enums.ProjectStatus;
import com.swetha.graphexplorer.domain.relationship.UsesTechnology;
import java.time.LocalDate;
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

@Node("Project")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "name", "status" })
public class Project {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private String id;

    private String name;

    private String description;

    private ProjectStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    @Relationship(type = "PROJECT_USES_TECHNOLOGY", direction = Relationship.Direction.OUTGOING)
    private Set<UsesTechnology> technologies = new HashSet<>();
}
