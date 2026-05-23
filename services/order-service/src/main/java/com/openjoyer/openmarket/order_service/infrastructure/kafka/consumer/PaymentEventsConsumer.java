package com.openjoyer.openmarket.order_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.payment.PaymentFailedEvent;
import com.openjoyer.openmarket.contracts.events.payment.PaymentSucceedEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.order_service.application.usecase.HandlePaymentFailedUseCase;
import com.openjoyer.openmarket.order_service.application.usecase.HandlePaymentSucceededUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventsConsumer {
    private final HandlePaymentSucceededUseCase handlePaymentSucceededUseCase;
    private final HandlePaymentFailedUseCase handlePaymentFailedUseCase;

    @KafkaListener(topics = KafkaTopics.PAYMENT_SUCCEEDED, groupId = "order-service")
    public void onPaymentSucceeded(PaymentSucceedEvent event) {
        handlePaymentSucceededUseCase.handle(event);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "order-service")
    public void onPaymentFailed(PaymentFailedEvent event) {
        handlePaymentFailedUseCase.handle(event);
    }
}
