package com.mxcoogi.dumdum.reservation;

import com.mxcoogi.dumdum.domain.product.Product;
import com.mxcoogi.dumdum.domain.product.ProductRepository;
import com.mxcoogi.dumdum.domain.reservation.Reservation;
import com.mxcoogi.dumdum.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class NoShowScheduler {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    /** 5분마다 실행 — pickupDeadline 지난 PENDING 예약을 NO_SHOW 처리 */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void processNoShows() {
        List<Reservation> expired = reservationRepository.findExpiredPendingReservations(LocalDateTime.now());

        if (expired.isEmpty()) return;

        log.info("[NoShowScheduler] 노쇼 처리 대상: {}건", expired.size());

        for (Reservation reservation : expired) {
            Long productId = reservation.getProduct().getId();
            Optional<Product> productOpt = productRepository.findByIdWithLock(productId);
            if (productOpt.isEmpty()) {
                log.error("[NoShowScheduler] 상품 없음, 건너뜀: reservationId={}, productId={}", reservation.getId(), productId);
                continue;
            }
            reservation.markNoShow();
            reservation.getUser().incrementNoShow();
            productOpt.get().cancelReservation(reservation.getQuantity());
        }

        log.info("[NoShowScheduler] 노쇼 처리 완료: {}건", expired.size());
    }
}
