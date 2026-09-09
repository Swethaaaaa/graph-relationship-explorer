package com.swetha.graphexplorer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTechnologyRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200)
        String name,

        @Size(max = 100)
        String category) {
}
