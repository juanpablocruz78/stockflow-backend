package com.stockflow.security.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
