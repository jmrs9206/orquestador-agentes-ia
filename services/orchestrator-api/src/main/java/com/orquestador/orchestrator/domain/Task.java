package com.orquestador.orchestrator.domain;

import java.time.LocalDateTime;

public class Task {
    private final String id;
    private final String projectId;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final String assigneeId;
    private final String reviewerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Task(String id, String projectId, String title, String description, TaskStatus status,
                String assigneeId, String reviewerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
        this.reviewerId = reviewerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public String getAssigneeId() { return assigneeId; }
    public String getReviewerId() { return reviewerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
