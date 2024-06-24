package com.aruba.Orchestrator.scenarios;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@SpringBootTest
public class Fault {

    private static final String host = "http://Orchestrator";

    private static HttpHeaders headerHttp;
    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() throws Exception {
        headerHttp = new HttpHeaders();
        headerHttp.setContentType(MediaType.APPLICATION_JSON);
        headerHttp.add("Authorization", getLadyMarianToken());
    }

    @Test
    public void erroreAggiornamentoMagazzino() {


        HttpClientErrorException e = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            HashMap<String, String> update = new HashMap<>();
            update.put("id", "99999999");
            update.put("quantity", "10");
            update.put("operation", "0");
            HttpEntity<?> entity = new HttpEntity<>(update, headerHttp);
            ResponseEntity<JsonNode> res;
            System.out.println("errore Aggiornamento Magazzino");
            restTemplate.exchange(host + "/orchestrator/inventory/warehouse/refreshStock", HttpMethod.POST, entity, JsonNode.class);
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        Assertions.assertTrue(e.getMessage().contains("Warehouse with id {99999999} was not found."));
        System.out.println(e.getMessage());

    }

    @Test
    public void erroreCreazioneOrdine() {

        HttpClientErrorException e = Assertions.assertThrows(HttpClientErrorException.class, () -> {
            HttpEntity<String> entity = new HttpEntity<>(headerHttp);
            ResponseEntity<JsonNode> res;
            System.out.println("errore creazione ordine");
            restTemplate.exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        Assertions.assertTrue(e.getMessage().contains("Impossible create order, Cart is empty"));
        System.out.println(e.getMessage());
    }

    @Test
    public void errorePagamentoGiacenzaMancante() {

        HttpClientErrorException e = Assertions.assertThrows(HttpClientErrorException.class, () -> {

            String productId = "2";

            modifyStock(productId, "100", "0");

            addProductToCart(productId);
            HttpEntity<String> entity = new HttpEntity<>(headerHttp);
            ResponseEntity<JsonNode> res;
            System.out.println("Create Order");
            res = restTemplate.exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
            Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
            Assertions.assertNotNull(res.getBody());
            System.out.println("New Order: " + res.getBody().toPrettyString());

//            String orderID = res.getBody().get("id").asText();
            modifyStock(productId, "10000", "1");
            String paymentID = res.getBody().get("payment").get("id").asText();
            res = restTemplate.exchange(host + "/orchestrator/payment/payments/pay/" + paymentID, HttpMethod.POST, entity, JsonNode.class);
            Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals("SUCCESSFUL", res.getBody().get("status").asText());
            System.out.println("Payment SUCCESSFUL: " + res.getBody().toPrettyString());
            modifyStock(productId, "100", "0");

        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        Assertions.assertTrue(e.getMessage().contains("Enable to make payment. One or more product is out of stock"));
        System.out.println(e.getMessage());
    }

    private String getLadyMarianToken() {
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDMThOcjh2S1J6cy1TNG5SanY2X1IwM1RrSWh5QzlxWjFoLVNvN1JPb1BNIn0.eyJleHAiOjE3MTg2OTg2MTIsImlhdCI6MTcxODY5ODMxMiwianRpIjoiNTVhNGU0OGUtMmYxYS00MzE2LTk0NDUtMmEzNDg2ZGYyMmRiIiwiaXNzIjoiaHR0cDovL2tjc2VydmVyOjgwODAvcmVhbG1zL2ZyYXR1Y2siLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiY2U0Y2RmYjAtZjQ5Yi00OGRmLTgzZjYtMWViZDNkZjQ4OTg3IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZnJhdHVjay1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiYWI2ZGEwMTgtZjdjZS00NGQzLTk2NWMtNWNiNDgxMTkzMzA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1mcmF0dWNrIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImFiNmRhMDE4LWY3Y2UtNDRkMy05NjVjLTVjYjQ4MTE5MzMwNSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoibGFkeSBtYXJpYW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsYWR5bWFyaWFuIiwiZ2l2ZW5fbmFtZSI6ImxhZHkiLCJmYW1pbHlfbmFtZSI6Im1hcmlhbiIsImVtYWlsIjoibGFkeW1hcmlhbkB0ZXN0LmNvbSJ9.jcOkrsNIi1MA8nda2IXVQfaf7yj_OXQ6Wji98HhbpANsBfMRe-XyPsX1Uy0-KY2kH2eCrDxMNymayUGFHRIZl7MTISa3Mm0jPbzNfLvUPFBUzXzzboCFRDnj9O250wxnKJ31w8yyJMzB7ipYHmyFlZOp1j5lGpZLK1qb0E1Bhg7yxyNp5Yj6KdvAFXVVHyNV1QUsgSLZdB57usMyD9PS7HVEqt-cR4b5ccDZyQhNb4hoejtUtO1B4bqN3RqTL_rwGP7H8PwN2pgzh-oB1bpx9AXuL-M4QG9kuoavnuzj6HS455R5SShd4Fd_mESYum33lGMg0-Wni-EkmZPYmN1Orw";
    }

    private void addProductToCart(String productId) {

        HashMap<String, String> req = new HashMap<>();

        req.put("product_id", productId);
        req.put("quantity", "10");

        HttpEntity<?> entity = new HttpEntity<>(req, headerHttp);
        System.out.println("add To Cart");
        ResponseEntity<JsonNode> res;
        res = restTemplate.exchange(host + "/orchestrator/order/cartItems/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
    }

    private void modifyStock(String productId, String quantity, String operation) {
        HashMap<String, String> update = new HashMap<>();
        update.put("id", productId);
        update.put("quantity", quantity);
        update.put("operation", operation);
        HttpEntity<?> entity = new HttpEntity<>(update, headerHttp);
        ResponseEntity<JsonNode> res;
        System.out.println("errore Aggiornamento Magazzino");
        res = restTemplate.exchange(host + "/orchestrator/inventory/warehouse/refreshStock", HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        System.out.println("modifyStock" + res.getBody().toPrettyString());
    }


}
