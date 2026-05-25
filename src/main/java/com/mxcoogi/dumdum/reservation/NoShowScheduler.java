package com.mxcoogi.dumdum.reservation;

import com.mxcoogi.dumdum.domain.reservation.Reservation;
import com.mxcoogi.dumdum.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoShowScheduler {

    private final ReservationRepository reservationRepository;

    /** 5분마다 실행 — pickupDeadline 지난 PENDING 예약을 NO_SHOW 처리 */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void processNoShows() {
        List<Reservation> expired = reservationRepository.findExpiredPendingReservations(LocalDateTime.now());

        if (expired.isEmpty()) return;

        log.info("[NoShowScheduler] 노쇼 처리 대상: {}건", expired.size());

        for (Reservation reservation : expired) {
            reservation.markNoShow();
            reservation.getUser().incrementNoShow();
            // 노쇼 수량만큼 재고 복구
            reservation.getProduct().cancelReservation(reservation.getQuantity());
        }

        log.info("[NoShowScheduler] 노쇼 처리 완료: {}건", expired.size());
    }
}
