package com.stockflow.common.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------
    // 400 - Validaciones DTO
    // -------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                ApiError.of(
                        HttpStatus.BAD_REQUEST.value(),
                        message,
                        request.getRequestURI()
                )
        );
    }

    // -------------------------
    // 404 - Entidad no encontrada
    // -------------------------
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    // -------------------------
    // 500 - Error inesperado
    // -------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Unexpected error occurred",
                        request.getRequestURI()
                )
        );
    }

    // 409
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleConflict(
            ProductAlreadyExistsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.of(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }

    /*  403 - Forbidden */
    // -------------------------
    // 403 - Acceso denegado
    // -------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiError.of(
                        HttpStatus.FORBIDDEN.value(),
                        "Access denied",
                        request.getRequestURI()
                )
        );
    }

    // -------------------------
    // 401 - No autenticado
    // -------------------------
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        request.getRequestURI()
                )
        );
    }

}
