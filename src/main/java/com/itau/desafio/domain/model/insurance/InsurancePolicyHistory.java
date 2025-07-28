package com.itau.desafio.domain.model.insurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InsurancePolicyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Enumerated(EnumType.STRING)
    private InsurancePolicyStatus status;
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_policy_id", nullable = false)
    @JsonIgnore
    private InsurancePolicy insurancePolicy;

    public InsurancePolicyHistory(InsurancePolicyStatus status, LocalDateTime timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }
}