package com.orquestador.orchestrator.infrastructure.ai;

import com.orquestador.orchestrator.infrastructure.ai.dto.GeminiRequest;
import com.orquestador.orchestrator.infrastructure.ai.dto.GeminiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class GeminiApiClientTest {

    private GeminiConfig config;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private GeminiApiClient client;

    @BeforeEach
    void setUp() {
        config = new GeminiConfig();
        config.setApiKey("test-api-key-12345");
        config.setBaseUrl("https://generativelanguage.googleapis.com");
        config.setDefaultModel("gemini-1.5-flash");

        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        client = new GeminiApiClient(config, restTemplate);
    }

    @Test
    void shouldThrowExceptionWhenApiKeyIsMissing() {
        config.setApiKey(null);

        GeminiRequest request = new GeminiRequest(
                new GeminiRequest.SystemInstruction("System instruction"),
                Collections.singletonList(new GeminiRequest.Content("user", "Hello Gemini")),
                new GeminiRequest.GenerationConfig(0.5)
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> client.generateContent(request, null));
        assertTrue(ex.getMessage().contains("GEMINI_API_KEY"));
    }

    @Test
    void shouldPostToGeminiEndpointAndParseResponseSuccessfully() {
        String jsonResponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "Task completed successfully with Gemini API." }
                        ],
                        "role": "model"
                      },
                      "finishReason": "STOP"
                    }
                  ],
                  "usageMetadata": {
                    "promptTokenCount": 15,
                    "candidatesTokenCount": 8,
                    "totalTokenCount": 23
                  }
                }
                """;

        mockServer.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=test-api-key-12345"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        GeminiRequest request = new GeminiRequest(
                new GeminiRequest.SystemInstruction("System prompt"),
                Collections.singletonList(new GeminiRequest.Content("user", "Execute task")),
                new GeminiRequest.GenerationConfig(0.2)
        );

        GeminiResponse response = client.generateContent(request, null);

        assertNotNull(response);
        assertEquals("Task completed successfully with Gemini API.", response.extractResponseText());
        assertNotNull(response.getUsageMetadata());
        assertEquals(23, response.getUsageMetadata().getTotalTokenCount());

        mockServer.verify();
    }
}
