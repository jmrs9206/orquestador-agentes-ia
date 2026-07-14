package com.orquestador.orchestrator.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentCreateInput {
    @NotBlank(message = "El nombre del agente es requerido")
    private String name;

    @NotBlank(message = "El rol del agente es requerido")
    private String role;

    private String systemPrompt;

    @NotBlank(message = "El modelo de IA es requerido")
    private String modelName;

    private double temperature = 0.7;
}
