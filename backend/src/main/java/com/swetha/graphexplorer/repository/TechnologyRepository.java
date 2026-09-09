package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.Technology;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TechnologyRepository extends Neo4jRepository<Technology, String> {
}
