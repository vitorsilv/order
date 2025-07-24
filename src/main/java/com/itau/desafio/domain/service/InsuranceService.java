package com.itau.desafio.application;


import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.model.insurance.InsurancePolicyStatus;
import com.itau.desafio.domain.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class PolicyService {
    private  PolicyRepository policyRepository;
    //CLASSE ANALISE DE FRAUDE

    @Transactional
    public InsurancePolicy createPolicy(InsurancePolicy request) {
        InsurancePolicy saved = policyRepository.save(request);
        // evento DE NOTIFICACAO
        return saved;
    }
}