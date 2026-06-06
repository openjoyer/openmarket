package com.openjoyer.openmarket.inventory_service.infrastructure.scheduling;

import com.openjoyer.openmarket.inventory_service.domain.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessedEventCleanupJob {
    private final ProcessedEventRepository processedEventRepository;

    @Value("${inventory.inbox.retention-days}")
    private int retentionDays;

    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    @Transactional
    public void deleteOld() {
        Instant deadline = Instant.now().minus(Duration.ofDays(retentionDays));
        int deleted = processedEventRepository.deleteOlderThan(deadline);
        log.info("Deleted {} processed_events older than {}", deleted, deadline);
    }
}
