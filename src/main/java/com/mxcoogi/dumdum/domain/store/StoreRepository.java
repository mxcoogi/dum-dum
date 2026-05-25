package com.mxcoogi.dumdum.domain.store;

import com.mxcoogi.dumdum.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByUser(User user);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    @Query("""
            SELECT s FROM Store s
            WHERE s.verificationStatus = 'VERIFIED'
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude))
                * cos(radians(s.longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(s.latitude)))) <= :radiusKm
            """)
    List<Store> findVerifiedStoresWithinRadius(@Param("lat") double lat,
                                               @Param("lng") double lng,
                                               @Param("radiusKm") double radiusKm);
}
