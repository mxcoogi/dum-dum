package com.mxcoogi.dumdum.product.dto;

import com.mxcoogi.dumdum.domain.product.Product;
import com.mxcoogi.dumdum.domain.product.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResponse(
        Long productId,
        Long storeId,
        String storeName,
        String name,
        String description,
        int originalPrice,
        int discountedPrice,
        int totalQuantity,
        /** 현재 예약 가능한 잔여 수량 */
        int remainingQuantity,
        LocalDateTime pickupDeadline,
        /** AVAILABLE | SOLD_OUT | EXPIRED | CANCELLED */
        ProductStatus status,
        /** displayOrder 오름차순 정렬 */
        List<ImageInfo> images
) {
    public record ImageInfo(String imageUrl, int displayOrder) {}

    public static ProductDetailResponse from(Product product) {
        List<ImageInfo> imageInfos = product.getImages().stream()
                .map(img -> new ImageInfo(img.getImageUrl(), img.getDisplayOrder()))
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getStore().getId(),
                product.getStore().getName(),
                product.getName(),
                product.getDescription(),
                product.getOriginalPrice(),
                product.getDiscountedPrice(),
                product.getTotalQuantity(),
                product.getRemainingQuantity(),
                product.getPickupDeadline(),
                product.getStatus(),
                imageInfos
        );
    }
}
