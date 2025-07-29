package com.itau.desafio.domain.model.insurance;

public enum InsurancePolicyStatus {
    RECEIVED, VALIDATED, PENDING, REJECTED, APPROVED, CANCELED;
    public boolean canTransitionTo(InsurancePolicyStatus next) {
        return switch (this) {
            case RECEIVED -> next == VALIDATED || next == REJECTED|| next == CANCELED;
            case VALIDATED -> next == PENDING;
            case PENDING -> next == APPROVED || next == REJECTED || next == CANCELED || next == PENDING;
            case APPROVED, CANCELED, REJECTED -> false;
        };
    }
}