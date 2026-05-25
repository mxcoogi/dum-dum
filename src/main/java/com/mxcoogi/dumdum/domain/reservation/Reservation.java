package com.mxcoogi.dumdum.domain.reservation;

import com.mxcoogi.dumdum.domain.product.Product;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 예약한 유저 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 예약한 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 예약 수량 */
    @Column(nullable = false)
    private int quantity;

    /** 픽업 마감 시각 (예약 시점에 Product에서 복사) */
    @Column(nullable = false)
    private LocalDateTime pickupDeadline;

    /** 예약 상태 (PENDING, COMPLETED, CANCELLED, NO_SHOW) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    /** 방문 완료 시각 */
    private LocalDateTime completedAt;

    /** 취소 또는 노쇼 처리 시각 */
    private LocalDateTime cancelledAt;

    public static Reservation create(User user, Product product, int quantity) {
        return Reservation.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .pickupDeadline(product.getPickupDeadline())
                .build();
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markNoShow() {
        this.status = ReservationStatus.NO_SHOW;
        this.cancelledAt = LocalDateTime.now();
    }

    public boolean isCancellable() {
        return this.status == ReservationStatus.PENDING;
    }
}
