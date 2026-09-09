package com.swetha.graphexplorer.mapper;

import com.swetha.graphexplorer.domain.node.Skill;
import com.swetha.graphexplorer.dto.response.SkillResponse;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {

    public SkillResponse toResponse(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory());
    }
}
