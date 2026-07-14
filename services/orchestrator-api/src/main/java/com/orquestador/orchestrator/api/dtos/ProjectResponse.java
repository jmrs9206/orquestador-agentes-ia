package com.orquestador.orchestrator.api.dtos;

import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProjectResponse {
    private String id;
    private String key;
    private String name;
    private String description;
    private ProjectStatus status;
    private String repositoryPath;
    private String defaultBranch;
    private String contextPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    public static ProjectResponse fromDomain(Project domain) {
        if (domain == null) return null;
        return new ProjectResponse(
                domain.getId(),
                domain.getKey(),
                domain.getName(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getRepositoryPath(),
                domain.getDefaultBranch(),
                domain.getContextPath(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getArchivedAt()
        );
    }
}
