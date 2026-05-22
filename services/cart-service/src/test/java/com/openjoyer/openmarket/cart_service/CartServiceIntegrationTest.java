package com.openjoyer.openmarket.cart_service;

import com.openjoyer.openmarket.cart_service.application.command.AddItemCommand;
import com.openjoyer.openmarket.cart_service.application.dto.CartView;
import com.openjoyer.openmarket.cart_service.application.usecase.AddItemToCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.GetCartUseCase;
import com.openjoyer.openmarket.cart_service.application.usecase.RemoveItemFromCartUseCase;
import com.openjoyer.openmarket.cart_service.domain.repository.CartRepository;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class CartServiceIntegrationTest {

    @Container
    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private AddItemToCartUseCase addItemToCartUseCase;

    @Autowired
    private GetCartUseCase getCartUseCase;

    @Autowired
    private RemoveItemFromCartUseCase removeItemFromCartUseCase;

    @Autowired
    private CartRepository cartRepository;

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        cartRepository.deleteByUserId("user123");
    }

    @Test
    void fullCartWorkflow_shouldWorkCorrectly() {
        AddItemCommand addCommand1 = new AddItemCommand(
                "user123",
                "sku1",
                "Product 1",
                "image1.jpg",
                100.0,
                2
        );
        CartView cart1 = addItemToCartUseCase.handle(addCommand1);

        assertNotNull(cart1);
        assertEquals("user123", cart1.getUserId());
        assertEquals(1, cart1.getItems().size());
        assertEquals(2, cart1.getItems().get(0).getQuantity());

        AddItemCommand addCommand2 = new AddItemCommand(
                "user123",
                "sku1",
                "Product 1",
                "image1.jpg",
                100.0,
                3
        );
        CartView cart2 = addItemToCartUseCase.handle(addCommand2);

        assertEquals(1, cart2.getItems().size());
        assertEquals(5, cart2.getItems().get(0).getQuantity());

        AddItemCommand addCommand3 = new AddItemCommand(
                "user123",
                "sku2",
                "Product 2",
                "image2.jpg",
                50.0,
                1
        );
        CartView cart3 = addItemToCartUseCase.handle(addCommand3);

        assertEquals(2, cart3.getItems().size());

        CartView retrievedCart = getCartUseCase.handle("user123");
        assertEquals(2, retrievedCart.getItems().size());

        var removeCommand = new com.openjoyer.openmarket.cart_service.application.command.RemoveItemCommand(
                "sku1",
                "user123",
                2
        );
        CartView cart4 = removeItemFromCartUseCase.handle(removeCommand);

        assertEquals(2, cart4.getItems().size());
        assertEquals(3, cart4.getItems().stream()
                .filter(i -> i.getSkuId().equals("sku1"))
                .findFirst()
                .get()
                .getQuantity());
    }

    @Test
    void contextLoads() {
        assertNotNull(addItemToCartUseCase);
        assertNotNull(getCartUseCase);
        assertNotNull(removeItemFromCartUseCase);
        assertNotNull(cartRepository);
    }
}