package com.itau.desafio.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderEvent(
        UUID orderId,
        UUID customerId,
        String status,
        LocalDateTime timestamp
) {
    public OrderEvent(UUID orderId, UUID customerId, String status) {
        this(orderId, customerId, status, LocalDateTime.now());
    }
}
