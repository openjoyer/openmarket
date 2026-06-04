package com.openjoyer.openmarket.order_service.infrastructure.kafka.consumer;

import com.openjoyer.openmarket.contracts.events.shipment.ShipmentFailedEvent;
import com.openjoyer.openmarket.contracts.events.shipment.ShipmentSucceededEvent;
import com.openjoyer.openmarket.contracts.kafka.KafkaTopics;
import com.openjoyer.openmarket.order_service.application.usecase.HandleShipmentFailedUseCase;
import com.openjoyer.openmarket.order_service.application.usecase.HandleShipmentSucceededUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentEventsConsumer {
    private final HandleShipmentSucceededUseCase handleShipmentSucceededUseCase;
    private final HandleShipmentFailedUseCase handleShipmentFailedUseCase;

    @KafkaListener(topics = KafkaTopics.SHIPMENT_SUCCEEDED, groupId = "order-service")
    public void onShipmentSucceeded(ShipmentSucceededEvent shipmentSucceededEvent) {
        handleShipmentSucceededUseCase.handle(shipmentSucceededEvent);
    }

    @KafkaListener(topics = KafkaTopics.SHIPMENT_FAILED, groupId = "order-service")
    public void onShipmentFailed(ShipmentFailedEvent shipmentFailedEvent) {
        handleShipmentFailedUseCase.handle(shipmentFailedEvent);
    }
}
