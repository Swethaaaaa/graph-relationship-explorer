package com.swetha.graphexplorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GraphExplorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphExplorerApplication.class, args);
    }
}
