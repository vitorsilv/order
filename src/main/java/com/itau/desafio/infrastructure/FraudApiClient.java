package com.itau.desafio.infrastructure;

import com.itau.desafio.domain.model.fraud.FraudAnalysisRequest;
import com.itau.desafio.domain.model.fraud.FraudAnalysisResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "fraud-api",
        url = "${fraud.api.url}"
)
public interface FraudApiClient {

    @PostMapping("/fraud-analysis")
    FraudAnalysisResponse analyzePolicy(@RequestBody FraudAnalysisRequest request);
}