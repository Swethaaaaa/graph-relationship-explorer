package com.swetha.graphexplorer.dto.response;

import com.swetha.graphexplorer.domain.enums.CompanySize;

public record CompanyResponse(
        String id,
        String name,
        String industry,
        String website,
        CompanySize size,
        Integer foundedYear) {
}
