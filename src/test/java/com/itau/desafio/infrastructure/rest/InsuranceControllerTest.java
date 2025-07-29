package com.itau.desafio.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.desafio.domain.model.insurance.*;
import com.itau.desafio.repository.InsuranceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InsuranceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private InsurancePolicyRequest buildRequest(UUID customerId, BigDecimal insuredAmount) {
        return new InsurancePolicyRequest(
                customerId,
                UUID.randomUUID(),
                InsurancePolicyCategory.AUTO,
                SalesChannel.WEB_SITE,
                PaymentMethod.CREDIT_CARD,
                BigDecimal.valueOf(100.0),
                insuredAmount,
                Map.of("Cobertura", insuredAmount),
                List.of("Assistência 24h")
        );
    }

    @Test
    void shouldCreateApprovedPolicy() throws Exception {
        UUID customerId = UUID.randomUUID();
        InsurancePolicyRequest request = buildRequest(customerId, new BigDecimal("10000.00"));

        String responseJson = mockMvc.perform(post("/api/insurance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_id").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andReturn().getResponse().getContentAsString();

        InsurancePolicyResponse response = objectMapper.readValue(responseJson, InsurancePolicyResponse.class);
        assertThat(insuranceRepository.findById(response.getId())).isPresent();
    }

    @Test
    void shouldCreatePolicyRejectedByFraud() throws Exception {
        UUID customerId = UUID.randomUUID();
        InsurancePolicyRequest request = buildRequest(customerId, new BigDecimal("1000000.00"));

        String responseJson = mockMvc.perform(post("/api/insurance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_id").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andReturn().getResponse().getContentAsString();

        InsurancePolicyResponse response = objectMapper.readValue(responseJson, InsurancePolicyResponse.class);
        assertThat(insuranceRepository.findById(response.getId())).isPresent();
    }

    @Test
    void shouldFindPolicyByExistingId() throws Exception {
        InsurancePolicyRequest request = buildRequest(UUID.randomUUID(), new BigDecimal("10000.00"));
        String responseJson = mockMvc.perform(post("/api/insurance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        InsurancePolicyResponse response = objectMapper.readValue(responseJson, InsurancePolicyResponse.class);

        mockMvc.perform(get("/api/insurance/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()));
    }

    @Test
    void shouldReturn404ForNonExistingPolicy() throws Exception {
        mockMvc.perform(get("/api/insurance/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindPoliciesByCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        InsurancePolicyRequest request1 = buildRequest(customerId, new BigDecimal("10000.00"));
        InsurancePolicyRequest request2 = buildRequest(customerId, new BigDecimal("20000.00"));

        mockMvc.perform(post("/api/insurance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))).andReturn();
        mockMvc.perform(post("/api/insurance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))).andReturn();

        mockMvc.perform(get("/api/insurance/customer/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customer_id").value(customerId.toString()))
                .andExpect(jsonPath("$[1].customer_id").value(customerId.toString()));
    }

    @Test
    void shouldReturnEmptyListForCustomerWithoutPolicies() throws Exception {
        mockMvc.perform(get("/api/insurance/customer/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}