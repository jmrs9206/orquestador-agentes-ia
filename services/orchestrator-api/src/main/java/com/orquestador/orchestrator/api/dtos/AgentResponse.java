package com.orquestador.orchestrator.api.dtos;

import com.orquestador.orchestrator.domain.Agent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgentResponse {
    private String id;
    private String name;
    private String role;
    private String systemPrompt;
    private String modelName;
    private double temperature;

    public static AgentResponse fromDomain(Agent domain) {
        if (domain == null) return null;
        return new AgentResponse(
                domain.getId(),
                domain.getName(),
                domain.getRole(),
                domain.getSystemPrompt(),
                domain.getModelName(),
                domain.getTemperature()
        );
    }
}
