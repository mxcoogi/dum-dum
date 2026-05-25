package com.mxcoogi.dumdum.global.security.oauth2;

import com.mxcoogi.dumdum.domain.auth.ClientType;
import com.mxcoogi.dumdum.domain.auth.RefreshToken;
import com.mxcoogi.dumdum.domain.auth.RefreshTokenRepository;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.global.security.CustomUserDetails;
import com.mxcoogi.dumdum.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${oauth2.redirect-uri:http://localhost:3000/oauth2/callback}")
    private String redirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        ClientType clientType = ClientType.WEB;
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), clientType);

        saveRefreshToken(user, clientType, refreshToken);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(User user, ClientType clientType, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpiry(clientType) / 1000);

        refreshTokenRepository.findByUserAndClientType(user, clientType)
                .ifPresentOrElse(
                        existing -> existing.rotate(token, expiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.create(user, clientType, token, expiresAt))
                );
    }
}
