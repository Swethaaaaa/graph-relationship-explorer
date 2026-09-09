package com.swetha.graphexplorer.domain;

import java.util.UUID;
import org.springframework.data.neo4j.core.schema.IdGenerator;

/**
 * Generates a random UUID as its String representation. Every node's
 * {@code @Id} field is typed {@code String} (so it's safe to expose
 * directly in REST URLs), but Spring Data Neo4j's built-in
 * {@code GeneratedValue.UUIDGenerator} implements {@code IdGenerator<UUID>}
 * — using it against a {@code String} field throws a
 * {@code ClassCastException} at save time (found by actually running the
 * app against a real Neo4j, not just compiling it). This generator closes
 * that gap.
 */
public class StringUuidGenerator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        return UUID.randomUUID().toString();
    }
}
