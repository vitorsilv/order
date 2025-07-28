package com.itau.desafio.domain.model.insurance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class InsurancePolicyRequest {
    @NotNull(message = "customerId é obrigatório")
    private UUID customerId;
    @NotNull(message = "productId é obrigatório")
    private UUID productId;
    @NotNull(message = "category é obrigatória")
    private InsurancePolicyCategory category;
    @NotNull(message = "salesChannel é obrigatório")
    @JsonProperty(value = "salesChannel")
    private SalesChannel salesChannel;
    @NotNull(message = "paymentMethod é obrigatório")
    @JsonProperty(value = "paymentMethod")
    private PaymentMethod paymentMethod;
    @NotNull(message = "totalMonthlyPremiumAmount é obrigatório")
    @Positive(message = "totalMonthlyPremiumAmount deve ser positivo")
    private BigDecimal totalMonthlyPremiumAmount;
    @NotNull(message = "insuredAmount é obrigatório")
    @PositiveOrZero(message = "insuredAmount não pode ser negativo")
    private BigDecimal insuredAmount;
    @NotEmpty(message = "Pelo menos uma cobertura é obrigatória")
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;

    public InsurancePolicy toEntity() {
        return new InsurancePolicy(customerId, productId, category, salesChannel, paymentMethod, totalMonthlyPremiumAmount, insuredAmount, coverages, assistances);
    }
}






