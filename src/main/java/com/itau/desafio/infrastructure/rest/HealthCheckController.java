package com.itau.desafio.infrastructure.rest;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController implements HealthIndicator {

    @GetMapping
    public ResponseEntity<Health> healthCheck() {
        Health health = check();
        return health.getStatus().equals(Status.UP) ?
                ResponseEntity.ok(health) :
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
    }

    @GetMapping("/liveness")
    public Health liveness() {
        return Health.up().build();
    }

    @GetMapping("/readiness")
    public Health readiness() {
        return check();
    }

    @Override
    public Health health() {
        return check();
    }

    protected Health check() {
        try {
            return Health.up()
                    .withDetail("service", "insurance-service")
                    .withDetail("status", "operational")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "insurance-service")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}