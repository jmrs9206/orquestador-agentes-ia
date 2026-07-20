package com.orquestador.orchestrator.domain;

import java.time.LocalDateTime;

public class Execution {
    private final String id;
    private final String taskId;
    private final String agentId;
    private final ExecutionStatus status;
    private final String commandLine;
    private final String logFilePath;
    private final Integer exitCode;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public Execution(String id, String taskId, String agentId, ExecutionStatus status,
                     String commandLine, String logFilePath, Integer exitCode,
                     LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.id = id;
        this.taskId = taskId;
        this.agentId = agentId;
        this.status = status;
        this.commandLine = commandLine;
        this.logFilePath = logFilePath;
        this.exitCode = exitCode;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public String getId() { return id; }
    public String getTaskId() { return taskId; }
    public String getAgentId() { return agentId; }
    public ExecutionStatus getStatus() { return status; }
    public String getCommandLine() { return commandLine; }
    public String getLogFilePath() { return logFilePath; }
    public Integer getExitCode() { return exitCode; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
}
