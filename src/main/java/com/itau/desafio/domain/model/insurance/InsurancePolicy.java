package com.itau.desafio.domain.model.insurance;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Data
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
    private Double totalMonthlyPremiumAmount;
    private Double insuredAmount;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, BigDecimal> coverages;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> assistances;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InsurancePolicyHistory> history;

    public void addStatusHistory(InsurancePolicyStatus status) {
        this.history.add(new InsurancePolicyHistory(status, LocalDateTime.now()));
    }
}






