package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.node.Company;
import com.swetha.graphexplorer.dto.request.CreateCompanyRequest;
import com.swetha.graphexplorer.dto.response.CompanyResponse;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.exception.DuplicateEntityException;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.mapper.CompanyMapper;
import com.swetha.graphexplorer.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateEntityException(
                    "A company named '%s' already exists".formatted(request.name()));
        }
        Company company = Company.builder()
                .name(request.name())
                .industry(request.industry())
                .website(request.website())
                .size(request.size())
                .foundedYear(request.foundedYear())
                .build();
        return companyMapper.toResponse(companyRepository.save(company));
    }

    public CompanyResponse getCompany(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.COMPANY, id));
        return companyMapper.toResponse(company);
    }

    public PageResponse<CompanyResponse> searchCompanies(String query, Pageable pageable) {
        Page<Company> page = (query == null || query.isBlank())
                ? companyRepository.findAll(pageable)
                : companyRepository.findByNameContainingIgnoreCase(query, pageable);
        return PageResponse.of(page.map(companyMapper::toResponse));
    }
}
