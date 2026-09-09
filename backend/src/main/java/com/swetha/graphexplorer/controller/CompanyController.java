package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateCompanyRequest;
import com.swetha.graphexplorer.dto.response.CompanyResponse;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.service.CompanyService;
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
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Validated
@Tag(name = "Companies")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Create a company")
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.created(URI.create("/api/companies/" + response.id())).body(response);
    }

    @Operation(summary = "Get a company by id")
    @GetMapping("/{id}")
    public CompanyResponse getCompany(@PathVariable String id) {
        return companyService.getCompany(id);
    }

    @Operation(summary = "Search companies by name, paginated")
    @GetMapping("/search")
    public PageResponse<CompanyResponse> searchCompanies(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return companyService.searchCompanies(q, PageRequest.of(page, size, Sort.by("name")));
    }
}
