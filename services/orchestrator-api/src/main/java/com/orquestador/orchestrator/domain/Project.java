package com.orquestador.orchestrator.domain;

import java.time.LocalDateTime;

public class Project {
    private final String id;
    private final String key;
    private final String name;
    private final String description;
    private final ProjectStatus status;
    private final String repositoryPath;
    private final String defaultBranch;
    private final String contextPath;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime archivedAt;

    public Project(String id, String key, String name, String description, ProjectStatus status,
                   String repositoryPath, String defaultBranch, String contextPath,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime archivedAt) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.description = description;
        this.status = status;
        this.repositoryPath = repositoryPath;
        this.defaultBranch = defaultBranch;
        this.contextPath = contextPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archivedAt = archivedAt;
    }

    public String getId() { return id; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProjectStatus getStatus() { return status; }
    public String getRepositoryPath() { return repositoryPath; }
    public String getDefaultBranch() { return defaultBranch; }
    public String getContextPath() { return contextPath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getArchivedAt() { return archivedAt; }
}
