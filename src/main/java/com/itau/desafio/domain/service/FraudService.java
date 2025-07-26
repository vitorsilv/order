package com.itau.desafio.domain.service;

import com.itau.desafio.domain.model.fraud.FraudAnalysisRequest;
import com.itau.desafio.domain.model.fraud.FraudAnalysisResponse;
import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.model.insurance.InsurancePolicyCategory;
import com.itau.desafio.domain.model.insurance.InsurancePolicyStatus;
import com.itau.desafio.infrastructure.FraudApiClient;
import com.itau.desafio.infrastructure.messaging.PolicyStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudService {
    private final FraudApiClient fraudApiClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void processPolicy(InsurancePolicy insurancePolicy) {
        FraudAnalysisResponse analysis = fraudApiClient.analyzePolicy(
                new FraudAnalysisRequest(insurancePolicy.getId(), insurancePolicy.getCustomerId())
        );

        // Verificação de dados de resposta com os dados do banco

        boolean validated = switch(analysis.classification()) {
            case REGULAR -> validateRegularCustomerPolicy(insurancePolicy);
            case HIGH_RISK -> validateHigRiskCustomerPolicy(insurancePolicy);
            case PRIORITY -> validatePriorityCustomerPolicy(insurancePolicy);
            case NO_HISTORY -> validateNewCustomerPolicy(insurancePolicy);
        };

        if (validated) {
            insurancePolicy.addStatusHistory(InsurancePolicyStatus.VALIDATED);
            log.info("2. VALIDADO ");
        } else {
            insurancePolicy.addStatusHistory(InsurancePolicyStatus.REJECTED);
            log.info("4. REJEITADO ");
        }

        kafkaTemplate.send("policy-status-updates",
                new PolicyStatusEvent(insurancePolicy.getId(), insurancePolicy.getStatus()));
    }

    private boolean validateRegularCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case LIFE, PROPERTY -> insuredAmount.compareTo(new BigDecimal("500000.00")) <= 0;
            case AUTO -> insuredAmount.compareTo(new BigDecimal("350000.00")) <= 0;
            default -> insuredAmount.compareTo(new BigDecimal("255000.00")) <= 0;
        };
    }

    private boolean validateHigRiskCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case AUTO -> insuredAmount.compareTo(new BigDecimal("25000.00")) <= 0;
            case PROPERTY -> insuredAmount.compareTo(new BigDecimal("15000.00")) <= 0;
            default -> insuredAmount.compareTo(new BigDecimal("125000.00")) <= 0;
        };
    }

    private boolean validatePriorityCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case LIFE  -> insuredAmount.compareTo(new BigDecimal("800000.00")) <= 0;
            case AUTO, PROPERTY -> insuredAmount.compareTo(new BigDecimal("45000.00")) <= 0;
            default -> insuredAmount.compareTo(new BigDecimal("375000.00")) <= 0;
        };
    }

    private boolean validateNewCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case LIFE, PROPERTY  -> insuredAmount.compareTo(new BigDecimal("20000.00")) <= 0;
            case AUTO  -> insuredAmount.compareTo(new BigDecimal("75000.00")) <= 0;
            default -> insuredAmount.compareTo(new BigDecimal("55000.00")) <= 0;
        };
    }

}
