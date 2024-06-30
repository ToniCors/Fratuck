package com.aruba.ApiGateway.controller;

import com.aruba.ApiGateway.component.ApiGatewayLogger;
import com.aruba.ApiGateway.dto.LoginReqDto;
import com.aruba.ApiGateway.dto.TokenDto;
import com.aruba.ApiGateway.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/loginApiGW")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto req) {

        ApiGatewayLogger.logger.info("try loggin...");

        TokenDto res = authService.getAccessToken(req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
