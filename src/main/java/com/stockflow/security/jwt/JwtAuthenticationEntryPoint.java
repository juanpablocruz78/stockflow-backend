package com.stockflow.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.common.dto.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint  implements AuthenticationEntryPoint {
    private final ObjectMapper mapper;

    public JwtAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String errorType = (String) request.getAttribute("jwt_error");

        String message;

        if (errorType == null) {
            message = "Token ausente o inválido";
        } else {
            message = switch (errorType) {
                case "TOKEN_EXPIRED" -> "El token ha expirado";
                case "TOKEN_MALFORMED" -> "El token está mal formado";
                case "TOKEN_SIGNATURE_INVALID" -> "La firma del token es inválida";
                case "TOKEN_UNSUPPORTED" -> "Token no soportado";
                default -> "Token ausente o inválido";
            };
        }

        ApiError error = new ApiError(
                401,
                "UNAUTHORIZED",
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
