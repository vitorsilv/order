package com.itau.desafio.domain.service;


import com.itau.desafio.domain.model.insurance.*;
import com.itau.desafio.domain.repository.InsuranceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@AllArgsConstructor
@Service
public class InsuranceService {
    private InsuranceRepository insuranceRepository;
    private FraudService fraudService;



    @Transactional
    public InsurancePolicyResponse createInsurancePolicy(InsurancePolicyRequest request) {
        InsurancePolicy saved = insuranceRepository.save(request.toEntity());



        log.info("Apólice criada com sucesso: {}", saved.getId());

        fraudService.processPolicy(saved);
//
//        saved.addStatusHistory(InsurancePolicyStatus.PENDING);
//
//        saved.addStatusHistory(InsurancePolicyStatus.APPROVED);
//
//        insuranceRepository.save(saved);
        return saved.toResponse();
    }
}