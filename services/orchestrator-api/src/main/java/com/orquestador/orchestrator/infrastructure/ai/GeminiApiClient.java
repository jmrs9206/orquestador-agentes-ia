package com.orquestador.orchestrator.infrastructure.ai;

import com.orquestador.orchestrator.infrastructure.ai.dto.GeminiRequest;
import com.orquestador.orchestrator.infrastructure.ai.dto.GeminiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeminiApiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiApiClient.class);

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;

    public GeminiApiClient(GeminiConfig geminiConfig, RestTemplate geminiRestTemplate) {
        this.geminiConfig = geminiConfig;
        this.restTemplate = geminiRestTemplate;
    }

    public GeminiResponse generateContent(GeminiRequest request, String overrideModel) {
        String apiKey = geminiConfig.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY environment variable or property is not set or empty.");
        }

        String model = (overrideModel != null && !overrideModel.isBlank()) ? overrideModel : geminiConfig.getDefaultModel();
        String url = UriComponentsBuilder.fromHttpUrl(geminiConfig.getBaseUrl())
                .path("/v1beta/models/" + model + ":generateContent")
                .queryParam("key", apiKey)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

        log.info("Sending request to Gemini API model: {}", model);

        try {
            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(url, entity, GeminiResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully received Gemini response.");
                return response.getBody();
            } else {
                throw new RuntimeException("Gemini API call failed with HTTP status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error communicating with Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }
}
