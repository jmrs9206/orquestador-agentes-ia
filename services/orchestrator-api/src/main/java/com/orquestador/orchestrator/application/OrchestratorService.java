package com.orquestador.orchestrator.application;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.AgentRepository;
import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionRepository;
import com.orquestador.orchestrator.domain.ExecutionStatus;
import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectRepository;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskRepository;
import com.orquestador.orchestrator.domain.TaskStatus;
import com.orquestador.orchestrator.domain.exceptions.InvalidProjectStateException;
import com.orquestador.orchestrator.domain.exceptions.ProjectNotFoundException;
import com.orquestador.orchestrator.infrastructure.audit.TaskAuditService;
import com.orquestador.orchestrator.infrastructure.runner.CliRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(OrchestratorService.class);

    private final ProjectRepository projectRepository;
    private final AgentRepository agentRepository;
    private final TaskRepository taskRepository;
    private final ExecutionRepository executionRepository;
    private final CliRunner cliRunner;
    private final AgentRoleGuard roleGuard;
    private final TaskAuditService auditService;

    public OrchestratorService(ProjectRepository projectRepository, AgentRepository agentRepository,
                               TaskRepository taskRepository, ExecutionRepository executionRepository,
                               CliRunner cliRunner, AgentRoleGuard roleGuard, TaskAuditService auditService) {
        this.projectRepository = projectRepository;
        this.agentRepository = agentRepository;
        this.taskRepository = taskRepository;
        this.executionRepository = executionRepository;
        this.cliRunner = cliRunner;
        this.roleGuard = roleGuard;
        this.auditService = auditService;
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
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));

        Agent assignee = (assigneeId != null) ? getAgentById(assigneeId) : null;
        Agent reviewer = (reviewerId != null) ? getAgentById(reviewerId) : null;

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
        Task saved = taskRepository.save(task);

        try {
            auditService.generateTaskContract(project, saved, assignee);
        } catch (Exception e) {
            log.warn("Failed to generate task contract file: {}", e.getMessage());
        }

        return saved;
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
        Project project = projectRepository.findById(existing.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));

        Agent reviewer = (existing.getReviewerId() != null) ? getAgentById(existing.getReviewerId()) : null;

        // Apply strict Role Guard validation before allowing status change
        roleGuard.validateStatusTransition(existing, targetStatus, reviewer, reviewer != null ? reviewer.getRole() : "@reviewer");

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
        Task saved = taskRepository.save(updated);

        // Audit evidence log compilation on completion
        if (targetStatus == TaskStatus.DONE) {
            try {
                List<Execution> executions = executionRepository.findByTaskId(saved.getId());
                Execution lastExec = executions.isEmpty() ? null : executions.get(executions.size() - 1);
                String logContent = (lastExec != null) ? getLogContent(lastExec.getId()) : "Task completed by authorized reviewer.";
                auditService.generateEvidenceLog(project, saved, lastExec, logContent);
            } catch (Exception e) {
                log.warn("Failed to generate evidence log file: {}", e.getMessage());
            }
        }

        return saved;
    }

    // Execution operations
    public Execution triggerExecution(String taskId, String agentId, String commandLine) {
        Task task = getTaskById(taskId);
        Agent agent = getAgentById(agentId);
        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));

        // Enforce role authorization: only assigned agent can trigger task execution
        roleGuard.validateExecutionTrigger(task, agent);

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

        // Generate or update Task Contract on disk
        try {
            auditService.generateTaskContract(project, task, agent);
        } catch (Exception e) {
            log.warn("Failed to update task contract file on execution: {}", e.getMessage());
        }

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
