package com.orquestador.orchestrator.api.dtos;

import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExecutionResponse {
    private String id;
    private String taskId;
    private String agentId;
    private ExecutionStatus status;
    private String commandLine;
    private String logFilePath;
    private Integer exitCode;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static ExecutionResponse fromDomain(Execution domain) {
        if (domain == null) return null;
        return new ExecutionResponse(
                domain.getId(),
                domain.getTaskId(),
                domain.getAgentId(),
                domain.getStatus(),
                domain.getCommandLine(),
                domain.getLogFilePath(),
                domain.getExitCode(),
                domain.getStartedAt(),
                domain.getFinishedAt()
        );
    }
}
