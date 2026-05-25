package com.mxcoogi.dumdum.auth;

import com.mxcoogi.dumdum.auth.dto.LoginRequest;
import com.mxcoogi.dumdum.auth.dto.SignupRequest;
import com.mxcoogi.dumdum.auth.dto.TokenResponse;
import com.mxcoogi.dumdum.domain.auth.ClientType;
import com.mxcoogi.dumdum.domain.auth.RefreshToken;
import com.mxcoogi.dumdum.domain.auth.RefreshTokenRepository;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.domain.user.UserRepository;
import com.mxcoogi.dumdum.global.common.ResponseCode;
import com.mxcoogi.dumdum.global.exception.ApiException;
import com.mxcoogi.dumdum.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ResponseCode.DUPLICATE_EMAIL);
        }

        User user = User.createLocalUser(
                request.getNickname(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(user);

        return issueTokens(user, request.getClientType());
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ResponseCode.INVALID_PASSWORD);
        }

        return issueTokens(user, request.getClientType());
    }

    @Transactional
    public TokenResponse refresh(String refreshToken, ClientType clientType) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApiException(ResponseCode.INVALID_TOKEN));

        // 만료된 토큰은 즉시 삭제 후 예외 — 탈취 토큰이 재사용되지 않도록
        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw new ApiException(ResponseCode.EXPIRED_TOKEN);
        }

        User user = saved.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), clientType);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpiry(clientType) / 1000);

        // rotate: 기존 토큰 무효화 + 새 토큰 저장 (token reuse detection)
        saved.rotate(newRefreshToken, expiresAt);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    private TokenResponse issueTokens(User user, ClientType clientType) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), clientType);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpiry(clientType) / 1000);

        // WEB/MOBILE 각각 1개씩 유지 — 재로그인 시 기존 토큰 rotate, 없으면 신규 생성
        refreshTokenRepository.findByUserAndClientType(user, clientType)
                .ifPresentOrElse(
                        existing -> existing.rotate(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.create(user, clientType, refreshToken, expiresAt))
                );

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
