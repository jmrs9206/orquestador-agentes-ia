package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskJpaEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "assignee_id", length = 36)
    private String assigneeId;

    @Column(name = "reviewer_id", length = 36)
    private String reviewerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static TaskJpaEntity fromDomain(Task domain) {
        if (domain == null) return null;
        return new TaskJpaEntity(
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

    public Task toDomain() {
        return new Task(
                this.id,
                this.projectId,
                this.title,
                this.description,
                this.status,
                this.assigneeId,
                this.reviewerId,
                this.createdAt,
                this.updatedAt
        );
    }
}
