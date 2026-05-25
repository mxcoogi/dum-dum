package com.mxcoogi.dumdum.product.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ProductCreateRequest(
        @NotBlank String name,
        String description,
        /** 원가 */
        @NotNull @Positive int originalPrice,
        /** 마감 할인가 — 원가보다 낮아야 함 (검증은 서비스 레벨에서 추가 가능) */
        @NotNull @Positive int discountedPrice,
        /** 총 등록 수량 — remainingQuantity 초기값으로 사용 */
        @NotNull @Positive int quantity,
        /** 픽업 마감 시각 — 현재 시각 이후여야 함 */
        @NotNull @Future LocalDateTime pickupDeadline
) {}
