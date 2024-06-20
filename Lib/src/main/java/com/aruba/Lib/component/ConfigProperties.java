package com.aruba.Lib.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ms")
@Getter
@Setter
public class ConfigProperties {

    private String apiGatewayHost;
    private String deliveryBasePath;
    private String inventoryBasePath;
    private String orderBasePath;
    private String paymentBasePath;


}
