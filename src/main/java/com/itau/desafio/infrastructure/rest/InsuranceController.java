package com.itau.desafio.infrastructure.rest;

import com.itau.desafio.domain.model.insurance.InsurancePolicyRequest;
import com.itau.desafio.domain.model.insurance.InsurancePolicyResponse;
import com.itau.desafio.service.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    private static final String LOG_PREFIX = "[InsuranceController]";

    private final InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsurancePolicyResponse> createInsurancePolicy(
            @Valid @RequestBody InsurancePolicyRequest request) {
        log.info("{} Nova solicitação de apólice para cliente: {}", LOG_PREFIX, request.getCustomerId());
        return ResponseEntity.ok(insuranceService.createInsurancePolicy(request));
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<InsurancePolicyResponse> getByPolicyId(
            @PathVariable UUID policyId) {
        log.info("{} Consultando apólice por ID: {}", LOG_PREFIX, policyId);
        InsurancePolicyResponse response = insuranceService.findByPolicyId(policyId)
                .orElseThrow(() -> {
                    log.warn("{} Apólice não encontrada com ID: {}", LOG_PREFIX, policyId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Apólice não encontrada com ID: " + policyId
                    );
                });
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InsurancePolicyResponse>> getByCustomerId(
            @PathVariable UUID customerId) {
        log.info("{} Consultando apólices por cliente: {}", LOG_PREFIX, customerId);
        List<InsurancePolicyResponse> policies = insuranceService.findByCustomerId(customerId);

        if (policies.isEmpty()) {
            log.warn("{} Nenhuma apólice encontrada para o cliente: {}", LOG_PREFIX, customerId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhuma apólice encontrada para o cliente: " + customerId
            );
        }

        return ResponseEntity.ok(policies);
    }

}