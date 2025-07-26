package com.itau.desafio.domain.model.fraud;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FraudAnalysisResponse(
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("customerId") UUID customerId,
        @JsonProperty("analyzedAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        String analyzedAt,
        @JsonProperty("classification") FraudClassification classification,
        @JsonProperty("occurrences") List<Occurrence> occurrences
) {
    public record Occurrence(
            @JsonProperty("id") UUID id,
            @JsonProperty("productId") Long productId,
            @JsonProperty("type") OccurrenceType type,
            @JsonProperty("description") String description,
            @JsonProperty("createdAt")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
            LocalDateTime createdAt,
            @JsonProperty("updatedAt")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
            LocalDateTime updatedAt
    ) {
        enum OccurrenceType{
            FRAUD, SUSPICION, WARNING
        }
    }
}

