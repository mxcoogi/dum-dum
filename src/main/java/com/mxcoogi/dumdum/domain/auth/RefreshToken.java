package com.mxcoogi.dumdum.domain.auth;

import com.mxcoogi.dumdum.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "client_type"}))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 토큰 소유 유저 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 클라이언트 타입 (WEB, MOBILE) */
    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    /** refresh token 값 */
    @Column(nullable = false, unique = true)
    private String token;

    /** 만료 시각 */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public static RefreshToken create(User user, ClientType clientType, String token, LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .user(user)
                .clientType(clientType)
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }

    public void rotate(String newToken, LocalDateTime expiresAt) {
        this.token = newToken;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
