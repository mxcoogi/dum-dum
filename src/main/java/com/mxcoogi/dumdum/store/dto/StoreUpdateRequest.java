package com.mxcoogi.dumdum.store.dto;

import jakarta.validation.constraints.NotBlank;

public record StoreUpdateRequest(
        @NotBlank String name,
        String description,
        String phoneNumber,
        /** 이미지 업로드 후 받은 URL을 전달 */
        String profileImageUrl
) {}
