package com.mxcoogi.dumdum.domain.reservation;

import com.mxcoogi.dumdum.domain.product.Product;
import com.mxcoogi.dumdum.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByProduct(Product product);

    boolean existsByUserAndProductAndStatusNot(User user, Product product, ReservationStatus status);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.status = 'PENDING'
            AND r.pickupDeadline < :now
            """)
    List<Reservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);
}
