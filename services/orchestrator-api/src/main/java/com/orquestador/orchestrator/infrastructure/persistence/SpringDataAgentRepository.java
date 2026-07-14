package com.orquestador.orchestrator.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAgentRepository extends JpaRepository<AgentJpaEntity, String> {
}
