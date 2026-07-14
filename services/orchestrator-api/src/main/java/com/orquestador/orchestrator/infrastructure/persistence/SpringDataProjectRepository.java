package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataProjectRepository extends JpaRepository<ProjectJpaEntity, String> {
    Optional<ProjectJpaEntity> findByKey(String key);
    List<ProjectJpaEntity> findByStatusNot(ProjectStatus status);
}
