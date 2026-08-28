package ch.tbz.m321servicenoah.model;

import java.time.Instant;

public record OrderMessage(long orderId, String product, int quantity, Instant createdAt) {
}
