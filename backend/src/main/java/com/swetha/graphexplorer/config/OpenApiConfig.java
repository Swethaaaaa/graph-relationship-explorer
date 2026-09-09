package com.swetha.graphexplorer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI graphExplorerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Graph-Based Relationship Explorer API")
                        .description("Entity management, relationship management, graph traversal, and "
                                + "relationship discovery over a Neo4j-backed professional network graph.")
                        .version("v1"));
    }
}
