package com.orquestador.orchestrator.domain;

import java.util.List;
import java.util.Optional;

public interface ExecutionRepository {
    Execution save(Execution execution);
    Optional<Execution> findById(String id);
    List<Execution> findByTaskId(String taskId);
}
