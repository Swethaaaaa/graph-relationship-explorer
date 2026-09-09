package com.swetha.graphexplorer.dto.request;

import com.swetha.graphexplorer.domain.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateProjectRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull(message = "status is required")
        ProjectStatus status,

        LocalDate startDate,

        LocalDate endDate) {
}
