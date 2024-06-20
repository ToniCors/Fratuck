package com.aruba.Orchestrator.scenarios;

import com.aruba.Orchestrator.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;


@SpringBootTest
public class Standard {


//    @Autowired
//    private WebApplicationContext webApplicationContext;

    private static final String host = "http://localhost:9000";

    //    private MockMvc mockMvc;
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
    public void getLoggedUser() {

        HttpEntity<String> entity;

        entity = new HttpEntity<>(headerHttp);
        ResponseEntity<JsonNode> res;
        res = restTemplate.exchange(host + "/users/getLogged", HttpMethod.GET, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        System.out.println("Logged User: " + res.getBody().toPrettyString());
    }

    /*
    E' stato scelto di associare solo un carrello per ogni utente.
    Quindi se l'Utente non ha nessun carrello associato viene creato
     altrimenti viene restituito il carrello associato
     */
    @Test
    public void creazioneCarrello() {

        HttpEntity<String> entity = new HttpEntity<>(headerHttp);
        ResponseEntity<JsonNode> res;
        res = restTemplate.exchange(host + "/orchestrator/order/carts/create", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        System.out.println("Logged User: " + res.getBody().toPrettyString());


    }

    /*Viene inserito un prodotto al carrello del utente, poi viene creato l'ordine*/
    @Test
    public void salvataggioOrdine(){

        addProductToCart();
        HttpEntity<String> entity = new HttpEntity<>(headerHttp);
        ResponseEntity<JsonNode> res;
        System.out.println("Create Order");
        res = restTemplate.exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        System.out.println("New Order: " + res.getBody().toPrettyString());
    }

    @Test
    public void verificaMagazzino(){


        HashMap<String, String> e1 = new HashMap<>();
        e1.put("product_id","2");
        e1.put("total","1");
        HashMap<String, String> e2 = new HashMap<>();
        e2.put("product_id","1");
        e2.put("total","1");

        List<HashMap<String,String>> req = new ArrayList<>();
        req.add(e1);
        req.add(e2);
        HttpEntity<?> entity = new HttpEntity<>(req, headerHttp);


        ResponseEntity<JsonNode> res;
        System.out.println("Vscenarios 1,2,4erifica magazzino");
        res = restTemplate.exchange(host + "/orchestrator/inventory/warehouse/checkStock", HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNull(res.getBody());
    }

    private void addProductToCart() {
        HashMap<String, String> req = new HashMap<>();

        req.put("product_id", "2");
        req.put("quantity", "10");

        HttpEntity<?> entity = new HttpEntity<>(req, headerHttp);
        System.out.println("add To Cart");
        ResponseEntity<JsonNode> res;
        res = restTemplate.exchange(host + "/orchestrator/order/cartItems/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
        Assertions.assertNull(res.getBody());
    }


    private String getLadyMarianToken() {
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJDMThOcjh2S1J6cy1TNG5SanY2X1IwM1RrSWh5QzlxWjFoLVNvN1JPb1BNIn0.eyJleHAiOjE3MTg2OTg2MTIsImlhdCI6MTcxODY5ODMxMiwianRpIjoiNTVhNGU0OGUtMmYxYS00MzE2LTk0NDUtMmEzNDg2ZGYyMmRiIiwiaXNzIjoiaHR0cDovL2tjc2VydmVyOjgwODAvcmVhbG1zL2ZyYXR1Y2siLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiY2U0Y2RmYjAtZjQ5Yi00OGRmLTgzZjYtMWViZDNkZjQ4OTg3IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZnJhdHVjay1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiYWI2ZGEwMTgtZjdjZS00NGQzLTk2NWMtNWNiNDgxMTkzMzA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1mcmF0dWNrIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImFiNmRhMDE4LWY3Y2UtNDRkMy05NjVjLTVjYjQ4MTE5MzMwNSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoibGFkeSBtYXJpYW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsYWR5bWFyaWFuIiwiZ2l2ZW5fbmFtZSI6ImxhZHkiLCJmYW1pbHlfbmFtZSI6Im1hcmlhbiIsImVtYWlsIjoibGFkeW1hcmlhbkB0ZXN0LmNvbSJ9.jcOkrsNIi1MA8nda2IXVQfaf7yj_OXQ6Wji98HhbpANsBfMRe-XyPsX1Uy0-KY2kH2eCrDxMNymayUGFHRIZl7MTISa3Mm0jPbzNfLvUPFBUzXzzboCFRDnj9O250wxnKJ31w8yyJMzB7ipYHmyFlZOp1j5lGpZLK1qb0E1Bhg7yxyNp5Yj6KdvAFXVVHyNV1QUsgSLZdB57usMyD9PS7HVEqt-cR4b5ccDZyQhNb4hoejtUtO1B4bqN3RqTL_rwGP7H8PwN2pgzh-oB1bpx9AXuL-M4QG9kuoavnuzj6HS455R5SShd4Fd_mESYum33lGMg0-Wni-EkmZPYmN1Orw";
    }


}
