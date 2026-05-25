package com.mxcoogi.dumdum.reservation;

import com.mxcoogi.dumdum.domain.product.Product;
import com.mxcoogi.dumdum.domain.product.ProductRepository;
import com.mxcoogi.dumdum.domain.product.ProductStatus;
import com.mxcoogi.dumdum.domain.reservation.Reservation;
import com.mxcoogi.dumdum.domain.reservation.ReservationRepository;
import com.mxcoogi.dumdum.domain.reservation.ReservationStatus;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.domain.user.UserRepository;
import com.mxcoogi.dumdum.global.common.ResponseCode;
import com.mxcoogi.dumdum.global.exception.ApiException;
import com.mxcoogi.dumdum.reservation.dto.ReservationCreateResponse;
import com.mxcoogi.dumdum.reservation.dto.ReservationDetailResponse;
import com.mxcoogi.dumdum.reservation.dto.ReservationListResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 생성
     * @param userId
     * @param productId
     * @param quantity
     * @return
     */
    public ReservationCreateResponse createReservation(Long userId,Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new ApiException(ResponseCode.PRODUCT_NOT_FOUND));
        if(product.getStatus() != ProductStatus.AVAILABLE) {
            throw new ApiException(ResponseCode.PRODUCT_UNAVAILABLE);
        }
        if (reservationRepository.existsByUserAndProductAndStatus(user, product, ReservationStatus.PENDING)) {
            throw new ApiException(ResponseCode.DUPLICATE_RESERVATION);
        }

        try {
            product.reserve(quantity);
        } catch (IllegalStateException e) {
            throw new ApiException(ResponseCode.OUT_OF_STOCK);
        }

        Reservation reservation = Reservation.create(user, product, quantity);
        reservationRepository.save(reservation);
        return ReservationCreateResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationListResponse> getMyReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        return reservationRepository.findByUser(user)
                .stream()
                .map(ReservationListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ResponseCode.RESERVATION_NOT_FOUND));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException(ResponseCode.RESERVATION_ACCESS_DENIED);
        }
        return ReservationDetailResponse.from(reservation);
    }

    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ResponseCode.RESERVATION_NOT_FOUND));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException(ResponseCode.RESERVATION_ACCESS_DENIED);
        }
        if (!reservation.isCancellable()) {
            throw new ApiException(ResponseCode.CANNOT_CANCEL);
        }
        reservation.cancel();
        // 취소된 수량만큼 재고 복구
        reservation.getProduct().cancelReservation(reservation.getQuantity());
    }
}
