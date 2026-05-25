package com.mxcoogi.dumdum.global.exception;

import com.mxcoogi.dumdum.global.common.ResponseCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ResponseCode responseCode;

    public ApiException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}
