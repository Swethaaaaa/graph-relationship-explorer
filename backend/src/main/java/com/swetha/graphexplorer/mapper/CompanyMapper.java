package com.swetha.graphexplorer.mapper;

import com.swetha.graphexplorer.domain.node.Company;
import com.swetha.graphexplorer.dto.response.CompanyResponse;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getIndustry(),
                company.getWebsite(),
                company.getSize(),
                company.getFoundedYear());
    }
}
