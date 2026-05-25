package com.mxcoogi.dumdum.store.dto;

import com.mxcoogi.dumdum.domain.store.Store;
import com.mxcoogi.dumdum.domain.store.VerificationStatus;

public record StoreDetailResponse(
        Long storeId,
        String name,
        String description,
        String phoneNumber,
        String address,
        /** 위도 */
        Double latitude,
        /** 경도 */
        Double longitude,
        String profileImageUrl,
        VerificationStatus verificationStatus
) {
    public static StoreDetailResponse from(Store store) {
        return new StoreDetailResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getPhoneNumber(),
                store.getAddress(),
                store.getLatitude(),
                store.getLongitude(),
                store.getProfileImageUrl(),
                store.getVerificationStatus()
        );
    }
}
