package com.itau.desafio.domain.model.insurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class InsurancePolicy  {
    private static final String LOG_PREFIX = "[INSURANCE_POLICY]";
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "insurancePolicy", fetch = FetchType.LAZY)
    private List<InsurancePolicyHistory> history;

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
        this.status = InsurancePolicyStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
        this.history = new ArrayList<>();;
        this.addStatusHistory(InsurancePolicyStatus.RECEIVED);
    }

    public void changeStatus(InsurancePolicyStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            log.error("{} Transição de status inválida. Apólice: {}, Status atual: {}, Próximo status: {}",
                    LOG_PREFIX, this.id, this.status, nextStatus);
            throw new IllegalStateException("Transição de status inválida: " + this.status + " -> " + nextStatus);
        }
        this.addStatusHistory(nextStatus);
        log.info("{} Status alterado com sucesso. Apólice: {}, Novo status: {}", LOG_PREFIX, this.id, nextStatus);
    }

    private void addStatusHistory(InsurancePolicyStatus status) {
        InsurancePolicyHistory historyEntry = new InsurancePolicyHistory(status, LocalDateTime.now());
        historyEntry.setInsurancePolicy(this);
        this.history.add(historyEntry);
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






