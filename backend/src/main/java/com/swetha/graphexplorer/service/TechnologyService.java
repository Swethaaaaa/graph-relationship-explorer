package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.node.Technology;
import com.swetha.graphexplorer.dto.request.CreateTechnologyRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.TechnologyResponse;
import com.swetha.graphexplorer.exception.DuplicateEntityException;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.mapper.TechnologyMapper;
import com.swetha.graphexplorer.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechnologyService {

    private final TechnologyRepository technologyRepository;
    private final TechnologyMapper technologyMapper;

    @Transactional
    public TechnologyResponse createTechnology(CreateTechnologyRequest request) {
        if (technologyRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateEntityException(
                    "A technology named '%s' already exists".formatted(request.name()));
        }
        Technology technology = Technology.builder()
                .name(request.name())
                .category(request.category())
                .build();
        return technologyMapper.toResponse(technologyRepository.save(technology));
    }

    public TechnologyResponse getTechnology(String id) {
        Technology technology = technologyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.TECHNOLOGY, id));
        return technologyMapper.toResponse(technology);
    }

    public PageResponse<TechnologyResponse> searchTechnologies(String query, Pageable pageable) {
        Page<Technology> page = (query == null || query.isBlank())
                ? technologyRepository.findAll(pageable)
                : technologyRepository.findByNameContainingIgnoreCase(query, pageable);
        return PageResponse.of(page.map(technologyMapper::toResponse));
    }
}
