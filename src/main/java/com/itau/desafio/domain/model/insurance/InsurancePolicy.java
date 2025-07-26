package com.itau.desafio.domain.model.insurance;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Getter
@Entity
public class InsurancePolicy  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID customerId;
    private UUID productId;
    @Enumerated(EnumType.STRING)
    private InsurancePolicyCategory category;
    @Enumerated(EnumType.STRING)
    private SalesChannel salesChannel;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private InsurancePolicyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private BigDecimal totalMonthlyPremiumAmount;
    private BigDecimal insuredAmount;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, BigDecimal> coverages;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> assistances;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InsurancePolicyHistory> history = Collections.emptyList();

    public InsurancePolicy(UUID customerId, UUID productId, InsurancePolicyCategory category, SalesChannel salesChannel, PaymentMethod paymentMethod,
     BigDecimal totalMonthlyPremiumAmount, BigDecimal insuredAmount, Map<String, BigDecimal> coverages, List<String> assistances) {
        this.customerId = customerId;
        this.productId = productId;
        this.category = category;
        this.salesChannel = salesChannel;
        this.paymentMethod = paymentMethod;
        this.totalMonthlyPremiumAmount =totalMonthlyPremiumAmount;
        this.insuredAmount = insuredAmount;
        this.coverages = coverages;
        this.assistances = assistances;
        this.addStatusHistory(InsurancePolicyStatus.RECEIVED);
    }

    public void addStatusHistory(InsurancePolicyStatus status) {
        this.history.add(new InsurancePolicyHistory(status, LocalDateTime.now()));
        this.status = status;
    }

    public InsurancePolicyResponse toResponse() {
        return InsurancePolicyResponse.builder()
                .id(this.id)
                .customerId(this.customerId)
                .productId(this.productId)
                .category(this.category)
                .salesChannel(this.salesChannel)
                .paymentMethod(this.paymentMethod)
                .status(this.status)
                .createdAt(this.createdAt)
                .finishedAt(this.finishedAt)
                .totalMonthlyPremiumAmount(this.totalMonthlyPremiumAmount)
                .insuredAmount(this.insuredAmount)
                .coverages(this.coverages)
                .assistances(this.assistances)
                .history(this.history)
                .build();
    }

}






