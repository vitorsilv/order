package com.itau.desafio.domain.model.insurance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonSerialize
public class InsurancePolicyResponse {
    private UUID id;
    private UUID customerId;
    private UUID productId;
    private InsurancePolicyCategory category;
    @JsonProperty(value = "salesChannel")
    private SalesChannel salesChannel;
    @JsonProperty(value = "paymentMethod")
    private PaymentMethod paymentMethod;
    private InsurancePolicyStatus status;
    @JsonProperty(value = "createdAt")
    private LocalDateTime createdAt;
    @JsonProperty(value = "finishedAt")
    private LocalDateTime finishedAt;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;
    private List<InsurancePolicyHistory> history;
}






