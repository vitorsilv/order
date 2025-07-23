package com.itau.desafio.application;

import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
public class InsurancePolicy  {
    private UUID id;
    private UUID customerId;
    private UUID productId;
    private InsurancePolicyCategory category;
    private SalesChannel salesChannel;
    private PaymentMethod paymentMethod;
    private InsurancePolicyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private Double totalMonthlyPremiumAmount;
    private Double insuredAmount;
    private Map<String, BigDecimal> coverages;
    private List<String> assistances;
    private InsurancePolicyHistory history;
}
enum InsurancePolicyCategory {
    LIFE, AUTO, PROPERTY, BUSINESS
}
enum SalesChannel {
    MOBILE, WHATSAPP, WEB_SITE
}
enum PaymentMethod {
    CREDIT_CARD, ACCOUNT_DEBIT, BOLETO, PIX
}
enum InsurancePolicyStatus {
    RECEIVED, VALIDATED, PENDING, REJECTED, APPROVED, CANCELLED
}

class InsurancePolicyHistory {
    private InsurancePolicyStatus status;
    private LocalDateTime timestamp;
}
