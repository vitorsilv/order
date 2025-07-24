package com.itau.desafio.infrastructure.rest;

import com.itau.desafio.domain.service.InsuranceService;
import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceController {
    private InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsurancePolicy> createPolicy(
            @RequestBody InsurancePolicy request
    ) {
        return ResponseEntity.ok(insuranceService.createPolicy(request));
    }

}