package com.openjoyer.openmarket.api_gateway.config;

import com.openjoyer.openmarket.api_gateway.properties.GatewayServicesProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayServicesProperties.class)
public class PropertiesConfig {
}
