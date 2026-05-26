package com.mxcoogi.dumdum.reservation.dto;

import com.mxcoogi.dumdum.domain.reservation.Reservation;
import com.mxcoogi.dumdum.domain.reservation.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationListResponse(
        Long reservationId,
        String storeName,
        String productName,
        int quantity,
        LocalDateTime pickupDeadline,
        ReservationStatus status
) {
    public static ReservationListResponse from(Reservation reservation) {
        return new ReservationListResponse(
                reservation.getId(),
                reservation.getProduct().getStore().getName(),
                reservation.getProduct().getName(),
                reservation.getQuantity(),
                reservation.getPickupDeadline(),
                reservation.getStatus()
        );
    }
}
