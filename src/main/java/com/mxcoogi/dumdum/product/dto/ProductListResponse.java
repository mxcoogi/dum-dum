package com.mxcoogi.dumdum.product.dto;

import com.mxcoogi.dumdum.domain.product.Product;

import java.time.LocalDateTime;

public record ProductListResponse(
        Long productId,
        String storeName,
        String name,
        int originalPrice,
        int discountedPrice,
        int remainingQuantity,
        LocalDateTime pickupDeadline,
        /** 대표 이미지 — 없으면 null */
        String thumbnailImageUrl,
        /** 요청 위치 기준 거리 (km) */
        double distance
) {
    public static ProductListResponse from(Product product, double distance) {
        String thumbnail = product.getImages().isEmpty()
                ? null
                : product.getImages().get(0).getImageUrl();

        return new ProductListResponse(
                product.getId(),
                product.getStore().getName(),
                product.getName(),
                product.getOriginalPrice(),
                product.getDiscountedPrice(),
                product.getRemainingQuantity(),
                product.getPickupDeadline(),
                thumbnail,
                distance
        );
    }
}
