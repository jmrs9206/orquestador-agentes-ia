package com.orquestador.orchestrator.api.dtos;

import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {
    private String id;
    private String projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private String assigneeId;
    private String reviewerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse fromDomain(Task domain) {
        if (domain == null) return null;
        return new TaskResponse(
                domain.getId(),
                domain.getProjectId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getAssigneeId(),
                domain.getReviewerId(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
