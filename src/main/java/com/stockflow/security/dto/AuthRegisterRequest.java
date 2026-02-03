package com.stockflow.security.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
        @NotBlank
        String username,

        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String role
) {
}
