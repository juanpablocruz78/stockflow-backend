package com.stockflow.common.exception;

import java.time.Instant;

public record ApiError(
    int status,
    String message,
    Instant timestamp,
    String path
){
    public static ApiError of(int status, String message, String path) {
        return new ApiError(
                status,
                message,
                Instant.now(),
                path
        );
    }
}
