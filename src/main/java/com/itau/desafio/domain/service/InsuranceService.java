package com.itau.desafio.domain.service;


import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.repository.InsuranceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceService {
    private InsuranceRepository insuranceRepository;
    //CLASSE ANALISE DE FRAUDE

    @Transactional
    public InsurancePolicy createPolicy(InsurancePolicy request) {
        InsurancePolicy saved = insuranceRepository.save(request);
        // evento DE NOTIFICACAO
        return saved;
    }
}