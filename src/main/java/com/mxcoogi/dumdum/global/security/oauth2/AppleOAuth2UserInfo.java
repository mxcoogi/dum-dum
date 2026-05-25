package com.mxcoogi.dumdum.global.security.oauth2;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    /** Apple은 최초 로그인 시에만 이름 제공 — 이후 null 가능 */
    @Override
    public String getNickname() {
        Map<String, Object> name = (Map<String, Object>) attributes.get("name");
        if (name == null) return null;
        String firstName = (String) name.get("firstName");
        String lastName = (String) name.get("lastName");
        return (firstName != null ? firstName : "") + (lastName != null ? lastName : "");
    }

    @Override
    public String getProfileImageUrl() {
        return null;
    }
}
