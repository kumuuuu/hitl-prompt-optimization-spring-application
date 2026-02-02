package com.kumuditha.hitl.service;

import com.kumuditha.hitl.dto.ml.AmbiguityRequest;
import com.kumuditha.hitl.dto.ml.AmbiguityResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AmbiguityAnalysisService {

    private static final String ML_URL =
            "https://babyrider-hitl-prompt-optimization-api.hf.space/analyze/";

    private final RestTemplate restTemplate = new RestTemplate();

    public AmbiguityResponse analyze(String text) {

        AmbiguityRequest request = new AmbiguityRequest(text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AmbiguityRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<AmbiguityResponse> response =
                restTemplate.postForEntity(
                        ML_URL,
                        entity,
                        AmbiguityResponse.class
                );

        return response.getBody();
    }
}
