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

import java.util.List;

@Service
@RequiredArgsConstructor
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
    public StoreDetailResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApiException(ResponseCode.STORE_NOT_FOUND));
        return StoreDetailResponse.from(store);
    }
}
