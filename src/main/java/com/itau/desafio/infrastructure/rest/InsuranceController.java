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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    private final InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsurancePolicyResponse> insurancePolicy(
            @Valid @RequestBody InsurancePolicyRequest request
    ) {
        log.info("");
        return ResponseEntity.ok(insuranceService.createInsurancePolicy(request));
    }

}