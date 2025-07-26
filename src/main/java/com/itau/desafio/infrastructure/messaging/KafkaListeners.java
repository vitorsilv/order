package com.itau.desafio.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class KafkaListeners {

    @KafkaListener(topics = "policy-status-updates")
    public void consumePolicyUpdate(PolicyStatusEvent event) {
        log.info("Status atualizado recebido: {}", event);
    }
}
