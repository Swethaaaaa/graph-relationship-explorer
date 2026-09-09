package com.swetha.graphexplorer.mapper;

import com.swetha.graphexplorer.domain.node.Project;
import com.swetha.graphexplorer.dto.response.ProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getStartDate(),
                project.getEndDate());
    }
}
