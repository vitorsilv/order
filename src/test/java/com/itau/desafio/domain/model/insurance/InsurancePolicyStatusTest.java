package com.itau.desafio.domain.model.insurance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InsurancePolicyStatusTest {

    @Test
    void testReceivedTransitions() {
        assertTrue(InsurancePolicyStatus.RECEIVED.canTransitionTo(InsurancePolicyStatus.VALIDATED));
        assertTrue(InsurancePolicyStatus.RECEIVED.canTransitionTo(InsurancePolicyStatus.REJECTED));
        assertTrue(InsurancePolicyStatus.RECEIVED.canTransitionTo(InsurancePolicyStatus.CANCELED));
        assertFalse(InsurancePolicyStatus.RECEIVED.canTransitionTo(InsurancePolicyStatus.PENDING));
        assertFalse(InsurancePolicyStatus.RECEIVED.canTransitionTo(InsurancePolicyStatus.APPROVED));
        assertFalse(InsurancePolicyStatus.RECEIVED.canTransitionTo(InsurancePolicyStatus.RECEIVED));
    }

    @Test
    void testValidatedTransitions() {
        assertTrue(InsurancePolicyStatus.VALIDATED.canTransitionTo(InsurancePolicyStatus.PENDING));
        assertFalse(InsurancePolicyStatus.VALIDATED.canTransitionTo(InsurancePolicyStatus.RECEIVED));
        assertFalse(InsurancePolicyStatus.VALIDATED.canTransitionTo(InsurancePolicyStatus.REJECTED));
        assertFalse(InsurancePolicyStatus.VALIDATED.canTransitionTo(InsurancePolicyStatus.APPROVED));
        assertFalse(InsurancePolicyStatus.VALIDATED.canTransitionTo(InsurancePolicyStatus.CANCELED));
        assertFalse(InsurancePolicyStatus.VALIDATED.canTransitionTo(InsurancePolicyStatus.VALIDATED));
    }

    @Test
    void testPendingTransitions() {
        assertTrue(InsurancePolicyStatus.PENDING.canTransitionTo(InsurancePolicyStatus.APPROVED));
        assertTrue(InsurancePolicyStatus.PENDING.canTransitionTo(InsurancePolicyStatus.REJECTED));
        assertTrue(InsurancePolicyStatus.PENDING.canTransitionTo(InsurancePolicyStatus.CANCELED));
        assertTrue(InsurancePolicyStatus.PENDING.canTransitionTo(InsurancePolicyStatus.PENDING));
        assertFalse(InsurancePolicyStatus.PENDING.canTransitionTo(InsurancePolicyStatus.RECEIVED));
        assertFalse(InsurancePolicyStatus.PENDING.canTransitionTo(InsurancePolicyStatus.VALIDATED));
    }

    @Test
    void testApprovedTransitions() {
        for (InsurancePolicyStatus next : InsurancePolicyStatus.values()) {
            assertFalse(InsurancePolicyStatus.APPROVED.canTransitionTo(next));
        }
    }

    @Test
    void testCanceledTransitions() {
        for (InsurancePolicyStatus next : InsurancePolicyStatus.values()) {
            assertFalse(InsurancePolicyStatus.CANCELED.canTransitionTo(next));
        }
    }

    @Test
    void testRejectedTransitions() {
        for (InsurancePolicyStatus next : InsurancePolicyStatus.values()) {
            assertFalse(InsurancePolicyStatus.REJECTED.canTransitionTo(next));
        }
    }
}