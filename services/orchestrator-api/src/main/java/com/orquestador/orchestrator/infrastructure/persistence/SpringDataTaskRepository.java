package com.orquestador.orchestrator.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataTaskRepository extends JpaRepository<TaskJpaEntity, String> {
    List<TaskJpaEntity> findByProjectId(String projectId);
}
