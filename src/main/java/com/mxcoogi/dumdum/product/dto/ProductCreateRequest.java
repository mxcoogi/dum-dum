package com.mxcoogi.dumdum.product.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ProductCreateRequest(
        @NotBlank String name,
        String description,
        @NotNull @Positive int originalPrice,
        @NotNull @Positive int discountedPrice,
        /** 총 등록 수량 */
        @NotNull @Positive int quantity,
        /** 픽업 마감 시각 — 현재 시각 이후여야 함 */
        @NotNull @Future LocalDateTime pickupDeadline
) {}
