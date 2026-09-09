package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SkillRepository extends Neo4jRepository<Skill, String> {

    boolean existsByNameIgnoreCase(String name);

    Page<Skill> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
