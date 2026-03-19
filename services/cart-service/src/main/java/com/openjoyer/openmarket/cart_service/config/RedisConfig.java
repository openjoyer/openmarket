package com.openjoyer.openmarket.cart_service.config;

import com.openjoyer.openmarket.cart_service.infrastructure.redis.RedisCartDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, RedisCartDocument> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisCartDocument> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(RedisCartDocument.class));
        template.setHashValueSerializer(new JacksonJsonRedisSerializer<>(RedisCartDocument.class));

        template.afterPropertiesSet();
        return template;
    }
}