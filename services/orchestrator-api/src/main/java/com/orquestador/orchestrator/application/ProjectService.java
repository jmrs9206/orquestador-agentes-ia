package com.orquestador.orchestrator.application;

import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectRepository;
import com.orquestador.orchestrator.domain.ProjectStatus;
import com.orquestador.orchestrator.domain.exceptions.DuplicateProjectKeyException;
import com.orquestador.orchestrator.domain.exceptions.InvalidProjectStateException;
import com.orquestador.orchestrator.domain.exceptions.InvalidRepositoryPathException;
import com.orquestador.orchestrator.domain.exceptions.ProjectNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ProjectService {

    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-z0-9-_]+$");
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String key, String name, String description, String repositoryPath,
                                 String defaultBranch, String contextPath) {
        // Validate key pattern
        if (key == null || !KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("El formato del identificador (key) es inválido. Solo se admiten letras minúsculas, números, guiones y guiones bajos.");
        }

        // Validate key uniqueness
        if (projectRepository.findByKey(key).isPresent()) {
            throw new DuplicateProjectKeyException("Ya existe un proyecto registrado con la clave (key): " + key);
        }

        // Validate repository path existence
        if (repositoryPath == null || repositoryPath.isBlank()) {
            throw new InvalidRepositoryPathException("La ruta del repositorio no puede estar vacía.");
        }
        File repoDir = new File(repositoryPath);
        if (!repoDir.exists() || !repoDir.isDirectory()) {
            throw new InvalidRepositoryPathException("La ruta especificada no existe o no es un directorio válido: " + repositoryPath);
        }

        String actualDefaultBranch = (defaultBranch == null || defaultBranch.isBlank()) ? "main" : defaultBranch;
        String actualContextPath = (contextPath == null || contextPath.isBlank()) ? ".ai" : contextPath;

        Project project = new Project(
                UUID.randomUUID().toString(),
                key,
                name,
                description,
                ProjectStatus.DRAFT,
                repositoryPath,
                actualDefaultBranch,
                actualContextPath,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        return projectRepository.save(project);
    }

    public Project getProjectById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con el ID: " + id));
    }

    public List<Project> listProjects(boolean includeArchived) {
        return projectRepository.findAll(includeArchived);
    }

    public Project updateProject(String id, String name, String description, String defaultBranch) {
        Project existing = getProjectById(id);

        String updatedName = (name == null || name.isBlank()) ? existing.getName() : name;
        String updatedDescription = (description == null) ? existing.getDescription() : description;
        String updatedBranch = (defaultBranch == null || defaultBranch.isBlank()) ? existing.getDefaultBranch() : defaultBranch;

        Project updated = new Project(
                existing.getId(),
                existing.getKey(),
                updatedName,
                updatedDescription,
                existing.getStatus(),
                existing.getRepositoryPath(),
                updatedBranch,
                existing.getContextPath(),
                existing.getCreatedAt(),
                LocalDateTime.now(),
                existing.getArchivedAt()
        );

        return projectRepository.save(updated);
    }

    public Project archiveProject(String id) {
        Project existing = getProjectById(id);

        if (existing.getStatus() == ProjectStatus.ARCHIVED) {
            throw new InvalidProjectStateException("El proyecto ya se encuentra archivado.");
        }

        Project archived = new Project(
                existing.getId(),
                existing.getKey(),
                existing.getName(),
                existing.getDescription(),
                ProjectStatus.ARCHIVED,
                existing.getRepositoryPath(),
                existing.getDefaultBranch(),
                existing.getContextPath(),
                existing.getCreatedAt(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return projectRepository.save(archived);
    }

    public Project changeStatus(String id, ProjectStatus newStatus) {
        Project existing = getProjectById(id);
        validateTransition(existing.getStatus(), newStatus);

        LocalDateTime archivedAt = (newStatus == ProjectStatus.ARCHIVED) ? LocalDateTime.now() : existing.getArchivedAt();

        Project updated = new Project(
                existing.getId(),
                existing.getKey(),
                existing.getName(),
                existing.getDescription(),
                newStatus,
                existing.getRepositoryPath(),
                existing.getDefaultBranch(),
                existing.getContextPath(),
                existing.getCreatedAt(),
                LocalDateTime.now(),
                archivedAt
        );

        return projectRepository.save(updated);
    }

    private void validateTransition(ProjectStatus current, ProjectStatus target) {
        if (current == target) {
            return;
        }

        if (current == ProjectStatus.ARCHIVED) {
            throw new InvalidProjectStateException("No se permiten transiciones fuera del estado ARCHIVED.");
        }

        switch (current) {
            case DRAFT:
                if (target != ProjectStatus.ACTIVE && target != ProjectStatus.ARCHIVED) {
                    throw new InvalidProjectStateException("Desde DRAFT solo se permite transicionar a ACTIVE o ARCHIVED.");
                }
                break;
            case ACTIVE:
                if (target != ProjectStatus.PAUSED && target != ProjectStatus.BLOCKED && target != ProjectStatus.ARCHIVED) {
                    throw new InvalidProjectStateException("Desde ACTIVE solo se permite transicionar a PAUSED, BLOCKED o ARCHIVED.");
                }
                break;
            case PAUSED:
            case BLOCKED:
                if (target != ProjectStatus.ACTIVE && target != ProjectStatus.ARCHIVED) {
                    throw new InvalidProjectStateException("Desde " + current + " solo se permite transicionar a ACTIVE o ARCHIVED.");
                }
                break;
            default:
                throw new InvalidProjectStateException("Transición de estado desconocida.");
        }
    }
}
