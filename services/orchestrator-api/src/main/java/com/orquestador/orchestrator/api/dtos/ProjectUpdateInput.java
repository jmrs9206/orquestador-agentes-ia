package com.orquestador.orchestrator.api.dtos;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectUpdateInput {

    @Size(max = 100, message = "El nombre del proyecto no puede superar los 100 caracteres")
    private String name;

    private String description;
    private String defaultBranch;
}
