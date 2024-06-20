package com.aruba.Orchestrator.async;

import com.aruba.Orchestrator.component.OrchestratorLogger;
import com.aruba.Orchestrator.configuration.ConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Configuration
public class RabbitConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ConfigProperties configProperties;

    @RabbitListener(queues = "${rabbitmq.inboundQueueOrder}")
    public void processOrderMessage(Message message) {

        OrchestratorLogger.logger.info("Order Done");
        this.messageToJsonNode(message);

    }

    @RabbitListener(queues = "${rabbitmq.inboundQueuePayment}")
    public void processPaymentMessage(Message message) {

        try {
            OrchestratorLogger.logger.info("Payment Done");
            JsonNode node = this.messageToJsonNode(message);
            OrchestratorLogger.logger.info("Calling shipment");
            callShipmentService(node);
        } catch (RestClientException e) {
            OrchestratorLogger.logger.error("Enable to call Delivery service: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            OrchestratorLogger.logger.error("Error to work message: " + e.getMessage());
            e.printStackTrace();

        }


    }

    @RabbitListener(queues = "${rabbitmq.inboundQueueShipment}")
    public void processShipmentMessage(Message message) {
        OrchestratorLogger.logger.info("Shipment progress.....");
        this.messageToJsonNode(message);
    }

//    @RabbitListener(queues = "tappo")
//    public void debubListner(Message m) {
//        OrchestratorLogger.logger.info(new String(m.getBody()));
//    }

    private void callShipmentService(JsonNode node) {

        HashMap<String, String> map = new HashMap<>();

        HttpEntity<?> requestEntity = new HttpEntity<>(map);

        map.put("id", node.get("id").asText());
        if (node.hasNonNull("note")) map.put("note", node.get("note").asText());

        String url = configProperties.getDeliveryPath() + "/shipment/create";
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Object.class);


    }


    private JsonNode messageToJsonNode(Message message){

        JsonNode node = null;
        try {
            node = mapper.readTree(this.decodePayload(message));
            OrchestratorLogger.logger.info(node.toPrettyString());
        } catch (Exception e) {
            OrchestratorLogger.logger.error("Error to work message: " + e.getMessage());
            e.printStackTrace();
        }
        return node;

    }
    private String decodePayload(Message m) {
        return new String(m.getBody());

    }
}
