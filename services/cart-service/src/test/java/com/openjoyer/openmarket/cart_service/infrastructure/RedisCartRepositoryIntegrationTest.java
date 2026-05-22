package com.openjoyer.openmarket.cart_service.infrastructure;

import com.openjoyer.openmarket.cart_service.domain.model.Cart;
import com.openjoyer.openmarket.cart_service.domain.model.CartItem;
import com.openjoyer.openmarket.cart_service.infrastructure.redis.RedisCartDocument;
import com.openjoyer.openmarket.cart_service.infrastructure.redis.RedisCartRepository;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class RedisCartRepositoryIntegrationTest {

    @Container
    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private RedisTemplate<String, RedisCartDocument> redisTemplate;

    private RedisCartRepository repository;

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        repository = new RedisCartRepository(redisTemplate, Duration.ofDays(30));
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void save_shouldPersistCart() {
        Cart cart = new Cart();
        cart.setCartId("cart123");
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());
        cart.getItems().add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));
        cart.setUpdatedAt(Instant.now());

        Cart saved = repository.save(cart);

        assertNotNull(saved);
        assertEquals("cart123", saved.getCartId());
    }

    @Test
    void findByUserId_shouldReturnCart() {
        Cart cart = new Cart();
        cart.setCartId("cart123");
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());
        cart.getItems().add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));
        cart.setUpdatedAt(Instant.now());

        repository.save(cart);

        Optional<Cart> found = repository.findByUserId("user123");

        assertTrue(found.isPresent());
        assertEquals("cart123", found.get().getCartId());
        assertEquals(1, found.get().getItems().size());
        assertEquals("sku1", found.get().getItems().get(0).getSkuId());
    }

    @Test
    void findByUserId_shouldReturnEmptyWhenNotExists() {
        Optional<Cart> found = repository.findByUserId("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void deleteByUserId_shouldRemoveCart() {
        Cart cart = new Cart();
        cart.setCartId("cart123");
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());
        cart.setUpdatedAt(Instant.now());

        repository.save(cart);
        repository.deleteByUserId("user123");

        Optional<Cart> found = repository.findByUserId("user123");
        assertFalse(found.isPresent());
    }

    @Test
    void save_shouldUpdateExistingCart() {
        Cart cart = new Cart();
        cart.setCartId("cart123");
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());
        cart.getItems().add(new CartItem("sku1", "Product 1", "image1.jpg", 100.0, 2));
        cart.setUpdatedAt(Instant.now());

        repository.save(cart);

        cart.getItems().add(new CartItem("sku2", "Product 2", "image2.jpg", 50.0, 1));
        repository.save(cart);

        Optional<Cart> found = repository.findByUserId("user123");
        assertTrue(found.isPresent());
        assertEquals(2, found.get().getItems().size());
    }
}
