package ch.tbz.m321servicenoah.model;

import java.time.Instant;

public record OrderProcessedEvent(
        long orderId,
        String product,
        int quantity,
        Instant processedAt,
        String processedBy) {
}
