package com.itau.desafio.domain.model.insurance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@AllArgsConstructor
@NoArgsConstructor
public class InsurancePolicyRequest {
    private UUID customerId;
    private UUID productId;
    private InsurancePolicyCategory category;
    @JsonProperty(value = "salesChannel")
    private SalesChannel salesChannel;
    @JsonProperty(value = "paymentMethod")
    private PaymentMethod paymentMethod;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;

    public InsurancePolicy toEntity() {
        return new InsurancePolicy(customerId, productId, category, salesChannel, paymentMethod, totalMonthlyPremiumAmount, insuredAmount, coverages, assistances);
    }
}






