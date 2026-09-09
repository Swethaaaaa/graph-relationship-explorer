package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<User, String> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
