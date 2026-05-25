package com.mxcoogi.dumdum.domain.product;

import com.mxcoogi.dumdum.domain.store.Store;
import com.mxcoogi.dumdum.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 상품을 등록한 가게 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    /** 상품명 */
    @Column(nullable = false)
    private String name;

    /** 상품 설명 */
    private String description;

    /** 원가 */
    @Column(nullable = false)
    private int originalPrice;

    /** 할인가 (마감 세일가) */
    @Column(nullable = false)
    private int discountedPrice;

    /** 총 등록 수량 */
    @Column(nullable = false)
    private int totalQuantity;

    /** 예약 가능 잔여 수량 */
    @Column(nullable = false)
    private int remainingQuantity;

    /** 픽업 마감 시각 */
    @Column(nullable = false)
    private LocalDateTime pickupDeadline;

    /** 상품 상태 (AVAILABLE, SOLD_OUT, EXPIRED, CANCELLED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.AVAILABLE;

    /** 상품 이미지 목록 */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    public static Product create(Store store, String name, String description,
                                 int originalPrice, int discountedPrice,
                                 int quantity, LocalDateTime pickupDeadline) {
        return Product.builder()
                .store(store)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountedPrice(discountedPrice)
                .totalQuantity(quantity)
                .remainingQuantity(quantity)
                .pickupDeadline(pickupDeadline)
                .build();
    }

    public void reserve(int count) {
        if (this.remainingQuantity < count) {
            throw new IllegalStateException("재고 부족");
        }
        this.remainingQuantity -= count;
        if (this.remainingQuantity == 0) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public void cancelReservation(int count) {
        this.remainingQuantity += count;
        if (this.status == ProductStatus.SOLD_OUT) {
            this.status = ProductStatus.AVAILABLE;
        }
    }

    public void expire() {
        this.status = ProductStatus.EXPIRED;
    }

    public void cancel() {
        this.status = ProductStatus.CANCELLED;
    }

    public void addImage(String imageUrl, int displayOrder) {
        this.images.add(ProductImage.create(this, imageUrl, displayOrder));
    }
}
