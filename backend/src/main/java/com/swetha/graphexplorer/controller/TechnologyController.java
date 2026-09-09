package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateTechnologyRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.TechnologyResponse;
import com.swetha.graphexplorer.service.TechnologyService;
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
@RequestMapping("/api/technologies")
@RequiredArgsConstructor
@Validated
@Tag(name = "Technologies")
public class TechnologyController {

    private final TechnologyService technologyService;

    @Operation(summary = "Create a technology")
    @PostMapping
    public ResponseEntity<TechnologyResponse> createTechnology(@Valid @RequestBody CreateTechnologyRequest request) {
        TechnologyResponse response = technologyService.createTechnology(request);
        return ResponseEntity.created(URI.create("/api/technologies/" + response.id())).body(response);
    }

    @Operation(summary = "Get a technology by id")
    @GetMapping("/{id}")
    public TechnologyResponse getTechnology(@PathVariable String id) {
        return technologyService.getTechnology(id);
    }

    @Operation(summary = "Search technologies by name, paginated")
    @GetMapping("/search")
    public PageResponse<TechnologyResponse> searchTechnologies(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return technologyService.searchTechnologies(q, PageRequest.of(page, size, Sort.by("name")));
    }
}
