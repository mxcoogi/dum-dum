package com.mxcoogi.dumdum.domain.user;

import com.mxcoogi.dumdum.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 닉네임 */
    @Column(nullable = false)
    private String nickname;

    /** 이메일 (LOCAL 로그인 필수, OAuth는 선택) */
    private String email;

    /** 비밀번호 (LOCAL 로그인만 사용, BCrypt 인코딩) */
    private String password;

    /** 프로필 이미지 URL */
    private String profileImageUrl;

    /** 로그인 방식 (LOCAL, KAKAO, NAVER, GOOGLE, APPLE) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    /** OAuth 공급자의 고유 사용자 ID (OAuth 로그인만 사용) */
    private String providerId;

    /** 권한 (USER, ADMIN) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    /** 노쇼 누적 횟수 */
    @Builder.Default
    @Column(nullable = false)
    private int noShowCount = 0;

    public static User createOAuthUser(String nickname, String email, String profileImageUrl,
                                       LoginType loginType, String providerId) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .loginType(loginType)
                .providerId(providerId)
                .build();
    }

    public static User createLocalUser(String nickname, String email, String encodedPassword) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(encodedPassword)
                .loginType(LoginType.LOCAL)
                .build();
    }

    public void incrementNoShow() {
        this.noShowCount++;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
