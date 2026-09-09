package com.swetha.graphexplorer.dto.request;

import com.swetha.graphexplorer.domain.enums.CompanySize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(
        @NotBlank(message = "name is required")
        @Size(max = 200)
        String name,

        @Size(max = 200)
        String industry,

        @Size(max = 300)
        String website,

        CompanySize size,

        @Min(value = 1800, message = "foundedYear must be a realistic year")
        @Max(value = 2100, message = "foundedYear must be a realistic year")
        Integer foundedYear) {
}
