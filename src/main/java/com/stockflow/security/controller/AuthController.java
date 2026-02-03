package com.stockflow.security.controller;

import com.stockflow.security.dto.AuthRegisterRequest;
import com.stockflow.security.dto.AuthRequest;
import com.stockflow.security.dto.AuthResponse;
import com.stockflow.security.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
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
}
