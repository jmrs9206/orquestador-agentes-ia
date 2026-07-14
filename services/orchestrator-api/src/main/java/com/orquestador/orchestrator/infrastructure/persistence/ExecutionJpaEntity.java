package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionJpaEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "task_id", nullable = false, length = 36)
    private String taskId;

    @Column(name = "agent_id", nullable = false, length = 36)
    private String agentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ExecutionStatus status;

    @Column(name = "command_line", nullable = false)
    private String commandLine;

    @Column(name = "log_file_path", nullable = false)
    private String logFilePath;

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public static ExecutionJpaEntity fromDomain(Execution domain) {
        if (domain == null) return null;
        return new ExecutionJpaEntity(
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

    public Execution toDomain() {
        return new Execution(
                this.id,
                this.taskId,
                this.agentId,
                this.status,
                this.commandLine,
                this.logFilePath,
                this.exitCode,
                this.startedAt,
                this.finishedAt
        );
    }
}
