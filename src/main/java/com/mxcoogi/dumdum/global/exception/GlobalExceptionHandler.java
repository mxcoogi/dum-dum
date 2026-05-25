package com.mxcoogi.dumdum.global.exception;

import com.mxcoogi.dumdum.global.common.BaseResponse;
import com.mxcoogi.dumdum.global.common.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<Void>> handleApiException(ApiException e) {
        return BaseResponse.fail(e.getResponseCode()).toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null
                ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                : ResponseCode.INVALID_INPUT.getMessage();

        return BaseResponse.fail(ResponseCode.INVALID_INPUT, message).toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return BaseResponse.fail(ResponseCode.INTERNAL_SERVER_ERROR).toResponseEntity();
    }
}
