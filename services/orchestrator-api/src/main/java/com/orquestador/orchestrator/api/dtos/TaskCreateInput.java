package com.orquestador.orchestrator.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateInput {
    @NotBlank(message = "El título de la tarea es requerido")
    private String title;

    private String description;
    private String assigneeId;
    private String reviewerId;
}
