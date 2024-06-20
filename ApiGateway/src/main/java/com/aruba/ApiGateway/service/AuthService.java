package com.aruba.ApiGateway.service;

import com.aruba.ApiGateway.dto.LoginReqDto;
import com.aruba.ApiGateway.dto.TokenDto;

public interface AuthService {
    TokenDto getAccessToken(LoginReqDto request);
    TokenDto getRefreshToken(String refreshToken);
}
