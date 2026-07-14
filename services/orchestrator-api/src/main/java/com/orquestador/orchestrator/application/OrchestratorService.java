package com.orquestador.orchestrator.application;

import com.orquestador.orchestrator.domain.*;
import com.orquestador.orchestrator.domain.exceptions.InvalidProjectStateException;
import com.orquestador.orchestrator.domain.exceptions.ProjectNotFoundException;
import com.orquestador.orchestrator.infrastructure.runner.CliRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrchestratorService {

    private final ProjectRepository projectRepository;
    private final AgentRepository agentRepository;
    private final TaskRepository taskRepository;
    private final ExecutionRepository executionRepository;
    private final CliRunner cliRunner;

    public OrchestratorService(ProjectRepository projectRepository, AgentRepository agentRepository,
                               TaskRepository taskRepository, ExecutionRepository executionRepository,
                               CliRunner cliRunner) {
        this.projectRepository = projectRepository;
        this.agentRepository = agentRepository;
        this.taskRepository = taskRepository;
        this.executionRepository = executionRepository;
        this.cliRunner = cliRunner;
    }

    // Agent operations
    public Agent createAgent(String name, String role, String systemPrompt, String modelName, double temperature) {
        Agent agent = new Agent(UUID.randomUUID().toString(), name, role, systemPrompt, modelName, temperature);
        return agentRepository.save(agent);
    }

    public List<Agent> listAgents() {
        return agentRepository.findAll();
    }

    public Agent getAgentById(String id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Agente no encontrado con ID: " + id));
    }

    // Task operations
    public Task createTask(String projectId, String title, String description, String assigneeId, String reviewerId) {
        // Validate project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));

        // Validate agents exist if assigned
        if (assigneeId != null) getAgentById(assigneeId);
        if (reviewerId != null) getAgentById(reviewerId);

        Task task = new Task(
                UUID.randomUUID().toString(),
                projectId,
                title,
                description,
                TaskStatus.DRAFT,
                assigneeId,
                reviewerId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return taskRepository.save(task);
    }

    public List<Task> listTasksByProject(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Tarea no encontrada con ID: " + id));
    }

    public Task changeTaskStatus(String id, TaskStatus targetStatus) {
        Task existing = getTaskById(id);

        if (targetStatus == TaskStatus.DONE) {
            // Apply Cross-Review rule: Assignee and reviewer cannot be the same agent
            if (existing.getAssigneeId() != null && existing.getAssigneeId().equals(existing.getReviewerId())) {
                throw new IllegalArgumentException("La validación cruzada ha fallado: Un agente no puede revisar y aprobar su propio trabajo asignado.");
            }
        }

        Task updated = new Task(
                existing.getId(),
                existing.getProjectId(),
                existing.getTitle(),
                existing.getDescription(),
                targetStatus,
                existing.getAssigneeId(),
                existing.getReviewerId(),
                existing.getCreatedAt(),
                LocalDateTime.now()
        );
        return taskRepository.save(updated);
    }

    // Execution operations
    public Execution triggerExecution(String taskId, String agentId, String commandLine) {
        Task task = getTaskById(taskId);
        Agent agent = getAgentById(agentId);
        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));

        String executionId = UUID.randomUUID().toString();
        String logFilePath = ".ai/logs/exec-" + executionId + ".log";

        boolean isRisky = isCommandLineRisky(commandLine);
        ExecutionStatus initialStatus = isRisky ? ExecutionStatus.WAITING_APPROVAL : ExecutionStatus.PENDING;

        Execution execution = new Execution(
                executionId,
                taskId,
                agentId,
                initialStatus,
                commandLine,
                logFilePath,
                null,
                LocalDateTime.now(),
                null
        );

        Execution saved = executionRepository.save(execution);

        // If not risky, trigger immediately
        if (!isRisky) {
            cliRunner.runAsync(saved, project.getRepositoryPath());
        }

        return saved;
    }

    public Execution getExecutionById(String id) {
        return executionRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Ejecución no encontrada con ID: " + id));
    }

    public List<Execution> listExecutionsByTask(String taskId) {
        return executionRepository.findByTaskId(taskId);
    }

    public Execution approveExecution(String id) {
        Execution execution = getExecutionById(id);

        if (execution.getStatus() != ExecutionStatus.WAITING_APPROVAL) {
            throw new InvalidProjectStateException("Solo se pueden aprobar ejecuciones que se encuentren en estado WAITING_APPROVAL.");
        }

        Task task = getTaskById(execution.getTaskId());
        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));

        Execution approved = new Execution(
                execution.getId(),
                execution.getTaskId(),
                execution.getAgentId(),
                ExecutionStatus.PENDING,
                execution.getCommandLine(),
                execution.getLogFilePath(),
                null,
                execution.getStartedAt(),
                null
        );

        Execution saved = executionRepository.save(approved);
        // Start running
        cliRunner.runAsync(saved, project.getRepositoryPath());
        return saved;
    }

    public Execution rejectExecution(String id) {
        Execution execution = getExecutionById(id);

        if (execution.getStatus() != ExecutionStatus.WAITING_APPROVAL) {
            throw new InvalidProjectStateException("Solo se pueden rechazar ejecuciones que se encuentren en estado WAITING_APPROVAL.");
        }

        Execution rejected = new Execution(
                execution.getId(),
                execution.getTaskId(),
                execution.getAgentId(),
                ExecutionStatus.FAILED,
                execution.getCommandLine(),
                execution.getLogFilePath(),
                -2, // Custom exit code representing human rejection
                execution.getStartedAt(),
                LocalDateTime.now()
        );

        return executionRepository.save(rejected);
    }

    public String getLogContent(String executionId) {
        Execution execution = getExecutionById(executionId);
        Task task = getTaskById(execution.getTaskId());
        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));

        File logFile = new File(project.getRepositoryPath() + "/" + execution.getLogFilePath());
        if (!logFile.exists()) {
            return "";
        }
        try {
            return java.nio.file.Files.readString(logFile.toPath());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error leyendo el archivo de log: " + e.getMessage(), e);
        }
    }

    private boolean isCommandLineRisky(String cmd) {
        if (cmd == null) return false;
        String normalized = cmd.toLowerCase().trim();
        return normalized.contains("git push")
                || normalized.contains("npm publish")
                || normalized.contains("mvn deploy")
                || normalized.contains("docker push")
                || normalized.contains("deploy")
                || normalized.contains("rm -rf");
    }
}
