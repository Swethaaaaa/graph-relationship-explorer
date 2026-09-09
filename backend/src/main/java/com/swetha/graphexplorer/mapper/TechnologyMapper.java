package com.swetha.graphexplorer.mapper;

import com.swetha.graphexplorer.domain.node.Technology;
import com.swetha.graphexplorer.dto.response.TechnologyResponse;
import org.springframework.stereotype.Component;

@Component
public class TechnologyMapper {

    public TechnologyResponse toResponse(Technology technology) {
        return new TechnologyResponse(technology.getId(), technology.getName(), technology.getCategory());
    }
}
