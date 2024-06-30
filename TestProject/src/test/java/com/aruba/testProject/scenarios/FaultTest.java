package com.aruba.testProject.scenarios;

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

import static com.aruba.testProject.utils.TestUtils.*;

@SpringBootTest
public class FaultTest {

    private static HttpHeaders headerHttp;
    @BeforeEach
    public void setup(){
        headerHttp = new HttpHeaders();
        headerHttp.setContentType(MediaType.APPLICATION_JSON);
        headerHttp.setBearerAuth(callForLogin(ladyMarian));

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
            getRestTemplate().exchange(host + "/orchestrator/inventory/warehouse/refreshStock", HttpMethod.POST, entity, JsonNode.class);
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
            getRestTemplate().exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        Assertions.assertTrue(e.getMessage().contains("Impossible create order, Cart is empty"));
        System.out.println(e.getMessage());
    }

    @Test
    public void errorePagamentoGiacenzaMancante() {

        String productId = "2";

        HttpClientErrorException e = Assertions.assertThrows(HttpClientErrorException.class, () -> {


            modifyStock(productId, "100", "0");

            addProductToCart(productId);
            HttpEntity<String> entity = new HttpEntity<>(headerHttp);
            ResponseEntity<JsonNode> res;
            System.out.println("Create Order");
            res = getRestTemplate().exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
            Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
            Assertions.assertNotNull(res.getBody());
            System.out.println("New Order: " + res.getBody().toPrettyString());

            modifyStock(productId, "10000", "1");
            String paymentID = res.getBody().get("payment").get("id").asText();
            getRestTemplate().exchange(host + "/orchestrator/payment/payments/pay/" + paymentID, HttpMethod.POST, entity, JsonNode.class);

        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        Assertions.assertTrue(e.getMessage().contains("Enable to make payment. One or more product is out of stock"));
        System.out.println(e.getMessage());
        modifyStock(productId, "100", "0");

    }

    private void addProductToCart(String productId) {

        HashMap<String, String> req = new HashMap<>();

        req.put("product_id", productId);
        req.put("quantity", "10");

        HttpEntity<?> entity = new HttpEntity<>(req, headerHttp);
        System.out.println("add To Cart");
        ResponseEntity<JsonNode> res;
        res = getRestTemplate().exchange(host + "/orchestrator/order/cartItems/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
    }

    private void modifyStock(String productId, String quantity, String operation) {
        HashMap<String, String> update = new HashMap<>();
        update.put("id", productId);
        update.put("quantity", quantity);
        update.put("operation", operation);
        HttpEntity<?> entity = new HttpEntity<>(update, headerHttp);
        ResponseEntity<JsonNode> res;
        System.out.println("Aggiornamento Magazzino");
        res = getRestTemplate().exchange(host + "/orchestrator/inventory/warehouse/refreshStock", HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        System.out.println("modifyStock" + res.getBody().toPrettyString());
    }


}
