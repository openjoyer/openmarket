package com.openjoyer.openmarket.order_service.integration;

import com.openjoyer.openmarket.contracts.events.inventory.StockReleaseRequestedEvent;
import com.openjoyer.openmarket.contracts.events.inventory.StockReservedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRefundRequestedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentRequestedEvent;
import com.openjoyer.openmarket.contracts.events.shipment.ShipmentFailedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.order_service.domain.model.Order;
import com.openjoyer.openmarket.order_service.domain.model.OrderItem;
import com.openjoyer.openmarket.order_service.domain.model.OrderStatus;
import com.openjoyer.openmarket.order_service.domain.repository.OrderRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
@Testcontainers
@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        topics = {
                KafkaTopics.STOCK_RESERVATION_SUCCEEDED,
                KafkaTopics.STOCK_RESERVATION_FAILED,
                KafkaTopics.PAYMENT_SUCCEEDED,
                KafkaTopics.PAYMENT_FAILED,
                KafkaTopics.SHIPMENT_SUCCEEDED,
                KafkaTopics.SHIPMENT_FAILED,
                KafkaTopics.PAYMENT_REQUESTED,
                KafkaTopics.SHIPMENT_REQUESTED,
                KafkaTopics.STOCK_RELEASE_REQUESTED,
                KafkaTopics.PAYMENT_REFUND_REQUESTED
        }
)
class OrderSagaIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Test
    void reservationSucceeded_movesOrderToPaymentPending_andPublishesPaymentRequested() {
        Order seed = Order.create("user-1",
                List.of(OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 2)));
        UUID orderId = orderRepository.save(seed).getId();

        try (Consumer<String, PaymentRequestedEvent> paymentReq =
                     consumerFor(PaymentRequestedEvent.class, KafkaTopics.PAYMENT_REQUESTED)) {

            kafkaTemplate.send(KafkaTopics.STOCK_RESERVATION_SUCCEEDED, orderId.toString(),
                    new StockReservedEvent(orderId));

            await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                    assertEquals(OrderStatus.PAYMENT_PENDING,
                            orderRepository.findById(orderId).orElseThrow().getOrderStatus()));

            PaymentRequestedEvent published = awaitRecord(paymentReq, KafkaTopics.PAYMENT_REQUESTED);
            assertEquals(orderId, published.orderId());
            assertEquals("user-1", published.userId());
            assertEquals(0, published.amount().compareTo(new BigDecimal("20")));
        }
    }

    @Test
    void shipmentFailed_compensatesProcessingOrder_andPublishesReleaseAndRefund() {
        Order seed = Order.create("user-1",
                List.of(OrderItem.create("sku-1", "Keyboard", "keyboard.png", BigDecimal.TEN, 1)));
        seed.markReserved();
        seed.markPaid();
        UUID orderId = orderRepository.save(seed).getId();

        try (Consumer<String, StockReleaseRequestedEvent> releaseReq =
                     consumerFor(StockReleaseRequestedEvent.class, KafkaTopics.STOCK_RELEASE_REQUESTED);
             Consumer<String, PaymentRefundRequestedEvent> refundReq =
                     consumerFor(PaymentRefundRequestedEvent.class, KafkaTopics.PAYMENT_REFUND_REQUESTED)) {

            kafkaTemplate.send(KafkaTopics.SHIPMENT_FAILED, orderId.toString(),
                    new ShipmentFailedEvent(orderId, Instant.now()));

            await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                    assertEquals(OrderStatus.CANCELED,
                            orderRepository.findById(orderId).orElseThrow().getOrderStatus()));

            StockReleaseRequestedEvent release = awaitRecord(releaseReq, KafkaTopics.STOCK_RELEASE_REQUESTED);
            assertEquals(orderId, release.orderId());

            PaymentRefundRequestedEvent refund = awaitRecord(refundReq, KafkaTopics.PAYMENT_REFUND_REQUESTED);
            assertEquals(orderId, refund.orderId());
            assertEquals(0, refund.amount().compareTo(BigDecimal.TEN));
        }
    }


    private <T> Consumer<String, T> consumerFor(Class<T> type, String topic) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "it-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JacksonJsonDeserializer<T> valueDeserializer = new JacksonJsonDeserializer<>(type)
                .ignoreTypeHeaders()
                .trustedPackages("com.openjoyer.openmarket.contracts.*");

        Consumer<String, T> consumer = new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer).createConsumer();
        consumer.subscribe(List.of(topic));
        return consumer;
    }

    private <T> T awaitRecord(Consumer<String, T> consumer, String topic) {
        return await().atMost(Duration.ofSeconds(30)).until(() -> {
            ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(300));
            Iterator<ConsumerRecord<String, T>> it = records.records(topic).iterator();
            return it.hasNext() ? it.next().value() : null;
        }, Objects::nonNull);
    }
}