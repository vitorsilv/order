package com.itau.desafio.domain.model.fraud;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record FraudAnalysisRequest(
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("customerId") UUID customerId
) {}
