package com.mxcoogi.dumdum.reservation;

import com.mxcoogi.dumdum.global.common.BaseResponse;
import com.mxcoogi.dumdum.global.util.SecurityUtils;
import com.mxcoogi.dumdum.reservation.dto.ReservationCreateRequest;
import com.mxcoogi.dumdum.reservation.dto.ReservationCreateResponse;
import com.mxcoogi.dumdum.reservation.dto.ReservationDetailResponse;
import com.mxcoogi.dumdum.reservation.dto.ReservationListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<BaseResponse<ReservationCreateResponse>> createReservation(
            @Valid @RequestBody ReservationCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return BaseResponse.success(
                reservationService.createReservation(userId, request.productId(), request.quantity())
        ).toResponseEntity();
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ReservationListResponse>>> getMyReservations() {
        Long userId = SecurityUtils.getCurrentUserId();
        return BaseResponse.success(reservationService.getMyReservations(userId)).toResponseEntity();
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<BaseResponse<ReservationDetailResponse>> getReservation(
            @PathVariable Long reservationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return BaseResponse.success(reservationService.getReservation(userId, reservationId)).toResponseEntity();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<BaseResponse<Void>> cancelReservation(@PathVariable Long reservationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        reservationService.cancelReservation(userId, reservationId);
        return BaseResponse.success().toResponseEntity();
    }

    @PostMapping("/{reservationId}/complete")
    public ResponseEntity<BaseResponse<Void>> completeReservation(@PathVariable Long reservationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        reservationService.completeReservation(userId, reservationId);
        return BaseResponse.success().toResponseEntity();
    }
}
