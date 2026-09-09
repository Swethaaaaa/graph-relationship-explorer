package com.swetha.graphexplorer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200)
        String name,

        @Size(max = 2000)
        String description,

        @Size(max = 200)
        String department) {
}
