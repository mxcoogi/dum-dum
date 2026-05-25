package com.mxcoogi.dumdum.store;

import com.mxcoogi.dumdum.domain.store.Store;
import com.mxcoogi.dumdum.domain.store.StoreRepository;
import com.mxcoogi.dumdum.domain.user.User;
import com.mxcoogi.dumdum.domain.user.UserRepository;
import com.mxcoogi.dumdum.global.common.ResponseCode;
import com.mxcoogi.dumdum.global.exception.ApiException;
import com.mxcoogi.dumdum.store.dto.StoreDetailResponse;
import com.mxcoogi.dumdum.store.dto.StoreListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    /**
     * 가게 등록
     * @param userId
     * @param name
     * @param description
     * @param latitude
     * @param longitude
     * @param address
     * @param phoneNumber
     * @param businessRegistrationNumber
     * @return
     */
    public StoreDetailResponse registerStore(Long userId, String name, String description,
                                             Double latitude, Double longitude,
                                             String address, String phoneNumber, String businessRegistrationNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        if(storeRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber)){
            throw new ApiException(ResponseCode.DUPLICATE_BUSINESS_NUMBER);
        }
        Store store = Store.create(user, name, description, phoneNumber,
                address, latitude, longitude, businessRegistrationNumber);
        storeRepository.save(store);
        return StoreDetailResponse.from(store);
    }

    /**
     * 내 가게 목록 조회
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<StoreListResponse> getMyStores(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        List<StoreListResponse> list = storeRepository.findByUser(user)
                .stream()
                .map(StoreListResponse::from)
                .toList();
        return list;
    }

    /**
     * 가게 상세 조회
     * @param storeId
     * @return
     */
    @Transactional(readOnly = true)
    public StoreDetailResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException(ResponseCode.STORE_NOT_FOUND));
        return StoreDetailResponse.from(store);
    }

    /**
     * 가게 정보 수정
     * @param userId
     * @param storeId
     * @param name
     * @param description
     * @param phoneNumber
     * @param profileImageUrl
     * @return
     */
    public StoreDetailResponse updateStore(Long userId, Long storeId, String name, String description, String phoneNumber, String profileImageUrl){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ResponseCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException(ResponseCode.STORE_NOT_FOUND));
        if(!Objects.equals(user.getId(), store.getUser().getId())){
            throw new ApiException(ResponseCode.FORBIDDEN);
        }
        store.updateInfo(name, description, phoneNumber, profileImageUrl);
        storeRepository.save(store);
        return StoreDetailResponse.from(store);
    }

    /**
     * 가게 인증
     * ADMIN 전용
     * @param storeId
     * @return
     */
    public StoreDetailResponse verifyStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException(ResponseCode.STORE_NOT_FOUND));
        store.verify();
        return StoreDetailResponse.from(store);
    }
    /**
     * 가게 인증 거부
     * ADMIN 전용
     * @param storeId
     * @return
     */
    public StoreDetailResponse rejectVerifyStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException(ResponseCode.STORE_NOT_FOUND));
        store.reject();
        return StoreDetailResponse.from(store);
    }
}
