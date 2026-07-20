package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExecutionRepositoryAdapter implements ExecutionRepository {

    private final SpringDataExecutionRepository springDataRepository;

    public ExecutionRepositoryAdapter(SpringDataExecutionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Execution save(Execution execution) {
        ExecutionJpaEntity entity = ExecutionJpaEntity.fromDomain(execution);
        ExecutionJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Execution> findById(String id) {
        return springDataRepository.findById(id).map(ExecutionJpaEntity::toDomain);
    }

    @Override
    public List<Execution> findByTaskId(String taskId) {
        return springDataRepository.findByTaskId(taskId).stream().map(ExecutionJpaEntity::toDomain).collect(Collectors.toList());
    }
}
