package com.mxcoogi.dumdum.domain.auth;

import com.mxcoogi.dumdum.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserAndClientType(User user, ClientType clientType);

    void deleteByUser(User user);
}
