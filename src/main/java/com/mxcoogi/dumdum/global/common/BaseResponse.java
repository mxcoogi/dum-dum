package com.mxcoogi.dumdum.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private final ResponseCode code;
    private final String message;
    private final T data;

    private BaseResponse(ResponseCode code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ResponseCode.SUCCESS, ResponseCode.SUCCESS.getMessage(), data);
    }

    public static BaseResponse<Void> success() {
        return new BaseResponse<>(ResponseCode.SUCCESS, ResponseCode.SUCCESS.getMessage(), null);
    }

    public static BaseResponse<Void> fail(ResponseCode responseCode) {
        return new BaseResponse<>(responseCode, responseCode.getMessage(), null);
    }

    public static BaseResponse<Void> fail(ResponseCode responseCode, String message) {
        return new BaseResponse<>(responseCode, message, null);
    }

    public ResponseEntity<BaseResponse<T>> toResponseEntity() {
        return ResponseEntity.status(code.getHttpStatus()).body(this);
    }
}
