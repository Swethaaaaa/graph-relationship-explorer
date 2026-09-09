package com.swetha.graphexplorer.repository;

import com.swetha.graphexplorer.domain.node.Company;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CompanyRepository extends Neo4jRepository<Company, String> {
}
