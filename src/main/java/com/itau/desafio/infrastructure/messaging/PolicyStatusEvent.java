package com.itau.desafio.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itau.desafio.domain.model.insurance.InsurancePolicyStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@JsonSerialize
public class PolicyStatusEvent {
    private final UUID policyId;
    private final InsurancePolicyStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
}
