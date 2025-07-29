package com.itau.desafio.service;

import com.itau.desafio.infrastructure.messaging.OrderEvent;
import com.itau.desafio.infrastructure.messaging.OrderProducer;
import com.itau.desafio.domain.model.insurance.*;
import com.itau.desafio.repository.InsuranceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

        InsurancePolicy entity = request.toEntity();
        InsurancePolicy policy = insuranceRepository.save(entity);

        log.info("{} Apólice criada com sucesso - ID: {}, Cliente: {}, Produto: {}",
                LOG_PREFIX,
                policy.getId(),
                policy.getCustomerId(),
                policy.getProductId());

        boolean validated = fraudService.processPolicy(policy);
        if (validated) {
            policy.changeStatus(InsurancePolicyStatus.VALIDATED);
            sendStatusEvent(policy, policy.getStatus());
        } else {
            policy.changeStatus(InsurancePolicyStatus.REJECTED);
            sendStatusEvent(policy, policy.getStatus());
            policy.setFinishedAt(LocalDateTime.now());
            log.info("{} Processo concluído para apólice {} - Tempo total: {}",
                    LOG_PREFIX,
                    policy.getId(),
                    calculateProcessingTime(policy));
            return policy.toResponse();
        }

        policy.changeStatus(InsurancePolicyStatus.PENDING);
        sendStatusEvent(policy, policy.getStatus());

        policy.changeStatus(InsurancePolicyStatus.APPROVED);
        sendStatusEvent(policy, policy.getStatus());

        policy.setFinishedAt(LocalDateTime.now());
        log.info("{} Processo concluído para apólice {} - Tempo total: {}",
                LOG_PREFIX,
                policy.getId(),
                calculateProcessingTime(policy));

        return policy.toResponse();
    }

    public Optional<InsurancePolicyResponse> findByPolicyId(UUID policyId) {
        log.info("{} Buscando apólice por ID: {}", LOG_PREFIX, policyId);
        return insuranceRepository.findById(policyId)
                .map(InsurancePolicy::toResponse);
    }

    public List<InsurancePolicyResponse> findByCustomerId(UUID customerId) {
        log.info("{} Buscando apólices por cliente: {}", LOG_PREFIX, customerId);
        return insuranceRepository.findByCustomerId(customerId).stream()
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