package com.openjoyer.openmarket.order_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.payment.PaymentSucceedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventsConsumer {

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_SUCCEEDED,
            groupId = "order-service"
    )
    public void handlePaymentSucceeded(PaymentSucceedEvent event) {
        // TODO
    }
}
