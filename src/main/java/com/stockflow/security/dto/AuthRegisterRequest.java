package com.stockflow.security.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record AuthRegisterRequest(
        @NotBlank
        String username,

        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotEmpty
        Set<String> roles   // ["USER", "ADMIN"]
) {
}
