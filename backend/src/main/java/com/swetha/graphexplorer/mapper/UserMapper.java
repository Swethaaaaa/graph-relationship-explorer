package com.swetha.graphexplorer.mapper;

import com.swetha.graphexplorer.domain.node.User;
import com.swetha.graphexplorer.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getTitle(),
                user.getBio(),
                user.getLocation(),
                user.getCreatedAt());
    }
}
