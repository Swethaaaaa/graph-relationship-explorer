package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.Project;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ProjectRepository extends Neo4jRepository<Project, String> {
}
