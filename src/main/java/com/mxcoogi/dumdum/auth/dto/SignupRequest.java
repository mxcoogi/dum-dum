package com.mxcoogi.dumdum.auth.dto;

import com.mxcoogi.dumdum.domain.auth.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotNull
    private ClientType clientType;
}
