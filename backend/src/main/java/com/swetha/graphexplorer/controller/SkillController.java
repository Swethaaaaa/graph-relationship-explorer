package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateSkillRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.SkillResponse;
import com.swetha.graphexplorer.service.SkillService;
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
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Validated
@Tag(name = "Skills")
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "Create a skill")
    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody CreateSkillRequest request) {
        SkillResponse response = skillService.createSkill(request);
        return ResponseEntity.created(URI.create("/api/skills/" + response.id())).body(response);
    }

    @Operation(summary = "Get a skill by id")
    @GetMapping("/{id}")
    public SkillResponse getSkill(@PathVariable String id) {
        return skillService.getSkill(id);
    }

    @Operation(summary = "Search skills by name, paginated")
    @GetMapping("/search")
    public PageResponse<SkillResponse> searchSkills(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return skillService.searchSkills(q, PageRequest.of(page, size, Sort.by("name")));
    }
}
