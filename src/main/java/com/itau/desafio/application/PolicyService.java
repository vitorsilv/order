package com.itau.desafio.application;


import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.model.insurance.InsurancePolicyStatus;
import com.itau.desafio.domain.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    //CLASSE ANALISE DE FRAUDE

    @Transactional
    public InsurancePolicy createPolicy(InsurancePolicy request) {
        request.setId(UUID.randomUUID());
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(InsurancePolicyStatus.RECEIVED);
        request.addStatusHistory(InsurancePolicyStatus.RECEIVED);

        InsurancePolicy saved = policyRepository.save(request);
        // evento DE NOTIFICACAO
        return saved;
    }
}