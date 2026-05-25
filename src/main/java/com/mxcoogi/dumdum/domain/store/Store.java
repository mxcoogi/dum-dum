package com.mxcoogi.dumdum.domain.store;

import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stores")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 가게 소유자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 가게명 */
    @Column(nullable = false)
    private String name;

    /** 가게 설명 */
    private String description;

    /** 가게 연락처 */
    private String phoneNumber;

    /** 가게 주소 */
    @Column(nullable = false)
    private String address;

    /** 위도 (반경 검색용) */
    @Column(nullable = false)
    private double latitude;

    /** 경도 (반경 검색용) */
    @Column(nullable = false)
    private double longitude;

    /** 사업자등록번호 */
    @Column(nullable = false, unique = true)
    private String businessRegistrationNumber;

    /** 사업자 인증 상태 (PENDING, VERIFIED, REJECTED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    /** 가게 대표 이미지 URL */
    private String profileImageUrl;

    public static Store create(User user, String name, String description, String phoneNumber,
                               String address, double latitude, double longitude,
                               String businessRegistrationNumber) {
        return Store.builder()
                .user(user)
                .name(name)
                .description(description)
                .phoneNumber(phoneNumber)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .businessRegistrationNumber(businessRegistrationNumber)
                .build();
    }

    public void verify() {
        this.verificationStatus = VerificationStatus.VERIFIED;
    }

    public void reject() {
        this.verificationStatus = VerificationStatus.REJECTED;
    }

    public void updateInfo(String name, String description, String phoneNumber, String profileImageUrl) {
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isVerified() {
        return this.verificationStatus == VerificationStatus.VERIFIED;
    }
}
