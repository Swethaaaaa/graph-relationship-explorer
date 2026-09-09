package com.swetha.graphexplorer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "fullName is required")
        @Size(max = 200, message = "fullName must be at most 200 characters")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        @Size(max = 320)
        String email,

        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @Size(max = 2000, message = "bio must be at most 2000 characters")
        String bio,

        @Size(max = 200, message = "location must be at most 200 characters")
        String location) {
}
