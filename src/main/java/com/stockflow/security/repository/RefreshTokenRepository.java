package com.stockflow.security.repository;

import com.stockflow.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    void deleteByUser(Long userId);

    @Modifying
    @Transactional
    void deleteByToken(String token);
}
