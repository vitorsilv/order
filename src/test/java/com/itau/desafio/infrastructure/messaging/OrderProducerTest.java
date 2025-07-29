package com.itau.desafio.infrastructure.messaging;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class OrderProducerTest {

    @Test
    void shouldSendOrderEvent() {
        SnsTemplate snsTemplate = mock(SnsTemplate.class);
        OrderProducer producer = new OrderProducer(snsTemplate);
        OrderEvent event = mock(OrderEvent.class);

        producer.sendOrderEvent(event);

        verify(snsTemplate, times(1)).convertAndSend("order-topic", event);
    }
}