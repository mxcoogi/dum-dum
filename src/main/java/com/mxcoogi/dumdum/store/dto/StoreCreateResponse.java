package com.mxcoogi.dumdum.store.dto;

import com.mxcoogi.dumdum.domain.store.Store;
import com.mxcoogi.dumdum.domain.store.VerificationStatus;

public record StoreCreateResponse(
        Long storeId,
        String name,
        /** 등록 직후 항상 PENDING — 어드민 승인 후 VERIFIED */
        VerificationStatus verificationStatus
) {
    public static StoreCreateResponse from(Store store) {
        return new StoreCreateResponse(store.getId(), store.getName(), store.getVerificationStatus());
    }
}
