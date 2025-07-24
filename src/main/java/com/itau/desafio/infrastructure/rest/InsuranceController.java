package com.itau.desafio.infrastructure.rest;

import com.itau.desafio.application.PolicyService;
import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policies")
@AllArgsConstructor
@NoArgsConstructor
public class PolicyController {
    private PolicyService policyService;

    @PostMapping
    public ResponseEntity<InsurancePolicy> createPolicy(
            @RequestBody InsurancePolicy request
    ) {
        return ResponseEntity.ok(policyService.createPolicy(request));
    }

}