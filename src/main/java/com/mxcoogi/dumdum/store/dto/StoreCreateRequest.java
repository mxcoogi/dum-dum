package com.mxcoogi.dumdum.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreCreateRequest(
        @NotBlank String name,
        String description,
        String phoneNumber,
        @NotBlank String address,
        /** 위도 — 반경 검색에 사용 */
        @NotNull Double latitude,
        /** 경도 — 반경 검색에 사용 */
        @NotNull Double longitude,
        /** 사업자등록번호 — 중복 등록 불가 */
        @NotBlank String businessRegistrationNumber
) {}
