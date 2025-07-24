package com.itau.desafio.domain.model.insurance;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
public class InsurancePolicyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private InsurancePolicyStatus status;
    private LocalDateTime timestamp;

    public InsurancePolicyHistory(InsurancePolicyStatus status, LocalDateTime timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }
}