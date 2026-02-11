package com.stockflow.security.controller;

import com.stockflow.security.dto.*;
import com.stockflow.security.entity.RefreshToken;
import com.stockflow.security.entity.User;
import com.stockflow.security.jwt.JwtService;
import com.stockflow.security.service.AuthService;
import com.stockflow.security.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService service, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody AuthRegisterRequest request) {

        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request) {

        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return service.refresh(request.refreshToken());

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LogoutRequest request
    ) {
        System.out.println(">>> POST /auth/logout HIT <<<");
        service.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

}
