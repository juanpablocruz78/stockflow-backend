package com.stockflow.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.stockflow.security.entity.RefreshToken;
import com.stockflow.security.entity.Role;
import com.stockflow.security.entity.User;
import com.stockflow.security.repository.RefreshTokenRepository;
import com.stockflow.security.repository.RoleRepository;
import com.stockflow.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;


    @Test
    void shouldReturn401WhenNoTokenProvided() throws Exception {

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenTokenIsInvalid() throws Exception {

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWithValidToken() throws Exception {

        // 1. PREPARAR: Crear el rol y el usuario que el test espera
        Role roleAdmin = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName("ADMIN");
                            return roleRepository.save(newRole);
                        });

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@stockflow.com");
        admin.setPassword(passwordEncoder.encode("123456")); // ¡Importante encriptar!
        admin.setRoles(Set.of(roleAdmin));
        userRepository.save(admin);
        // 1️⃣ Login
        String loginRequest = """
        {
            "username": "admin",
            "password": "123456"
        }
        """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(response, "$.accessToken");

        // 2️⃣ Usar token contra endpoint protegido
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {

        Role roleAdmin = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ADMIN");
                    return roleRepository.save(newRole);
                });

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@stockflow.com");
        admin.setPassword(passwordEncoder.encode("123456")); // ¡Importante encriptar!
        admin.setRoles(Set.of(roleAdmin));
        userRepository.save(admin);

        String loginRequest = """
        {
            "username": "admin",
            "password": "123456"
        }
        """;

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = JsonPath.read(loginResponse, "$.refreshToken");

        String refreshRequest = """
        {
            "refreshToken": "%s"
        }
        """.formatted(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailWhenRefreshTokenIsInvalid() throws Exception {

        String refreshRequest = """
        {
            "refreshToken": "invalid-token-123"
        }
        """;

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailWhenRefreshTokenIsExpired() throws Exception {

        this.clean();
        // crear rol
        Role roleUser = roleRepository
                .findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });

        // crear usuario
        User user = new User();
        user.setUsername("expiredUser_"+ UUID.randomUUID());
        user.setEmail("expired@example.com"); //  obligatorio
        user.setPassword(passwordEncoder.encode("123456"));

        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        user.setRoles(roles);

        user = userRepository.save(user);

        // crear refresh expirado
        RefreshToken expired = new RefreshToken();
        expired.setToken("expired-token");
        expired.setUser(user);
        expired.setExpiryDate(Instant.now().minusSeconds(60));

        refreshTokenRepository.save(expired);

        // ejecutar refresh
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
              "refreshToken": "expired-token"
            }
            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWithValidToken2() throws Exception {

        String accessToken = loginAndGetAccessToken();

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    private String loginAndGetAccessToken() throws Exception {

        this.clean();
        String username = "user_" + UUID.randomUUID();

        Role roleUser = roleRepository.findByName("USER")
                .orElseThrow();

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(Set.of(roleUser));

        userRepository.save(user);

        String loginRequest = """
        {
            "username": "%s",
            "password": "123456"
        }
        """.formatted(username);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);

        return jsonNode.get("accessToken").asText();
    }


    @Test
    void shouldReturn401WithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenUserWithoutAdminRoleAccessAdminEndpoint() throws Exception {

        String userToken = loginAndGetAccessToken(); // USER role

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String loginAsAdminAndGetAccessToken() throws Exception {

        this.clean();
        String username = "admin_" + UUID.randomUUID();

        Role roleAdmin = roleRepository.findByName("ADMIN")
                .orElseThrow();

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(Set.of(roleAdmin));

        userRepository.save(user);

        String loginRequest = """
        {
            "username": "%s",
            "password": "123456"
        }
        """.formatted(username);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);

        return jsonNode.get("accessToken").asText();
    }

    @Test
    void shouldAllowAdminAccessAdminEndpoint() throws Exception {

        String adminToken = loginAsAdminAndGetAccessToken();

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGenerateNewAccessTokenWithRefresh() throws Exception {

        String accessToken = loginAndGetAccessToken();
        String refreshToken = extractRefreshTokenFromLogin(); // helper similar

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "refreshToken": "%s"
                        }
                    """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);

        String newAccessToken = jsonNode.get("accessToken").asText();

        assertNotEquals(accessToken, newAccessToken);
    }

    private String extractRefreshTokenFromLogin() throws Exception {

        this.clean();
        String username = "user_" + UUID.randomUUID();

        Role roleUser = roleRepository.findByName("USER")
                .orElseThrow();

        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(Set.of(roleUser));

        userRepository.save(user);

        String loginRequest = """
        {
            "username": "%s",
            "password": "123456"
        }
        """.formatted(username);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);

        return jsonNode.get("refreshToken").asText();
    }

    @BeforeEach
    void clean() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }
}
