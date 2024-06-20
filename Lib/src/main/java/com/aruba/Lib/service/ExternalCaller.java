package com.aruba.Lib.service;

import com.aruba.Lib.logging.logger.MsLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalCaller {

    private final RestTemplate restTemplate;

    public ExternalCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
        MsLogger.logger.info("external call to : {}", url);
        ResponseEntity<T> res = restTemplate.exchange(url,method,requestEntity, responseType, uriVariables);
        MsLogger.logger.info("Response status code: {}", res.getStatusCode());

        return res;
    }

    public <T> T callGET(String url, Class<T> responseType) {
        MsLogger.logger.info("external call to : {}", url);
        return restTemplate.getForObject(url, responseType);
    }

    public <T> T callPOST(String url, Object body, Class<T> responseType) {
        MsLogger.logger.info("external call to : {}", url);
        return restTemplate.postForObject(url, body, responseType);
    }

}
