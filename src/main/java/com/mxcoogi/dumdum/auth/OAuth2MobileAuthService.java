package com.mxcoogi.dumdum.auth;

import com.mxcoogi.dumdum.auth.dto.TokenResponse;
import com.mxcoogi.dumdum.domain.auth.ClientType;
import com.mxcoogi.dumdum.domain.auth.RefreshToken;
import com.mxcoogi.dumdum.domain.auth.RefreshTokenRepository;
import com.mxcoogi.dumdum.domain.user.LoginType;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.domain.user.UserRepository;
import com.mxcoogi.dumdum.global.common.ResponseCode;
import com.mxcoogi.dumdum.global.exception.ApiException;
import com.mxcoogi.dumdum.global.security.jwt.JwtTokenProvider;
import com.mxcoogi.dumdum.global.security.oauth2.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2MobileAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Transactional
    public TokenResponse login(String provider, String providerToken, ClientType clientType) {
        LoginType loginType = parseLoginType(provider);
        OAuth2UserInfo userInfo = fetchUserInfo(loginType, providerToken);

        User user = userRepository.findByLoginTypeAndProviderId(loginType, userInfo.getProviderId())
                .map(existing -> updateUser(existing, userInfo))
                .orElseGet(() -> createUser(loginType, userInfo));

        return issueTokens(user, clientType);
    }

    private LoginType parseLoginType(String provider) {
        try {
            return LoginType.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(ResponseCode.INVALID_INPUT);
        }
    }

    private OAuth2UserInfo fetchUserInfo(LoginType loginType, String providerToken) {
        Map<String, Object> attributes = switch (loginType) {
            case KAKAO -> fetchFromUrl("https://kapi.kakao.com/v2/user/me", providerToken);
            case NAVER -> fetchFromUrl("https://openapi.naver.com/v1/nid/me", providerToken);
            case GOOGLE -> fetchFromUrl("https://www.googleapis.com/oauth2/v3/userinfo", providerToken);
            case APPLE -> decodeAppleIdToken(providerToken);
            default -> throw new ApiException(ResponseCode.INVALID_INPUT);
        };

        return switch (loginType) {
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case APPLE -> new AppleOAuth2UserInfo(attributes);
            default -> throw new ApiException(ResponseCode.INVALID_INPUT);
        };
    }

    private Map<String, Object> fetchFromUrl(String url, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ApiException(ResponseCode.INVALID_TOKEN);
        }
        return response.getBody();
    }

    /** Apple은 userinfo 엔드포인트 없음 — ID token(JWT) payload 디코딩으로 유저 정보 추출 */
    private Map<String, Object> decodeAppleIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Claims claims = Jwts.parser().unsecured().build()
                    .parseUnsecuredClaims(payload).getPayload();
            return Map.of(
                    "sub", claims.getSubject(),
                    "email", claims.get("email", String.class)
            );
        } catch (Exception e) {
            throw new ApiException(ResponseCode.INVALID_TOKEN);
        }
    }

    private User createUser(LoginType loginType, OAuth2UserInfo userInfo) {
        User user = User.createOAuthUser(
                userInfo.getNickname() != null ? userInfo.getNickname() : "사용자",
                userInfo.getEmail(),
                userInfo.getProfileImageUrl(),
                loginType,
                userInfo.getProviderId()
        );
        return userRepository.save(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getNickname() != null) user.updateNickname(userInfo.getNickname());
        if (userInfo.getProfileImageUrl() != null) user.updateProfileImageUrl(userInfo.getProfileImageUrl());
        return user;
    }

    private TokenResponse issueTokens(User user, ClientType clientType) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), clientType);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpiry(clientType) / 1000);

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
