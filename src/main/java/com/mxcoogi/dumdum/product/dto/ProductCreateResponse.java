package com.mxcoogi.dumdum.product.dto;

import com.mxcoogi.dumdum.domain.product.Product;

public record ProductCreateResponse(
        Long productId
) {
    public static ProductCreateResponse from(Product product) {
        return new ProductCreateResponse(product.getId());
    }
}
