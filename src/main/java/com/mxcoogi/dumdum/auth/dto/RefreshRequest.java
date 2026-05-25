package com.mxcoogi.dumdum.auth.dto;

import com.mxcoogi.dumdum.domain.auth.ClientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefreshRequest {

    @NotBlank
    private String refreshToken;

    @NotNull
    private ClientType clientType;
}
