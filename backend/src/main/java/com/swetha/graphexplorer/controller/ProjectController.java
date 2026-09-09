package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateProjectRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.ProjectResponse;
import com.swetha.graphexplorer.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create a project")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.created(URI.create("/api/projects/" + response.id())).body(response);
    }

    @Operation(summary = "Get a project by id")
    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable String id) {
        return projectService.getProject(id);
    }

    @Operation(summary = "Search projects by name, paginated")
    @GetMapping("/search")
    public PageResponse<ProjectResponse> searchProjects(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return projectService.searchProjects(q, PageRequest.of(page, size, Sort.by("name")));
    }
}
