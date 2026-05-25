package com.mxcoogi.dumdum.reservation.dto;

import com.mxcoogi.dumdum.domain.reservation.Reservation;
import com.mxcoogi.dumdum.domain.reservation.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationDetailResponse(
        Long reservationId,
        Long storeId,
        String storeName,
        String storeAddress,
        Long productId,
        String productName,
        int quantity,
        /** 예약 시점에 Product에서 복사된 값. 이후 상품 변경과 무관하게 원본 마감 시각을 유지한다. */
        LocalDateTime pickupDeadline,
        ReservationStatus status,
        /** 예약 생성 시각. */
        LocalDateTime createdAt
) {
    public static ReservationDetailResponse from(Reservation reservation) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getProduct().getStore().getId(),
                reservation.getProduct().getStore().getName(),
                reservation.getProduct().getStore().getAddress(),
                reservation.getProduct().getId(),
                reservation.getProduct().getName(),
                reservation.getQuantity(),
                reservation.getPickupDeadline(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
