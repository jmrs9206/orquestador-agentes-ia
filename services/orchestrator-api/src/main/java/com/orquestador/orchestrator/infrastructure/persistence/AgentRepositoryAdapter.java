package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.AgentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AgentRepositoryAdapter implements AgentRepository {

    private final SpringDataAgentRepository springDataRepository;

    public AgentRepositoryAdapter(SpringDataAgentRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Agent save(Agent agent) {
        AgentJpaEntity entity = AgentJpaEntity.fromDomain(agent);
        AgentJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Agent> findById(String id) {
        return springDataRepository.findById(id).map(AgentJpaEntity::toDomain);
    }

    @Override
    public List<Agent> findAll() {
        return springDataRepository.findAll().stream().map(AgentJpaEntity::toDomain).collect(Collectors.toList());
    }
}
