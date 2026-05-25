package com.mxcoogi.dumdum.global.security;

import com.mxcoogi.dumdum.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    /** LOCAL 로그인용 */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /** OAuth2 로그인용 */
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /** JWT subject = userId */
    @Override
    public String getUsername() {
        return String.valueOf(user.getId());
    }

    /** OAuth2User.getName() = providerId */
    @Override
    public String getName() {
        return user.getProviderId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
