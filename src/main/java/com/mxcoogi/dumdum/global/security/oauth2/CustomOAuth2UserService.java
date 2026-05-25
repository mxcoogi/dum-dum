package com.mxcoogi.dumdum.global.security.oauth2;

import com.mxcoogi.dumdum.domain.user.LoginType;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.domain.user.UserRepository;
import com.mxcoogi.dumdum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        LoginType loginType = LoginType.valueOf(registrationId.toUpperCase());
        OAuth2UserInfo userInfo = resolveUserInfo(loginType, oAuth2User.getAttributes());

        User user = userRepository.findByLoginTypeAndProviderId(loginType, userInfo.getProviderId())
                .map(existing -> updateUser(existing, userInfo))
                .orElseGet(() -> createUser(loginType, userInfo));

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo resolveUserInfo(LoginType loginType, Map<String, Object> attributes) {
        return switch (loginType) {
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case APPLE -> new AppleOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 로그인 방식: " + loginType);
        };
    }

    private User createUser(LoginType loginType, OAuth2UserInfo userInfo) {
        User user = User.createOAuthUser(
                userInfo.getNickname(),
                userInfo.getEmail(),
                userInfo.getProfileImageUrl(),
                loginType,
                userInfo.getProviderId()
        );
        return userRepository.save(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getNickname() != null) {
            user.updateNickname(userInfo.getNickname());
        }
        if (userInfo.getProfileImageUrl() != null) {
            user.updateProfileImageUrl(userInfo.getProfileImageUrl());
        }
        return user;
    }
}
