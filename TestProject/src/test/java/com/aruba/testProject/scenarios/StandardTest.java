package com.aruba.testProject.scenarios;

import com.aruba.testProject.utils.TestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.aruba.testProject.utils.TestUtils.*;
import static java.lang.Thread.sleep;


@SpringBootTest
public class StandardTest {


    private static HttpHeaders headerHttp;

    @BeforeEach
    public void setup(){
        headerHttp = new HttpHeaders();
        headerHttp.setContentType(MediaType.APPLICATION_JSON);
        headerHttp.setBearerAuth(callForLogin(ladyMarian));
    }

    @Test
    public void login() {
        callForLogin(robin);
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
        res = getRestTemplate().exchange(host + "/orchestrator/order/carts/create", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        System.out.println("Cart: " + res.getBody().toPrettyString());


    }

    /*Viene inserito un prodotto al carrello del utente, poi viene creato l'ordine*/
    @Test
    public void salvataggioOrdine() {

        addProductToCart();
        HttpEntity<String> entity = new HttpEntity<>(headerHttp);
        ResponseEntity<JsonNode> res;
        System.out.println("Create Order");
        res = getRestTemplate().exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        System.out.println("New Order: " + res.getBody().toPrettyString());
    }

    @Test
    public void verificaMagazzino() {

        HashMap<String, String> e1 = new HashMap<>();
        e1.put("product_id", "2");
        e1.put("total", "1");
        HashMap<String, String> e2 = new HashMap<>();
        e2.put("product_id", "1");
        e2.put("total", "1");

        List<HashMap<String, String>> req = new ArrayList<>();
        req.add(e1);
        req.add(e2);
        HttpEntity<?> entity = new HttpEntity<>(req, headerHttp);


        ResponseEntity<JsonNode> res;
        res = getRestTemplate().exchange(host + "/orchestrator/inventory/warehouse/checkStock", HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNull(res.getBody());

        for (HashMap<String, String> map : req) {
            res = getRestTemplate().exchange(host + "/orchestrator/inventory/products/" + map.get("product_id"), HttpMethod.GET, new HttpEntity<>(headerHttp), JsonNode.class);
            Assertions.assertNotNull(res.getBody().get("warehouse").get("stock"));
            Assertions.assertTrue(Long.parseLong(map.get("total")) < res.getBody().get("warehouse").get("stock").asLong());
            System.out.println("Product: " + res.getBody().toPrettyString());
        }

    }

    @Test
    public void pagaOrdine_creazioneSpedizione_spedizioneOrdine() throws InterruptedException {
        addProductToCart();
        HttpEntity<String> entity = new HttpEntity<>(headerHttp);
        ResponseEntity<JsonNode> res;
        System.out.println("Create Order");
        res = getRestTemplate().exchange(host + "/orchestrator/order/orders/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        System.out.println("New Order: " + res.getBody().toPrettyString());

        String orderID = res.getBody().get("id").asText();
        String paymentID = res.getBody().get("payment").get("id").asText();
        res = getRestTemplate().exchange(host + "/orchestrator/payment/payments/pay/"+paymentID, HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals("SUCCESSFUL", res.getBody().get("status").asText());
        System.out.println("Payment SUCCESSFUL: " + res.getBody().toPrettyString());

        sleep(2000);

        res = getRestTemplate().exchange(host + "/orchestrator/delivery/shipment/order/"+orderID, HttpMethod.GET, entity, JsonNode.class);

        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        Assertions.assertEquals("NEW", res.getBody().get("status").asText());

        System.out.println("Delivery NEW: " + res.getBody().toPrettyString());

        String shipmentID = res.getBody().get("id").asText();

        res = getRestTemplate().exchange(host + "/orchestrator/delivery/shipment/progress/"+shipmentID, HttpMethod.POST, entity, JsonNode.class);

        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        Assertions.assertEquals("PROGRESS", res.getBody().get("status").asText());

        System.out.println("Delivery PROGRESS: " + res.getBody().toPrettyString());

        res = getRestTemplate().exchange(host + "/orchestrator/delivery/shipment/deliver/"+shipmentID, HttpMethod.POST, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(res.getBody());
        Assertions.assertEquals("SHIPPED", res.getBody().get("status").asText());

        System.out.println("Delivery SHIPPED: " + res.getBody().toPrettyString());
    }

    private void addProductToCart() {
        HashMap<String, String> req = new HashMap<>();

        req.put("product_id", "2");
        req.put("quantity", "10");

        HttpEntity<?> entity = new HttpEntity<>(req, headerHttp);
        System.out.println("add To Cart");
        ResponseEntity<JsonNode> res;
        res = getRestTemplate().exchange(host + "/orchestrator/order/cartItems/put", HttpMethod.PUT, entity, JsonNode.class);
        Assertions.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
        Assertions.assertNull(res.getBody());
    }




}
