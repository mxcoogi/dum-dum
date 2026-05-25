package com.mxcoogi.dumdum.store;

import com.mxcoogi.dumdum.global.common.BaseResponse;
import com.mxcoogi.dumdum.global.util.SecurityUtils;
import com.mxcoogi.dumdum.store.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/api/stores")
    public ResponseEntity<BaseResponse<StoreCreateResponse>> registerStore(
            @Valid @RequestBody StoreCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        StoreCreateResponse response = storeService.registerStore(
                userId,
                request.name(),
                request.description(),
                request.latitude(),
                request.longitude(),
                request.address(),
                request.phoneNumber(),
                request.businessRegistrationNumber()
        );
        return BaseResponse.success(response).toResponseEntity();
    }

    @GetMapping("/api/stores/my")
    public ResponseEntity<BaseResponse<List<StoreListResponse>>> getMyStores() {
        Long userId = SecurityUtils.getCurrentUserId();
        return BaseResponse.success(storeService.getMyStores(userId)).toResponseEntity();
    }

    @GetMapping("/api/stores/{storeId}")
    public ResponseEntity<BaseResponse<StoreDetailResponse>> getStore(@PathVariable Long storeId) {
        return BaseResponse.success(storeService.getStore(storeId)).toResponseEntity();
    }

    @PutMapping("/api/stores/{storeId}")
    public ResponseEntity<BaseResponse<StoreDetailResponse>> updateStore(
            @PathVariable Long storeId,
            @Valid @RequestBody StoreUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        StoreDetailResponse response = storeService.updateStore(
                userId,
                storeId,
                request.name(),
                request.description(),
                request.phoneNumber(),
                request.profileImageUrl()
        );
        return BaseResponse.success(response).toResponseEntity();
    }

    @PostMapping("/api/admin/stores/{storeId}/verify")
    public ResponseEntity<BaseResponse<StoreDetailResponse>> verifyStore(@PathVariable Long storeId) {
        return BaseResponse.success(storeService.verifyStore(storeId)).toResponseEntity();
    }

    @PostMapping("/api/admin/stores/{storeId}/reject")
    public ResponseEntity<BaseResponse<StoreDetailResponse>> rejectStore(@PathVariable Long storeId) {
        return BaseResponse.success(storeService.rejectVerifyStore(storeId)).toResponseEntity();
    }
}
