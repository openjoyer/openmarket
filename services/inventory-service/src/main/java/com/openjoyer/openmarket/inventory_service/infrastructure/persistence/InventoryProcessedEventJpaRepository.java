package com.openjoyer.openmarket.inventory_service.infrastructure.persistence;

import com.openjoyer.openmarket.inventory_service.domain.model.InventoryProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface InventoryProcessedEventJpaRepository extends JpaRepository<InventoryProcessedEvent, UUID> {

    @Modifying
    @Query("delete from InventoryProcessedEvent e where e.processedAt < :timestamp")
    int deleteOlderThan(@Param("timestamp") Instant timestamp);
}
