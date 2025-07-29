package com.itau.desafio.infrastructure.messaging;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    private final SnsTemplate snsTemplate;

    public OrderProducer(SnsTemplate snsTemplate) {
        this.snsTemplate = snsTemplate;
    }

    public void sendOrderEvent(OrderEvent event) {
        snsTemplate.convertAndSend("order-topic", event);
    }
}