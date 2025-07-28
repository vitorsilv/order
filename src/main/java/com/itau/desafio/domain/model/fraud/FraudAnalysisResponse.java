package com.itau.desafio.domain.model.fraud;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record FraudAnalysisResponse(
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("customerId") UUID customerId,
        @JsonProperty("analyzedAt") String analyzedAt,
        @JsonProperty("classification") FraudClassification classification,
        @JsonProperty("occurrences") List<Occurrence> occurrences
) {
    public record Occurrence(
            @JsonProperty("id") UUID id,
            @JsonProperty("productId") Long productId,
            @JsonProperty("type") OccurrenceType type,
            @JsonProperty("description") String description,
            @JsonProperty("createdAt") String createdAt,
            @JsonProperty("updatedAt") String updatedAt
    ) {
        enum OccurrenceType{
            FRAUD, SUSPICION, WARNING
        }
    }
}

