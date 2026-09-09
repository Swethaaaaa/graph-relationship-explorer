package com.swetha.graphexplorer.controller;

import com.swetha.graphexplorer.dto.request.CreateUserRequest;
import com.swetha.graphexplorer.dto.response.PageResponse;
import com.swetha.graphexplorer.dto.response.UserResponse;
import com.swetha.graphexplorer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a user")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.created(URI.create("/api/users/" + response.id())).body(response);
    }

    @Operation(summary = "Get a user by id")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Search users by name, paginated")
    @GetMapping("/search")
    public PageResponse<UserResponse> searchUsers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return userService.searchUsers(q, PageRequest.of(page, size, Sort.by("fullName")));
    }
}
