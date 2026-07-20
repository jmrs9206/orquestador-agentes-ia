package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskRepositoryAdapter implements TaskRepository {

    private final SpringDataTaskRepository springDataRepository;

    public TaskRepositoryAdapter(SpringDataTaskRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = TaskJpaEntity.fromDomain(task);
        TaskJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Task> findById(String id) {
        return springDataRepository.findById(id).map(TaskJpaEntity::toDomain);
    }

    @Override
    public List<Task> findByProjectId(String projectId) {
        return springDataRepository.findByProjectId(projectId).stream().map(TaskJpaEntity::toDomain).collect(Collectors.toList());
    }
}
