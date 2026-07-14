package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectRepository;
import com.orquestador.orchestrator.domain.ProjectStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final SpringDataProjectRepository springDataRepository;

    public ProjectRepositoryAdapter(SpringDataProjectRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectJpaEntity entity = ProjectJpaEntity.fromDomain(project);
        ProjectJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Project> findById(String id) {
        return springDataRepository.findById(id).map(ProjectJpaEntity::toDomain);
    }

    @Override
    public Optional<Project> findByKey(String key) {
        return springDataRepository.findByKey(key).map(ProjectJpaEntity::toDomain);
    }

    @Override
    public List<Project> findAll(boolean includeArchived) {
        List<ProjectJpaEntity> entities;
        if (includeArchived) {
            entities = springDataRepository.findAll();
        } else {
            entities = springDataRepository.findByStatusNot(ProjectStatus.ARCHIVED);
        }
        return entities.stream().map(ProjectJpaEntity::toDomain).collect(Collectors.toList());
    }
}
