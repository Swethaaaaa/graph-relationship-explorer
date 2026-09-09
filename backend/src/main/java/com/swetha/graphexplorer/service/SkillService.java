package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.node.Skill;
import com.swetha.graphexplorer.dto.request.CreateSkillRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.SkillResponse;
import com.swetha.graphexplorer.exception.DuplicateEntityException;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.mapper.SkillMapper;
import com.swetha.graphexplorer.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Transactional
    public SkillResponse createSkill(CreateSkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateEntityException("A skill named '%s' already exists".formatted(request.name()));
        }
        Skill skill = Skill.builder()
                .name(request.name())
                .category(request.category())
                .build();
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    public SkillResponse getSkill(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.SKILL, id));
        return skillMapper.toResponse(skill);
    }

    public PageResponse<SkillResponse> searchSkills(String query, Pageable pageable) {
        Page<Skill> page = (query == null || query.isBlank())
                ? skillRepository.findAll(pageable)
                : skillRepository.findByNameContainingIgnoreCase(query, pageable);
        return PageResponse.of(page.map(skillMapper::toResponse));
    }
}
