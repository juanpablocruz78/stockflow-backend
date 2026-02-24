package com.stockflow.security.service;

import com.stockflow.security.entity.RefreshToken;
import com.stockflow.security.entity.User;
import com.stockflow.security.exception.InvalidRefreshTokenException;
import com.stockflow.security.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RefreshToken create(User user) {
        repository.deleteByUser(user.getId()); // 1 sesión activa (opcional)

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plusMillis(refreshExpiration)
        );

        return repository.save(token);
    }

    public RefreshToken verify(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (rt.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(rt);
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        return rt;
    }

    @Transactional
    public RefreshToken rotate(String oldToken) {

        RefreshToken existing = verify(oldToken);

        User user = existing.getUser();

        repository.deleteByUser(user.getId());

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(
                Instant.now().plusMillis(refreshExpiration)
        );

        return repository.save(newToken);
    }

    @Transactional
    public void deleteByToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido"));

        repository.delete(refreshToken);
    }

}
