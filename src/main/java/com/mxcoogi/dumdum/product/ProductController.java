package com.mxcoogi.dumdum.product;

import com.mxcoogi.dumdum.global.common.BaseResponse;
import com.mxcoogi.dumdum.global.util.SecurityUtils;
import com.mxcoogi.dumdum.product.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<BaseResponse<List<ProductListResponse>>> getProductsNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "2.0") double radius) {
        return BaseResponse.success(productService.getProductsNearby(lat, lng, radius)).toResponseEntity();
    }

    @GetMapping("/api/products/{productId}")
    public ResponseEntity<BaseResponse<ProductDetailResponse>> getProduct(@PathVariable Long productId) {
        return BaseResponse.success(productService.getProduct(productId)).toResponseEntity();
    }

    @PostMapping("/api/stores/{storeId}/products")
    public ResponseEntity<BaseResponse<ProductCreateResponse>> createProduct(
            @PathVariable Long storeId,
            @Valid @RequestBody ProductCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return BaseResponse.success(productService.createProduct(userId, storeId, request)).toResponseEntity();
    }

    @PostMapping("/api/products/{productId}/images")
    public ResponseEntity<BaseResponse<Void>> uploadImages(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> files) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.uploadImages(userId, productId, files);
        return BaseResponse.success().toResponseEntity();
    }

    @DeleteMapping("/api/products/{productId}")
    public ResponseEntity<BaseResponse<Void>> cancelProduct(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.cancelProduct(userId, productId);
        return BaseResponse.success().toResponseEntity();
    }
}
