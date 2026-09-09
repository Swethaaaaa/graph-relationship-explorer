package com.swetha.graphexplorer.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

/**
 * Creates Neo4j uniqueness constraints and search indexes on startup.
 * <p>
 * Constraints double as an index in Neo4j, so a uniqueness constraint on a
 * property means lookups by that property (e.g. "find User by email") are
 * O(log n) rather than a label scan. Constraints also enforce data integrity
 * at the database level, independent of application code.
 * <p>
 * All statements use {@code IF NOT EXISTS} so this is safe to run on every
 * startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(0)
public class GraphSchemaInitializer implements ApplicationRunner {

    private final Neo4jClient neo4jClient;

    private static final List<String> CONSTRAINTS = List.of(
            "CREATE CONSTRAINT user_id_unique IF NOT EXISTS FOR (n:User) REQUIRE n.id IS UNIQUE",
            "CREATE CONSTRAINT user_email_unique IF NOT EXISTS FOR (n:User) REQUIRE n.email IS UNIQUE",
            "CREATE CONSTRAINT company_id_unique IF NOT EXISTS FOR (n:Company) REQUIRE n.id IS UNIQUE",
            "CREATE CONSTRAINT company_name_unique IF NOT EXISTS FOR (n:Company) REQUIRE n.name IS UNIQUE",
            "CREATE CONSTRAINT team_id_unique IF NOT EXISTS FOR (n:Team) REQUIRE n.id IS UNIQUE",
            "CREATE CONSTRAINT skill_id_unique IF NOT EXISTS FOR (n:Skill) REQUIRE n.id IS UNIQUE",
            "CREATE CONSTRAINT skill_name_unique IF NOT EXISTS FOR (n:Skill) REQUIRE n.name IS UNIQUE",
            "CREATE CONSTRAINT project_id_unique IF NOT EXISTS FOR (n:Project) REQUIRE n.id IS UNIQUE",
            "CREATE CONSTRAINT technology_id_unique IF NOT EXISTS FOR (n:Technology) REQUIRE n.id IS UNIQUE",
            "CREATE CONSTRAINT technology_name_unique IF NOT EXISTS FOR (n:Technology) REQUIRE n.name IS UNIQUE"
    );

    // Non-unique search indexes for properties that are frequently filtered/
    // searched on but are not, by themselves, unique identifiers.
    private static final List<String> INDEXES = List.of(
            "CREATE INDEX user_full_name_idx IF NOT EXISTS FOR (n:User) ON (n.fullName)",
            "CREATE INDEX team_name_idx IF NOT EXISTS FOR (n:Team) ON (n.name)",
            "CREATE INDEX project_name_idx IF NOT EXISTS FOR (n:Project) ON (n.name)"
    );

    @Override
    public void run(ApplicationArguments args) {
        log.info("Applying Neo4j constraints and indexes ({} constraints, {} indexes)",
                CONSTRAINTS.size(), INDEXES.size());
        CONSTRAINTS.forEach(this::execute);
        INDEXES.forEach(this::execute);
    }

    private void execute(String cypher) {
        neo4jClient.query(cypher).run();
    }
}
