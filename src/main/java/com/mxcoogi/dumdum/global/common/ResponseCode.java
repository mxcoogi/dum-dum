package com.mxcoogi.dumdum.global.common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 성공
    SUCCESS(HttpStatus.OK, "SUCCESS", "성공"),

    // 인증/인가
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다"),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다"),

    // 가게
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "존재하지 않는 가게입니다"),
    STORE_NOT_VERIFIED(HttpStatus.FORBIDDEN, "STORE_NOT_VERIFIED", "인증되지 않은 가게입니다"),
    STORE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "STORE_ACCESS_DENIED", "해당 가게에 대한 권한이 없습니다"),
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "DUPLICATE_BUSINESS_NUMBER", "이미 등록된 사업자등록번호입니다"),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다"),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "OUT_OF_STOCK", "재고가 부족합니다"),
    PRODUCT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "PRODUCT_UNAVAILABLE", "예약 불가능한 상품입니다"),

    // 예약
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "존재하지 않는 예약입니다"),
    DUPLICATE_RESERVATION(HttpStatus.CONFLICT, "DUPLICATE_RESERVATION", "이미 예약한 상품입니다"),
    CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "CANNOT_CANCEL", "취소할 수 없는 예약입니다"),
    RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "RESERVATION_ACCESS_DENIED", "해당 예약에 대한 권한이 없습니다"),

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력값이 올바르지 않습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    @JsonValue
    private final String code;
    private final String message;
}
