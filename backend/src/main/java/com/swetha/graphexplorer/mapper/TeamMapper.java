package com.swetha.graphexplorer.mapper;

import com.swetha.graphexplorer.domain.node.Team;
import com.swetha.graphexplorer.dto.response.TeamResponse;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team team) {
        return new TeamResponse(team.getId(), team.getName(), team.getDescription(), team.getDepartment());
    }
}
