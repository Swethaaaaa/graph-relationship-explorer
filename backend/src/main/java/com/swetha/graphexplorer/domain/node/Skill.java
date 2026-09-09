package com.swetha.graphexplorer.domain.node;

import com.swetha.graphexplorer.domain.StringUuidGenerator;
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

@Node("Skill")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "name" })
public class Skill {

    @Id
    @GeneratedValue(StringUuidGenerator.class)
    private String id;

    private String name;

    /** Free-form grouping, e.g. "Programming Language", "Cloud", "Soft Skill". */
    private String category;
}
