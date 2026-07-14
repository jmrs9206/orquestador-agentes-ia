package com.orquestador.orchestrator.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateInput {

    @NotBlank(message = "La clave (key) es requerida")
    @Pattern(regexp = "^[a-z0-9-_]+$", message = "La clave (key) debe ser alfanumérica en minúsculas, guiones o guiones bajos")
    @Size(max = 50, message = "La clave (key) no puede superar los 50 caracteres")
    private String key;

    @NotBlank(message = "El nombre del proyecto es requerido")
    @Size(max = 100, message = "El nombre del proyecto no puede superar los 100 caracteres")
    private String name;

    private String description;

    @NotBlank(message = "La ruta del repositorio es requerida")
    private String repositoryPath;

    private String defaultBranch;
    private String contextPath;
}
