package com.mxcoogi.dumdum.domain.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 이미지가 속한 상품 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** 이미지 URL */
    @Column(nullable = false)
    private String imageUrl;

    /** 이미지 노출 순서 */
    @Column(nullable = false)
    private int displayOrder;

    public static ProductImage create(Product product, String imageUrl, int displayOrder) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .build();
    }
}
