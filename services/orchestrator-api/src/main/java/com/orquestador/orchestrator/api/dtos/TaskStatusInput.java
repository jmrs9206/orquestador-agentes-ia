package com.orquestador.orchestrator.api.dtos;

import com.orquestador.orchestrator.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusInput {
    @NotNull(message = "El estado de la tarea es requerido")
    private TaskStatus status;
}
