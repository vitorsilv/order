package com.itau.desafio.infrastructure.rest;

import com.itau.desafio.application.PolicyService;
import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<InsurancePolicy> createPolicy(
            @RequestBody InsurancePolicy request
    ) {
//        return ResponseEntity.ok(policyService.createPolicy(request));
        return ResponseEntity.ok(new InsurancePolicy());
    }

}