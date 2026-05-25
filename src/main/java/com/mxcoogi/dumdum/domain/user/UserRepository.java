package com.mxcoogi.dumdum.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLoginTypeAndProviderId(LoginType loginType, String providerId);

    boolean existsByEmail(String email);
}
