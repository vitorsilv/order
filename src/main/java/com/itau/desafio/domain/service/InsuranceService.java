package com.itau.desafio.domain.service;


import com.itau.desafio.domain.model.insurance.*;
import com.itau.desafio.domain.repository.InsuranceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceService {
    private final InsuranceRepository insuranceRepository;
    private final FraudService fraudService;


    @Transactional
    public InsurancePolicyResponse createInsurancePolicy(InsurancePolicyRequest request) {

        InsurancePolicy saved = insuranceRepository.save(request.toEntity());

        log.info("1. RECEBIDO e salvo com sucesso");

        fraudService.processPolicy(saved);

        saved.addStatusHistory(InsurancePolicyStatus.PENDING);

        saved.addStatusHistory(InsurancePolicyStatus.APPROVED);

        insuranceRepository.save(saved);
        return saved.toResponse();
    }
}