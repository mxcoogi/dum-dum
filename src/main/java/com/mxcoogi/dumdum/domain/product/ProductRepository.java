package com.mxcoogi.dumdum.domain.product;

import com.mxcoogi.dumdum.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStoreAndStatus(Store store, ProductStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @Query("""
            SELECT p FROM Product p
            JOIN p.store s
            WHERE s.verificationStatus = 'VERIFIED'
            AND p.status = 'AVAILABLE'
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude))
                * cos(radians(s.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(s.latitude)))) <= :radiusKm
            ORDER BY p.pickupDeadline ASC
            """)
    List<Product> findAvailableProductsWithinRadius(@Param("lat") double lat,
                                                    @Param("lng") double lng,
                                                    @Param("radiusKm") double radiusKm);
}
