package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJpaEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "`key`", unique = true, nullable = false, length = 50)
    private String key;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Column(name = "repository_path", nullable = false)
    private String repositoryPath;

    @Column(name = "default_branch", nullable = false, length = 50)
    private String defaultBranch;

    @Column(name = "context_path", nullable = false)
    private String contextPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public static ProjectJpaEntity fromDomain(Project domain) {
        if (domain == null) return null;
        return new ProjectJpaEntity(
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

    public Project toDomain() {
        return new Project(
                this.id,
                this.key,
                this.name,
                this.description,
                this.status,
                this.repositoryPath,
                this.defaultBranch,
                this.contextPath,
                this.createdAt,
                this.updatedAt,
                this.archivedAt
        );
    }
}
