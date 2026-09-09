package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<User, String> {
}
