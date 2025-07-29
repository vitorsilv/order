package com.itau.desafio.domain.service;

import com.itau.desafio.domain.model.insurance.*;
import com.itau.desafio.repository.InsuranceRepository;
import com.itau.desafio.infrastructure.messaging.OrderProducer;
import com.itau.desafio.service.FraudService;
import com.itau.desafio.service.InsuranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InsuranceServiceTest {

    @Mock
    private InsuranceRepository insuranceRepository;
    @Mock
    private FraudService fraudService;
    @Mock
    private OrderProducer eventProducer;

    @InjectMocks
    private InsuranceService insuranceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private InsurancePolicyRequest buildRequest() {
        return new InsurancePolicyRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                InsurancePolicyCategory.AUTO,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                BigDecimal.valueOf(100.0),
                BigDecimal.valueOf(10000.0),
                Map.of("Coverage1", BigDecimal.valueOf(5000)),
                List.of("Assistance1")
        );
    }

    @Test
    void shouldCreateApprovedPolicy() {
        InsurancePolicyRequest request = buildRequest();
        InsurancePolicy policy = request.toEntity();
        policy.setId(UUID.randomUUID());

        when(insuranceRepository.save(any())).thenReturn(policy);
        when(fraudService.processPolicy(any())).thenReturn(true);

        InsurancePolicyResponse response = insuranceService.createInsurancePolicy(request);

        assertNotNull(response);
        assertEquals(policy.getCustomerId(), response.getCustomerId());
        assertEquals(InsurancePolicyStatus.APPROVED, response.getStatus());
        verify(eventProducer, atLeastOnce()).sendOrderEvent(any());
    }

    @Test
    void shouldCreateRejectedPolicyDueToFraud() {
        InsurancePolicyRequest request = buildRequest();
        InsurancePolicy policy = request.toEntity();
        policy.setId(UUID.randomUUID());

        when(insuranceRepository.save(any())).thenReturn(policy);
        when(fraudService.processPolicy(any())).thenReturn(false);

        InsurancePolicyResponse response = insuranceService.createInsurancePolicy(request);

        assertNotNull(response);
        assertEquals(policy.getCustomerId(), response.getCustomerId());
        assertEquals(InsurancePolicyStatus.REJECTED, response.getStatus());
        verify(eventProducer, atLeastOnce()).sendOrderEvent(any());
    }

    @Test
    void shouldHandleEventProducerExceptionGracefully() {
        InsurancePolicyRequest request = buildRequest();
        InsurancePolicy policy = request.toEntity();
        policy.setId(UUID.randomUUID());

        when(insuranceRepository.save(any())).thenReturn(policy);
        when(fraudService.processPolicy(any())).thenReturn(true);
        doThrow(new RuntimeException("SNS failure")).when(eventProducer).sendOrderEvent(any());

        assertDoesNotThrow(() -> insuranceService.createInsurancePolicy(request));
        verify(eventProducer, atLeastOnce()).sendOrderEvent(any());
    }

    @Test
    void shouldFindPolicyById() {
        UUID policyId = UUID.randomUUID();
        InsurancePolicy policy = buildRequest().toEntity();
        policy.setId(policyId);

        when(insuranceRepository.findById(policyId)).thenReturn(Optional.of(policy));

        Optional<InsurancePolicyResponse> response = insuranceService.findByPolicyId(policyId);

        assertTrue(response.isPresent());
        assertEquals(policyId, response.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenPolicyNotFoundById() {
        UUID policyId = UUID.randomUUID();
        when(insuranceRepository.findById(policyId)).thenReturn(Optional.empty());

        Optional<InsurancePolicyResponse> response = insuranceService.findByPolicyId(policyId);

        assertFalse(response.isPresent());
    }

    @Test
    void shouldFindPoliciesByCustomerId() {
        UUID customerId = UUID.randomUUID();
        InsurancePolicy policy1 = buildRequest().toEntity();
        policy1.setId(UUID.randomUUID());
        policy1.setCustomerId(customerId);

        InsurancePolicy policy2 = buildRequest().toEntity();
        policy2.setId(UUID.randomUUID());
        policy2.setCustomerId(customerId);

        when(insuranceRepository.findByCustomerId(customerId)).thenReturn(List.of(policy1, policy2));

        List<InsurancePolicyResponse> responses = insuranceService.findByCustomerId(customerId);

        assertEquals(2, responses.size());
        assertTrue(responses.stream().allMatch(r -> r.getCustomerId().equals(customerId)));
    }

    @Test
    void shouldReturnEmptyListWhenNoPoliciesForCustomer() {
        UUID customerId = UUID.randomUUID();
        when(insuranceRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        List<InsurancePolicyResponse> responses = insuranceService.findByCustomerId(customerId);

        assertTrue(responses.isEmpty());
    }
}