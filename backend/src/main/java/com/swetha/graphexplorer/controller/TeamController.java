package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateTeamRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.TeamResponse;
import com.swetha.graphexplorer.service.TeamService;
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
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Validated
@Tag(name = "Teams")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Create a team")
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        TeamResponse response = teamService.createTeam(request);
        return ResponseEntity.created(URI.create("/api/teams/" + response.id())).body(response);
    }

    @Operation(summary = "Get a team by id")
    @GetMapping("/{id}")
    public TeamResponse getTeam(@PathVariable String id) {
        return teamService.getTeam(id);
    }

    @Operation(summary = "Search teams by name, paginated")
    @GetMapping("/search")
    public PageResponse<TeamResponse> searchTeams(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return teamService.searchTeams(q, PageRequest.of(page, size, Sort.by("name")));
    }
}
