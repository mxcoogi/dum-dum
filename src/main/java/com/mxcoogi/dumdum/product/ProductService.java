package com.mxcoogi.dumdum.product;

import com.mxcoogi.dumdum.domain.product.Product;
import com.mxcoogi.dumdum.domain.product.ProductRepository;
import com.mxcoogi.dumdum.domain.product.ProductStatus;
import com.mxcoogi.dumdum.domain.store.Store;
import com.mxcoogi.dumdum.domain.store.StoreRepository;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.domain.user.UserRepository;
import com.mxcoogi.dumdum.global.common.ResponseCode;
import com.mxcoogi.dumdum.global.exception.ApiException;
import com.mxcoogi.dumdum.global.storage.StorageService;
import com.mxcoogi.dumdum.product.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    /** 근처 마감 상품 목록 — 거리(km)는 Haversine으로 직접 계산 */
    @Transactional(readOnly = true)
    public List<ProductListResponse> getProductsNearby(double lat, double lng, double radius) {
        return productRepository.findAvailableProductsWithinRadius(lat, lng, radius)
                .stream()
                .map(product -> {
                    double distance = calculateDistance(
                            lat, lng,
                            product.getStore().getLatitude(),
                            product.getStore().getLongitude()
                    );
                    return ProductListResponse.from(product, distance);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(ResponseCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponse.from(product);
    }

    /** 가게 소유자 + VERIFIED 상태 모두 통과해야 등록 가능 */
    public ProductCreateResponse createProduct(Long userId, Long storeId, ProductCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException(ResponseCode.STORE_NOT_FOUND));

        if (!store.getUser().getId().equals(user.getId())) {
            throw new ApiException(ResponseCode.STORE_ACCESS_DENIED);
        }
        if (!store.isVerified()) {
            throw new ApiException(ResponseCode.STORE_NOT_VERIFIED);
        }

        Product product = Product.create(
                store,
                request.name(),
                request.description(),
                request.originalPrice(),
                request.discountedPrice(),
                request.quantity(),
                request.pickupDeadline()
        );
        productRepository.save(product);
        return ProductCreateResponse.from(product);
    }

    /**
     * 이미지 업로드 — 기존 이미지 뒤에 순서 이어서 추가
     * displayOrder는 기존 이미지 수부터 시작 (덮어쓰지 않음)
     */
    public void uploadImages(Long userId, Long productId, List<MultipartFile> files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(ResponseCode.PRODUCT_NOT_FOUND));

        if (!product.getStore().getUser().getId().equals(userId)) {
            throw new ApiException(ResponseCode.STORE_ACCESS_DENIED);
        }

        if (files.size() > 5) {
            throw new ApiException(ResponseCode.INVALID_INPUT);
        }

        int order = product.getImages().size();
        for (MultipartFile file : files) {
            String url = storageService.upload(file, "products");
            product.addImage(url, order++);
        }
    }

    /** EXPIRED·CANCELLED 상태는 이미 종료된 상품 — 취소 불가 */
    public void cancelProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(ResponseCode.PRODUCT_NOT_FOUND));

        if (!product.getStore().getUser().getId().equals(userId)) {
            throw new ApiException(ResponseCode.STORE_ACCESS_DENIED);
        }

        if (product.getStatus() == ProductStatus.EXPIRED ||
                product.getStatus() == ProductStatus.CANCELLED) {
            throw new ApiException(ResponseCode.PRODUCT_UNAVAILABLE);
        }

        product.cancel();
    }

    /** Haversine formula — 두 좌표 간 거리(km) */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
