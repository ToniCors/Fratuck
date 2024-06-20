package com.aruba.Orchestrator.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ms")
@Getter
@Setter
public class ConfigProperties {

    private String deliveryPath;
    private String inventoryPath;
    private String orderPath;
    private String paymentPath;


}
