package com.mxcoogi.dumdum.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(
        @NotNull Long productId,
        // 최소 1개 이상 예약해야 하며, @Min(1)으로 강제된다.
        @NotNull @Min(1) Integer quantity
) {}
