package com.stockflow.common.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
    int status,
    String message,
    Instant timestamp,
    String path,
    Map<String, String> errors
){
    public static ApiError of(int status, String message, String path) {
        return new ApiError(
                status,
                message,
                Instant.now(),
                path,
                null
        );
    }

    public static ApiError ofValidation(
            int status,
            String message,
            String path,
            Map<String, String> errors
    ) {
        return new ApiError(
                status,
                message,
                Instant.now(),
                path,
                errors
        );
    }
}
