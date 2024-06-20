package com.aruba.ApiGateway.service.impl;


import com.aruba.ApiGateway.dto.LoginReqDto;
import com.aruba.ApiGateway.dto.TokenDto;
import com.aruba.ApiGateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}")
    private String GRANT_TYPE_PASSWORD;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String kcClientId;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String kcClientSecret;
    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String kcGetTokenUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public TokenDto getAccessToken(LoginReqDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", GRANT_TYPE_PASSWORD);
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);
        requestBody.add("username", request.getUsername());
        requestBody.add("password", request.getPassword());

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl.concat("/protocol/openid-connect/token"), new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }

    @Override
    public TokenDto getRefreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl, new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }

}
