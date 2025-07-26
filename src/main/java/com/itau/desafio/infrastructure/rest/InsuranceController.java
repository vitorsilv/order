package com.itau.desafio.infrastructure.rest;

import com.itau.desafio.domain.model.insurance.InsurancePolicyRequest;
import com.itau.desafio.domain.model.insurance.InsurancePolicyResponse;
import com.itau.desafio.domain.service.InsuranceService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insurance")
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceController {
    private InsuranceService insuranceService;

    @PostMapping
    public ResponseEntity<InsurancePolicyResponse> insurancePolicy(
            @RequestBody InsurancePolicyRequest request
    ) {
        return ResponseEntity.ok(insuranceService.createInsurancePolicy(request));
    }

}