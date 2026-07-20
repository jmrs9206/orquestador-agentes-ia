package com.orquestador.orchestrator.infrastructure.runner;

import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionRepository;
import com.orquestador.orchestrator.domain.ExecutionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CliRunnerTest {

    @Test
    void shouldExecuteCommandAndSaveLogs(@TempDir Path tempDir) throws Exception {
        ExecutionRepository mockRepo = mock(ExecutionRepository.class);
        WorkspaceSandboxGuard sandboxGuard = new WorkspaceSandboxGuard();
        CliRunner runner = new CliRunner(mockRepo, sandboxGuard);

        String execId = "exec-123";
        String logRelativePath = ".ai/logs/exec-123.log";
        Execution execution = new Execution(
                execId,
                "task-123",
                "agent-123",
                ExecutionStatus.PENDING,
                "echo 'runner-works'",
                logRelativePath,
                null,
                LocalDateTime.now(),
                null
        );

        runner.runAsync(execution, tempDir.toAbsolutePath().toString());

        // Bounded wait for async execution
        Thread.sleep(1500);

        ArgumentCaptor<Execution> captor = ArgumentCaptor.forClass(Execution.class);
        verify(mockRepo, atLeastOnce()).save(captor.capture());

        Execution finalState = captor.getAllValues().stream()
                .filter(e -> e.getStatus() == ExecutionStatus.SUCCESS)
                .findFirst()
                .orElse(null);

        assertNotNull(finalState);
        assertEquals(0, finalState.getExitCode());

        File logFile = new File(tempDir.toFile(), logRelativePath);
        assertTrue(logFile.exists());
        String logContent = Files.readString(logFile.toPath());
        assertTrue(logContent.contains("runner-works"));
    }

    @Test
    void shouldHandleFailedCommands(@TempDir Path tempDir) throws Exception {
        ExecutionRepository mockRepo = mock(ExecutionRepository.class);
        WorkspaceSandboxGuard sandboxGuard = new WorkspaceSandboxGuard();
        CliRunner runner = new CliRunner(mockRepo, sandboxGuard);

        Execution execution = new Execution(
                "exec-fail",
                "task-123",
                "agent-123",
                ExecutionStatus.PENDING,
                "exit 42",
                ".ai/logs/fail.log",
                null,
                LocalDateTime.now(),
                null
        );

        runner.runAsync(execution, tempDir.toAbsolutePath().toString());
        Thread.sleep(1500);

        ArgumentCaptor<Execution> captor = ArgumentCaptor.forClass(Execution.class);
        verify(mockRepo, atLeastOnce()).save(captor.capture());

        Execution finalState = captor.getAllValues().stream()
                .filter(e -> e.getStatus() == ExecutionStatus.FAILED)
                .findFirst()
                .orElse(null);

        assertNotNull(finalState);
        assertEquals(42, finalState.getExitCode());
    }

    @Test
    void shouldFailExecutionWhenPathTraversalAttempted(@TempDir Path tempDir) throws Exception {
        ExecutionRepository mockRepo = mock(ExecutionRepository.class);
        WorkspaceSandboxGuard sandboxGuard = new WorkspaceSandboxGuard();
        CliRunner runner = new CliRunner(mockRepo, sandboxGuard);

        Execution execution = new Execution(
                "exec-traversal",
                "task-123",
                "agent-123",
                ExecutionStatus.PENDING,
                "echo 'pwned'",
                "../../../../etc/passwd",
                null,
                LocalDateTime.now(),
                null
        );

        runner.runAsync(execution, tempDir.toAbsolutePath().toString());
        Thread.sleep(1500);

        ArgumentCaptor<Execution> captor = ArgumentCaptor.forClass(Execution.class);
        verify(mockRepo, atLeastOnce()).save(captor.capture());

        Execution finalState = captor.getAllValues().stream()
                .filter(e -> e.getStatus() == ExecutionStatus.FAILED)
                .findFirst()
                .orElse(null);

        assertNotNull(finalState);
        assertEquals(-1, finalState.getExitCode());
    }
}
