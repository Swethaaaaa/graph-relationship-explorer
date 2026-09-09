package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.node.Project;
import com.swetha.graphexplorer.dto.request.CreateProjectRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.ProjectResponse;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.mapper.ProjectMapper;
import com.swetha.graphexplorer.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    // Project names are intentionally not globally unique (see DATA_MODEL.md) —
    // different teams/companies can reasonably each have a "Website Redesign".
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
        return projectMapper.toResponse(projectRepository.save(project));
    }

    public ProjectResponse getProject(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.PROJECT, id));
        return projectMapper.toResponse(project);
    }

    public PageResponse<ProjectResponse> searchProjects(String query, Pageable pageable) {
        Page<Project> page = (query == null || query.isBlank())
                ? projectRepository.findAll(pageable)
                : projectRepository.findByNameContainingIgnoreCase(query, pageable);
        return PageResponse.of(page.map(projectMapper::toResponse));
    }
}
