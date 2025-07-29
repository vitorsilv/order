package com.itau.desafio.service;

import com.itau.desafio.domain.exception.FraudApiException;
import com.itau.desafio.domain.model.fraud.FraudAnalysisRequest;
import com.itau.desafio.domain.model.fraud.FraudAnalysisResponse;
import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.model.insurance.InsurancePolicyCategory;
import com.itau.desafio.infrastructure.client.FraudApiClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudService {
    private final String LOG_PREFIX = "[FraudService]";

    private final FraudApiClient fraudApiClient;

    public boolean processPolicy(InsurancePolicy insurancePolicy) {
        log.info("{} Iniciando análise de fraude - {}",
                LOG_PREFIX,
                buildPolicyLogMessage(insurancePolicy));

        try {
            FraudAnalysisRequest request = new FraudAnalysisRequest(
                    insurancePolicy.getId(),
                    insurancePolicy.getCustomerId());

            log.debug("{} Chamando API de Fraude - Request: {}", LOG_PREFIX, request);

            FraudAnalysisResponse analysis = fraudApiClient.analyzePolicy(request);

            log.info("{} Resposta da API de Fraude - Classificação: {}",
                    LOG_PREFIX,
                    analysis.classification());

            return switch (analysis.classification()) {
                case REGULAR -> validateRegularCustomerPolicy(insurancePolicy);
                case HIGH_RISK -> validateHighRiskCustomerPolicy(insurancePolicy);
                case PRIORITY -> validatePriorityCustomerPolicy(insurancePolicy);
                case NO_HISTORY -> validateNewCustomerPolicy(insurancePolicy);
            };
        } catch (FeignException e) {
            String errorMsg = String.format("Erro na API de Fraude - Status: %d, Mensagem: %s",
                    e.status(),
                    e.contentUTF8());
            log.error("{} {} - Policy: {}",
                    LOG_PREFIX,
                    errorMsg,
                    buildPolicyLogMessage(insurancePolicy),
                    e);

            throw new FraudApiException(
                    errorMsg,
                    HttpStatus.valueOf(e.status()),
                    e.contentUTF8()
            );
        } catch (Exception e) {
            log.error("{} Erro inesperado durante análise de fraude - {}",
                    LOG_PREFIX,
                    buildPolicyLogMessage(insurancePolicy),
                    e);

            throw new FraudApiException(
                    "Erro inesperado durante análise de fraude",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e
            );
        }
    }

    private boolean validateRegularCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case LIFE, PROPERTY -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("500000.00")) <= 0;
                logValidationResult("REGULAR", category, insuredAmount, isValid, "500000.00");
                yield isValid;
            }
            case AUTO -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("350000.00")) <= 0;
                logValidationResult("REGULAR", category, insuredAmount, isValid, "350000.00");
                yield isValid;
            }
            default -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("255000.00")) <= 0;
                logValidationResult("REGULAR", category, insuredAmount, isValid, "255000.00");
                yield isValid;
            }
        };
    }

    private boolean validateHighRiskCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case AUTO -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("25000.00")) <= 0;
                logValidationResult("HIGH_RISK", category, insuredAmount, isValid, "25000.00");
                yield isValid;
            }
            case PROPERTY -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("15000.00")) <= 0;
                logValidationResult("HIGH_RISK", category, insuredAmount, isValid, "15000.00");
                yield isValid;
            }
            default -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("125000.00")) <= 0;
                logValidationResult("HIGH_RISK", category, insuredAmount, isValid, "125000.00");
                yield isValid;
            }
        };
    }

    private boolean validatePriorityCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case LIFE -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("800000.00")) <= 0;
                logValidationResult("PRIORITY", category, insuredAmount, isValid, "800000.00");
                yield isValid;
            }
            case AUTO, PROPERTY -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("45000.00")) <= 0;
                logValidationResult("PRIORITY", category, insuredAmount, isValid, "45000.00");
                yield isValid;
            }
            default -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("375000.00")) <= 0;
                logValidationResult("PRIORITY", category, insuredAmount, isValid, "375000.00");
                yield isValid;
            }
        };
    }

    private boolean validateNewCustomerPolicy(InsurancePolicy policy) {
        BigDecimal insuredAmount = policy.getInsuredAmount();
        InsurancePolicyCategory category = policy.getCategory();

        return switch (category) {
            case LIFE, PROPERTY -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("20000.00")) <= 0;
                logValidationResult("NO_HISTORY", category, insuredAmount, isValid, "20000.00");
                yield isValid;
            }
            case AUTO -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("75000.00")) <= 0;
                logValidationResult("NO_HISTORY", category, insuredAmount, isValid, "75000.00");
                yield isValid;
            }
            default -> {
                boolean isValid = insuredAmount.compareTo(new BigDecimal("55000.00")) <= 0;
                logValidationResult("NO_HISTORY", category, insuredAmount, isValid, "55000.00");
                yield isValid;
            }
        };
    }

    private String buildPolicyLogMessage(InsurancePolicy policy) {
        String POLICY_LOG_MSG = "PolicyID: %s, CustomerID: %s, Category: %s, Amount: %s";
        return String.format(POLICY_LOG_MSG,
                policy.getId(),
                policy.getCustomerId(),
                policy.getCategory(),
                policy.getInsuredAmount());
    }

    private void logValidationResult(String customerType,
                                     InsurancePolicyCategory category,
                                     BigDecimal amount,
                                     boolean isValid,
                                     String limit) {
        log.info("{} Validação {} - Categoria: {}, Valor: {}, Limite: {}, Resultado: {}",
                LOG_PREFIX,
                customerType,
                category,
                amount,
                limit,
                isValid ? "APROVADO" : "REJEITADO");
    }
}