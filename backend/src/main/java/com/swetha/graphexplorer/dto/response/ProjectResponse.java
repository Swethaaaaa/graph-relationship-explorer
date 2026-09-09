package com.swetha.graphexplorer.dto.response;

import com.swetha.graphexplorer.domain.enums.ProjectStatus;
import java.time.LocalDate;

public record ProjectResponse(
        String id,
        String name,
        String description,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate) {
}
