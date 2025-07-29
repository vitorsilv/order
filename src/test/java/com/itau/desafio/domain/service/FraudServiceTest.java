package com.itau.desafio.domain.service;

import com.itau.desafio.domain.exception.FraudApiException;
import com.itau.desafio.domain.model.fraud.FraudAnalysisRequest;
import com.itau.desafio.domain.model.fraud.FraudAnalysisResponse;
import com.itau.desafio.domain.model.fraud.FraudClassification;
import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.model.insurance.InsurancePolicyCategory;
import com.itau.desafio.infrastructure.client.FraudApiClient;
import com.itau.desafio.service.FraudService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FraudServiceTest {

    @Mock
    private FraudApiClient fraudApiClient;

    @InjectMocks
    private FraudService fraudService;

    private final UUID orderId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final String analyzedAt = LocalDateTime.now().toString();
    private final List<FraudAnalysisResponse.Occurrence> occurrences = Collections.emptyList();

    private InsurancePolicy buildPolicy(InsurancePolicyCategory category, BigDecimal amount) {
        InsurancePolicy policy = mock(InsurancePolicy.class);
        when(policy.getId()).thenReturn(orderId);
        when(policy.getCustomerId()).thenReturn(customerId);
        when(policy.getCategory()).thenReturn(category);
        when(policy.getInsuredAmount()).thenReturn(amount);
        return policy;
    }

    private FraudAnalysisResponse buildFraudAnalysisResponse(
            FraudClassification classification
    ) {
        return new FraudAnalysisResponse(orderId, customerId, analyzedAt, classification, occurrences);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldApproveRegularCustomerPolicy() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("300000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));

        boolean result = fraudService.processPolicy(policy);

        assertTrue(result);
    }

    @Test
    void shouldRejectRegularCustomerPolicyAboveLimit() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("400000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));

        boolean result = fraudService.processPolicy(policy);

        assertFalse(result);
    }

    @Test
    void shouldApproveHighRiskCustomerPolicy() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("20000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.HIGH_RISK));

        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectHighRiskCustomerPolicyAboveLimit() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("30000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.HIGH_RISK));

        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApprovePriorityCustomerPolicy() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.LIFE, new BigDecimal("700000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));

        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectPriorityCustomerPolicyAboveLimit() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.LIFE, new BigDecimal("900000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));

        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveNoHistoryCustomerPolicy() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("70000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));

        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectNoHistoryCustomerPolicyAboveLimit() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("80000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));

        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldThrowFraudApiExceptionOnFeignException() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("10000.00"));
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(500);
        when(feignException.contentUTF8()).thenReturn("Internal Error");

        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class))).thenThrow(feignException);

        FraudApiException ex = assertThrows(FraudApiException.class, () -> fraudService.processPolicy(policy));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Erro na API de Fraude"));
    }

    @Test
    void shouldThrowFraudApiExceptionOnUnexpectedException() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("10000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class))).thenThrow(new RuntimeException("Unexpected"));

        FraudApiException ex = assertThrows(FraudApiException.class, () -> fraudService.processPolicy(policy));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Erro inesperado durante análise de fraude"));
    }


    @Test
    void shouldApproveRegularCustomerPolicyAtLimitLife() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.LIFE, new BigDecimal("500000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectRegularCustomerPolicyAboveLimitLife() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.LIFE, new BigDecimal("500000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveRegularCustomerPolicyAtLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("500000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectRegularCustomerPolicyAboveLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("500000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveHighRiskCustomerPolicyAtLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("15000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.HIGH_RISK));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectHighRiskCustomerPolicyAboveLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("15000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.HIGH_RISK));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApprovePriorityCustomerPolicyAtLimitAuto() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("45000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectPriorityCustomerPolicyAboveLimitAuto() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.AUTO, new BigDecimal("45000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApprovePriorityCustomerPolicyAtLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("45000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectPriorityCustomerPolicyAboveLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("45000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveNoHistoryCustomerPolicyAtLimitLife() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.LIFE, new BigDecimal("20000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectNoHistoryCustomerPolicyAboveLimitLife() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.LIFE, new BigDecimal("20000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveNoHistoryCustomerPolicyAtLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("20000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectNoHistoryCustomerPolicyAboveLimitProperty() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.PROPERTY, new BigDecimal("20000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveRegularCustomerPolicyAtLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("255000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectRegularCustomerPolicyAboveLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("255000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.REGULAR));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveHighRiskCustomerPolicyAtLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("125000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.HIGH_RISK));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectHighRiskCustomerPolicyAboveLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("125000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.HIGH_RISK));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApprovePriorityCustomerPolicyAtLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("375000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectPriorityCustomerPolicyAboveLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("375000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.PRIORITY));
        assertFalse(fraudService.processPolicy(policy));
    }

    @Test
    void shouldApproveNoHistoryCustomerPolicyAtLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("55000.00"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));
        assertTrue(fraudService.processPolicy(policy));
    }

    @Test
    void shouldRejectNoHistoryCustomerPolicyAboveLimitBusiness() {
        InsurancePolicy policy = buildPolicy(InsurancePolicyCategory.BUSINESS, new BigDecimal("55000.01"));
        when(fraudApiClient.analyzePolicy(any(FraudAnalysisRequest.class)))
                .thenReturn(buildFraudAnalysisResponse(FraudClassification.NO_HISTORY));
        assertFalse(fraudService.processPolicy(policy));
    }
}