package com.mxcoogi.dumdum.auth;

import com.mxcoogi.dumdum.auth.dto.*;
import com.mxcoogi.dumdum.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OAuth2MobileAuthService oAuth2MobileAuthService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return BaseResponse.success(authService.signup(request)).toResponseEntity();
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return BaseResponse.success(authService.login(request)).toResponseEntity();
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return BaseResponse.success(authService.refresh(request.getRefreshToken(), request.getClientType())).toResponseEntity();
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return BaseResponse.success().toResponseEntity();
    }

    /** 모바일 네이티브 SDK OAuth2 로그인 */
    @PostMapping("/oauth2/{provider}")
    public ResponseEntity<BaseResponse<TokenResponse>> oAuth2MobileLogin(
            @PathVariable String provider,
            @Valid @RequestBody OAuth2LoginRequest request) {
        return BaseResponse.success(
                oAuth2MobileAuthService.login(provider, request.getProviderToken(), request.getClientType())
        ).toResponseEntity();
    }
}
