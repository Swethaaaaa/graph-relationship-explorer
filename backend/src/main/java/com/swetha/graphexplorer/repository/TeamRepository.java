package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TeamRepository extends Neo4jRepository<Team, String> {

    boolean existsByNameIgnoreCase(String name);

    Page<Team> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
