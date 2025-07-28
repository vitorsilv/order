package com.itau.desafio.infrastructure.rest;

import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import com.itau.desafio.domain.model.insurance.InsurancePolicyRequest;
import com.itau.desafio.domain.model.insurance.InsurancePolicyResponse;
import com.itau.desafio.domain.service.InsuranceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(insuranceService.findByPolicyId(policyId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InsurancePolicyResponse>> getByCustomerId(
            @PathVariable UUID customerId) {
        log.info("{} Consultando apólices por cliente: {}", LOG_PREFIX, customerId);
        return ResponseEntity.ok(insuranceService.findByCustomerId(customerId));
    }

}