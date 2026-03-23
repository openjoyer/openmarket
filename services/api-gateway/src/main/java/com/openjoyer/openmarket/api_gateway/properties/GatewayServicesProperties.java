package com.openjoyer.openmarket.api_gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.services")
@Getter
@Setter
public class GatewayServicesProperties {
    private String authUri;
    private String cartUri;
    private String catalogUri;
}
