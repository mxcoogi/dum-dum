package com.mxcoogi.dumdum.auth.dto;

import com.mxcoogi.dumdum.domain.auth.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private ClientType clientType;
}
