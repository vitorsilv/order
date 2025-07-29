package com.itau.desafio.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class HealthCheckControllerTest {

    @Test
    void shouldReturnOkWhenHealthIsUp() {
        HealthCheckController controller = new HealthCheckController();
        ResponseEntity<Health> response = controller.healthCheck();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(Status.UP, response.getBody().getStatus());
        assertEquals("insurance-service", response.getBody().getDetails().get("service"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenHealthIsDown() {
        HealthCheckController controller = new HealthCheckController() {
            @Override
            protected Health check() {
                return Health.down()
                        .withDetail("service", "insurance-service")
                        .withDetail("error", "Simulated error")
                        .build();
            }
        };
        ResponseEntity<Health> response = controller.healthCheck();

        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(Status.DOWN, response.getBody().getStatus());
        assertEquals("insurance-service", response.getBody().getDetails().get("service"));
        assertEquals("Simulated error", response.getBody().getDetails().get("error"));
    }

    @Test
    void shouldReturnUpOnLiveness() {
        HealthCheckController controller = new HealthCheckController();
        Health health = controller.liveness();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void shouldReturnCheckOnReadiness() {
        HealthCheckController controller = new HealthCheckController();
        Health health = controller.readiness();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("insurance-service", health.getDetails().get("service"));
    }
}