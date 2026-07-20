package com.orquestador.orchestrator.infrastructure.audit;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.infrastructure.runner.WorkspaceSandboxGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class TaskAuditService {

    private static final Logger log = LoggerFactory.getLogger(TaskAuditService.class);

    private final WorkspaceSandboxGuard sandboxGuard;

    public TaskAuditService(WorkspaceSandboxGuard sandboxGuard) {
        this.sandboxGuard = sandboxGuard;
    }

    public Path generateTaskContract(Project project, Task task, Agent agent) {
        String contractFileName = ".ai/TASK_" + task.getId() + "_CONTRACT.md";
        Path targetPath = sandboxGuard.validateAndNormalizePath(project.getRepositoryPath(), contractFileName);

        StringBuilder sb = new StringBuilder();
        sb.append("# Dynamic Task Contract - ").append(task.getId()).append("\n\n");
        sb.append("- **Project ID:** `").append(project.getId()).append("`\n");
        sb.append("- **Task ID:** `").append(task.getId()).append("`\n");
        sb.append("- **Title:** ").append(task.getTitle()).append("\n");
        sb.append("- **Assigned Agent:** `").append(agent != null ? agent.getId() : task.getAssigneeId()).append("` (Role: ").append(agent != null ? agent.getRole() : "Unassigned").append(")\n");
        sb.append("- **Reviewer:** `").append(task.getReviewerId() != null ? task.getReviewerId() : "Unassigned").append("`\n");
        sb.append("- **Status:** `").append(task.getStatus()).append("`\n");
        sb.append("- **Timestamp:** `").append(LocalDateTime.now()).append("`\n\n");
        sb.append("## Objective\n");
        sb.append(task.getDescription() != null ? task.getDescription() : "No description provided.").append("\n\n");
        sb.append("## Governance & Sandbox Constraints\n");
        sb.append("- All actions are restricted to repository path: `").append(project.getRepositoryPath()).append("`\n");
        sb.append("- Direct execution outside sandbox is prohibited.\n");

        writeFileSafely(targetPath, sb.toString());
        log.info("Generated Task Contract at: {}", targetPath);
        return targetPath;
    }

    public Path generateEvidenceLog(Project project, Task task, Execution execution, String logOutput) {
        String evidenceFileName = ".ai/TASK_" + task.getId() + "_EVIDENCE.md";
        Path targetPath = sandboxGuard.validateAndNormalizePath(project.getRepositoryPath(), evidenceFileName);

        StringBuilder sb = new StringBuilder();
        sb.append("# Execution Evidence Log - ").append(task.getId()).append("\n\n");
        sb.append("- **Task ID:** `").append(task.getId()).append("`\n");
        sb.append("- **Execution ID:** `").append(execution != null ? execution.getId() : "N/A").append("`\n");
        sb.append("- **Status:** `").append(execution != null ? execution.getStatus() : "UNKNOWN").append("`\n");
        sb.append("- **Exit Code:** `").append(execution != null ? execution.getExitCode() : -1).append("`\n");
        sb.append("- **Started At:** `").append(execution != null ? execution.getStartedAt() : "N/A").append("`\n");
        sb.append("- **Completed At:** `").append(execution != null ? execution.getFinishedAt() : "N/A").append("`\n\n");
        sb.append("## Executed Command\n");
        sb.append("```bash\n");
        sb.append(execution != null ? execution.getCommandLine() : "N/A").append("\n");
        sb.append("```\n\n");
        sb.append("## Output Logs\n");
        sb.append("```text\n");
        sb.append(logOutput != null && !logOutput.isBlank() ? logOutput : "[No output log recorded]").append("\n");
        sb.append("```\n");

        writeFileSafely(targetPath, sb.toString());
        log.info("Generated Evidence Log at: {}", targetPath);
        return targetPath;
    }

    private void writeFileSafely(Path targetPath, String content) {
        try {
            if (targetPath.getParent() != null) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.writeString(targetPath, content);
        } catch (IOException e) {
            log.error("Failed to write audit file: {}", targetPath, e);
            throw new RuntimeException("Audit file write error: " + e.getMessage(), e);
        }
    }
}
