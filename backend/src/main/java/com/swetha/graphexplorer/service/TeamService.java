package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.node.Team;
import com.swetha.graphexplorer.dto.request.CreateTeamRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.TeamResponse;
import com.swetha.graphexplorer.exception.DuplicateEntityException;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.mapper.TeamMapper;
import com.swetha.graphexplorer.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateEntityException("A team named '%s' already exists".formatted(request.name()));
        }
        Team team = Team.builder()
                .name(request.name())
                .description(request.description())
                .department(request.department())
                .build();
        return teamMapper.toResponse(teamRepository.save(team));
    }

    public TeamResponse getTeam(String id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.TEAM, id));
        return teamMapper.toResponse(team);
    }

    public PageResponse<TeamResponse> searchTeams(String query, Pageable pageable) {
        Page<Team> page = (query == null || query.isBlank())
                ? teamRepository.findAll(pageable)
                : teamRepository.findByNameContainingIgnoreCase(query, pageable);
        return PageResponse.of(page.map(teamMapper::toResponse));
    }
}
