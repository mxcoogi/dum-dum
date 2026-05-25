package com.mxcoogi.dumdum.auth.dto;

import com.mxcoogi.dumdum.domain.auth.ClientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OAuth2LoginRequest {

    /** 네이티브 SDK에서 발급받은 provider access token (Apple은 ID token) */
    @NotBlank
    private String providerToken;

    @NotNull
    private ClientType clientType;
}
