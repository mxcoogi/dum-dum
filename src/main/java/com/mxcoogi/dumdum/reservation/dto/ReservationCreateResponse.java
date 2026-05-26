package com.mxcoogi.dumdum.reservation.dto;

import com.mxcoogi.dumdum.domain.reservation.Reservation;
import com.mxcoogi.dumdum.domain.reservation.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationCreateResponse(
        Long reservationId,
        String productName,
        int quantity,
        /** 예약 시점에 Product에서 복사된 값. 이후 상품 정보가 변경되어도 영향받지 않는다. */
        LocalDateTime pickupDeadline,
        /** 생성 직후 항상 PENDING 상태로 고정된다. */
        ReservationStatus status
) {
    public static ReservationCreateResponse from(Reservation reservation) {
        return new ReservationCreateResponse(
                reservation.getId(),
                reservation.getProduct().getName(),
                reservation.getQuantity(),
                reservation.getPickupDeadline(),
                reservation.getStatus()
        );
    }
}
