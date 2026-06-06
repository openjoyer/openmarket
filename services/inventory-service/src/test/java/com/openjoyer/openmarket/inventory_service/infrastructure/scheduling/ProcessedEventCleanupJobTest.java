package com.openjoyer.openmarket.inventory_service.infrastructure.scheduling;

import com.openjoyer.openmarket.inventory_service.domain.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessedEventCleanupJobTest {
    @Mock
    private ProcessedEventRepository processedEventRepository;
    @InjectMocks
    private ProcessedEventCleanupJob job;

    @Test
    void deleteOld_shouldPurgeRowsOlderThanRetentionWindow() {
        ReflectionTestUtils.setField(job, "retentionDays", 30);
        when(processedEventRepository.deleteOlderThan(org.mockito.ArgumentMatchers.any())).thenReturn(5);

        Instant before = Instant.now().minus(Duration.ofDays(30));
        job.deleteOld();
        Instant after = Instant.now().minus(Duration.ofDays(30));

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(processedEventRepository).deleteOlderThan(captor.capture());
        Instant cutoff = captor.getValue();
        assertTrue(!cutoff.isBefore(before) && !cutoff.isAfter(after),
                "cutoff should be roughly now minus retention window");
    }
}
