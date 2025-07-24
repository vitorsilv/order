package com.itau.desafio.domain.repository;

import com.itau.desafio.domain.model.insurance.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InsuranceRepository extends JpaRepository <InsurancePolicy, UUID>{
    InsurancePolicy save(InsurancePolicy request);
    Optional<InsurancePolicy> findById(UUID id);
    List<InsurancePolicy> findByCustomerId(UUID customerId);
}