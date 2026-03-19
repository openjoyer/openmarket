package com.openjoyer.openmarket.cart_service.config;

import com.openjoyer.openmarket.cart_service.infrastructure.redis.RedisCartDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, RedisCartDocument> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, RedisCartDocument> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));

        template.afterPropertiesSet();
        return template;
    }
}