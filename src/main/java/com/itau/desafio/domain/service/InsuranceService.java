package com.itau.desafio.domain.service;

import com.itau.desafio.domain.event.OrderEvent;
import com.itau.desafio.infrastructure.messaging.OrderProducer;
import com.itau.desafio.domain.model.insurance.*;
import com.itau.desafio.domain.repository.InsuranceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class InsuranceService {
    private static final String LOG_PREFIX = "[InsuranceService]";

    private final InsuranceRepository insuranceRepository;
    private final FraudService fraudService;
    private final OrderProducer eventProducer;

    @Transactional
    public InsurancePolicyResponse createInsurancePolicy(InsurancePolicyRequest request) {
        log.info("{} Iniciando criação de apólice para cliente: {}", LOG_PREFIX, request.getCustomerId());

        InsurancePolicy policy = request.toEntity();
        InsurancePolicy saved = insuranceRepository.save(policy);

        log.info("{} Apólice criada com sucesso - ID: {}, Cliente: {}, Produto: {}",
                LOG_PREFIX,
                saved.getId(),
                saved.getCustomerId(),
                saved.getProductId());

        boolean validated = fraudService.processPolicy(policy);
        if (validated) {
            policy.addStatusHistory(InsurancePolicyStatus.VALIDATED);
            log.info("{} Validação de fraude APROVADA para apólice {}", LOG_PREFIX, saved.getId());
        } else {
            policy.addStatusHistory(InsurancePolicyStatus.REJECTED);
            log.info("{} Validação de fraude REJEITADA para apólice {}", LOG_PREFIX, saved.getId());
            sendStatusEvent(saved, policy.getStatus());
            policy.setFinishedAt(LocalDateTime.now());
            log.info("{} Processo concluído para apólice {} - Tempo total: {}",
                    LOG_PREFIX,
                    saved.getId(),
                    calculateProcessingTime(policy));
            return policy.toResponse();
        }

        sendStatusEvent(saved, policy.getStatus());

        policy.addStatusHistory(InsurancePolicyStatus.PENDING);
        log.info("{} Apólice {} em estado PENDENTE", LOG_PREFIX, saved.getId());
        sendStatusEvent(saved, policy.getStatus());

        policy.addStatusHistory(InsurancePolicyStatus.APPROVED);
        log.info("{} Apólice {} APROVADA com sucesso", LOG_PREFIX, saved.getId());
        sendStatusEvent(saved, policy.getStatus());

        policy.setFinishedAt(LocalDateTime.now());
        log.info("{} Processo concluído para apólice {} - Tempo total: {}",
                LOG_PREFIX,
                saved.getId(),
                calculateProcessingTime(policy));

        return policy.toResponse();
    }

    public InsurancePolicyResponse findByPolicyId(UUID policyId) {
        log.info("{} Buscando apólice por ID: {}", LOG_PREFIX, policyId);
        return insuranceRepository.findById(policyId)
                .map(InsurancePolicy::toResponse)
                .orElseThrow(() -> {
                    log.error("{} Apólice não encontrada: {}", LOG_PREFIX, policyId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Apólice não encontrada com ID: " + policyId
                    );
                });
    }

    public List<InsurancePolicyResponse> findByCustomerId(UUID customerId) {
        log.info("{} Buscando apólices por cliente: {}", LOG_PREFIX, customerId);
        List<InsurancePolicy> policies = insuranceRepository.findByCustomerId(customerId);

        if (policies.isEmpty()) {
            log.warn("{} Nenhuma apólice encontrada para cliente: {}", LOG_PREFIX, customerId);
        }

        return policies.stream()
                .map(InsurancePolicy::toResponse)
                .toList();
    }


    private void sendStatusEvent(InsurancePolicy policy, InsurancePolicyStatus status) {
        try {
            OrderEvent event = new OrderEvent(
                    policy.getId(),
                    policy.getCustomerId(),
                    status.toString()
            );
            eventProducer.sendOrderEvent(event);
            log.debug("{} Evento enviado para apólice {} - Status: {}",
                    LOG_PREFIX,
                    policy.getId(),
                    status);
        } catch (Exception e) {
            log.error("{} Falha ao enviar evento para apólice {} - Erro: {}",
                    LOG_PREFIX,
                    policy.getId(),
                    e.getMessage(), e);
        }
    }

    private String calculateProcessingTime(InsurancePolicy policy) {
        if (policy.getFinishedAt() != null && policy.getCreatedAt() != null) {
            long seconds = java.time.Duration.between(
                    policy.getCreatedAt(),
                    policy.getFinishedAt()
            ).getSeconds();
            return seconds + " segundos";
        }
        return "não calculado";
    }
}