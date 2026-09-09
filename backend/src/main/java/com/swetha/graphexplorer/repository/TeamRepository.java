package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.Team;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TeamRepository extends Neo4jRepository<Team, String> {
}
