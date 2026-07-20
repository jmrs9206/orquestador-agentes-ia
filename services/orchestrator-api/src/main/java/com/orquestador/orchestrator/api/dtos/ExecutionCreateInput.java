package com.orquestador.orchestrator.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionCreateInput {
    @NotBlank(message = "El ID del agente es requerido")
    private String agentId;

    @NotBlank(message = "El comando CLI a ejecutar es requerido")
    private String commandLine;
}
