package com.swetha.graphexplorer.dto.response;

import java.time.Instant;

public record UserResponse(
        String id,
        String fullName,
        String email,
        String title,
        String bio,
        String location,
        Instant createdAt) {
}
