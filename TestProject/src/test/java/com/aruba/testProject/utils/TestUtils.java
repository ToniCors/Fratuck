package com.aruba.testProject.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class TestUtils {

    public static final String host = "http://localhost:8999";
    private static final RestTemplate restTemplate = new RestTemplate();
    public static User ladyMarian = new User("ladymarian", "password");
    public static User robin = new User("robin", "password");
    public static User littleJohn = new User("littlejohn", "password");
    public static User nottingham = new User("nottingham", "password");

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }


    private String getUserToken(boolean mock, User u) {
        if (!mock) {
            return callForLogin(u);
        } else
            return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDMThOcjh2S1J6cy1TNG5SanY2X1IwM1RrSWh5QzlxWjFoLVNvN1JPb1BNIn0.eyJleHAiOjE3MTg2OTg2MTIsImlhdCI6MTcxODY5ODMxMiwianRpIjoiNTVhNGU0OGUtMmYxYS00MzE2LTk0NDUtMmEzNDg2ZGYyMmRiIiwiaXNzIjoiaHR0cDovL2tjc2VydmVyOjgwODAvcmVhbG1zL2ZyYXR1Y2siLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiY2U0Y2RmYjAtZjQ5Yi00OGRmLTgzZjYtMWViZDNkZjQ4OTg3IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZnJhdHVjay1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiYWI2ZGEwMTgtZjdjZS00NGQzLTk2NWMtNWNiNDgxMTkzMzA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1mcmF0dWNrIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImFiNmRhMDE4LWY3Y2UtNDRkMy05NjVjLTVjYjQ4MTE5MzMwNSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoibGFkeSBtYXJpYW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsYWR5bWFyaWFuIiwiZ2l2ZW5fbmFtZSI6ImxhZHkiLCJmYW1pbHlfbmFtZSI6Im1hcmlhbiIsImVtYWlsIjoibGFkeW1hcmlhbkB0ZXN0LmNvbSJ9.jcOkrsNIi1MA8nda2IXVQfaf7yj_OXQ6Wji98HhbpANsBfMRe-XyPsX1Uy0-KY2kH2eCrDxMNymayUGFHRIZl7MTISa3Mm0jPbzNfLvUPFBUzXzzboCFRDnj9O250wxnKJ31w8yyJMzB7ipYHmyFlZOp1j5lGpZLK1qb0E1Bhg7yxyNp5Yj6KdvAFXVVHyNV1QUsgSLZdB57usMyD9PS7HVEqt-cR4b5ccDZyQhNb4hoejtUtO1B4bqN3RqTL_rwGP7H8PwN2pgzh-oB1bpx9AXuL-M4QG9kuoavnuzj6HS455R5SShd4Fd_mESYum33lGMg0-Wni-EkmZPYmN1Orw";
    }

    public static String callForLogin(User u) {
        HashMap<String, String> update = new HashMap<>();
        update.put("username", u.getU());
        update.put("password", u.getP());

        HttpEntity<?> entity = new HttpEntity<>(update);
        ResponseEntity<JsonNode> res;
        res = restTemplate.exchange(host + "/loginApiGW", HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);

        System.out.println("token" + res.getBody().toPrettyString());
        Assertions.assertTrue(res.getBody().has("access_token"));

        return res.getBody().get("access_token").asText();
    }

    public static class User {
        String u;
        String p;

        public User(String u, String p) {
            this.u = u;
            this.p = p;
        }

        public String getU() {
            return u;
        }

        public String getP() {
            return p;
        }
    }

}

