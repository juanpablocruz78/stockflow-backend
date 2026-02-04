package com.stockflow.security.service;

import com.stockflow.security.dto.AuthRegisterRequest;
import com.stockflow.security.dto.AuthRequest;
import com.stockflow.security.dto.AuthResponse;
import com.stockflow.security.entity.Role;
import com.stockflow.security.entity.User;
import com.stockflow.security.jwt.JwtService;
import com.stockflow.security.repository.RoleRepository;
import com.stockflow.security.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.stockflow.security.service.RefreshTokenService;

import java.util.Set;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authManager,
                       JwtService jwtService,
                       UserRepository userRepo,
                       PasswordEncoder passwordEncoder, RoleRepository roleRepository, RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(AuthRegisterRequest request) {

        Set<Role> roles = roleRepository.findByNameIn(request.roles());
        if (roles.isEmpty()) {
            throw new RuntimeException("Roles no válidos");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // 🔥 CLAVE
        user.setRoles(roles);

        userRepo.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        System.out.println("Login attempt: " + request.username());

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepo.findByUsername(request.username())
                .orElseThrow();

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = refreshTokenService.create(user).getToken();

        return new AuthResponse(accessToken, refreshToken);
    }
}
