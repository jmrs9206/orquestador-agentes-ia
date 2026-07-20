package com.orquestador.orchestrator.infrastructure.audit;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionStatus;
import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectStatus;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskStatus;
import com.orquestador.orchestrator.infrastructure.runner.WorkspaceSandboxGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskAuditServiceTest {

    private TaskAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new TaskAuditService(new WorkspaceSandboxGuard());
    }

    @Test
    void shouldGenerateTaskContractInSandbox(@TempDir Path tempDir) throws IOException {
        Project project = new Project("p-101", "key", "Test Project", "Desc", ProjectStatus.ACTIVE, tempDir.toString(), "main", ".ai/PROJECT_CONTEXT.md", LocalDateTime.now(), LocalDateTime.now(), null);
        Task task = new Task("task-013", "p-101", "Audit contract gen", "Description of contract task", TaskStatus.IN_PROGRESS, "agent-1", "reviewer-1", LocalDateTime.now(), LocalDateTime.now());
        Agent agent = new Agent("agent-1", "Audit Agent", "@developer", "System prompt", "model", 0.1);

        Path contractPath = auditService.generateTaskContract(project, task, agent);

        assertTrue(Files.exists(contractPath));
        String content = Files.readString(contractPath);
        assertTrue(content.contains("Dynamic Task Contract - task-013"));
        assertTrue(content.contains("Role: @developer"));
        assertTrue(content.contains("Audit contract gen"));
    }

    @Test
    void shouldGenerateEvidenceLogOnTaskCompletion(@TempDir Path tempDir) throws IOException {
        Project project = new Project("p-101", "key", "Test Project", "Desc", ProjectStatus.ACTIVE, tempDir.toString(), "main", ".ai/PROJECT_CONTEXT.md", LocalDateTime.now(), LocalDateTime.now(), null);
        Task task = new Task("task-013", "p-101", "Audit evidence gen", "Desc", TaskStatus.DONE, "agent-1", "reviewer-1", LocalDateTime.now(), LocalDateTime.now());
        Execution execution = new Execution("exec-88", "task-013", "agent-1", ExecutionStatus.SUCCESS, "mvn test", ".ai/logs/exec-88.log", 0, LocalDateTime.now().minusMinutes(1), LocalDateTime.now());

        Path evidencePath = auditService.generateEvidenceLog(project, task, execution, "BUILD SUCCESS - 15 tests passed");

        assertTrue(Files.exists(evidencePath));
        String content = Files.readString(evidencePath);
        assertTrue(content.contains("Execution Evidence Log - task-013"));
        assertTrue(content.contains("Exit Code:** `0`"));
        assertTrue(content.contains("BUILD SUCCESS - 15 tests passed"));
    }
}
