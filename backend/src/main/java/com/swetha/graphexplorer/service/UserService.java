package com.swetha.graphexplorer.service;

import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.domain.node.User;
import com.swetha.graphexplorer.dto.request.CreateUserRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.UserResponse;
import com.swetha.graphexplorer.exception.DuplicateEntityException;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.mapper.UserMapper;
import com.swetha.graphexplorer.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEntityException(
                    "A user with email '%s' already exists".formatted(request.email()));
        }
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .title(request.title())
                .bio(request.bio())
                .location(request.location())
                .createdAt(Instant.now())
                .build();
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUser(String id) {
        return userMapper.toResponse(findUserOrThrow(id));
    }

    public PageResponse<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> page = (query == null || query.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByFullNameContainingIgnoreCase(query, pageable);
        return PageResponse.of(page.map(userMapper::toResponse));
    }

    User findUserOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.USER, id));
    }
}
