package com.mxcoogi.dumdum.store.dto;

import com.mxcoogi.dumdum.domain.store.Store;
import com.mxcoogi.dumdum.domain.store.VerificationStatus;

public record StoreListResponse(
        Long storeId,
        String name,
        String address,
        String imageUrl,
        VerificationStatus verificationStatus
) {
    public static StoreListResponse from(Store store) {
        return new StoreListResponse(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getProfileImageUrl(),
                store.getVerificationStatus()
        );
    }
}
