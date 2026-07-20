package com.orquestador.orchestrator.infrastructure.runner;

import com.orquestador.orchestrator.domain.exceptions.WorkspaceSecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceSandboxGuardTest {

    private WorkspaceSandboxGuard sandboxGuard;

    @BeforeEach
    void setUp() {
        sandboxGuard = new WorkspaceSandboxGuard();
    }

    @Test
    void shouldAllowPathWithinWorkspaceRoot(@TempDir Path tempDir) {
        String root = tempDir.toString();
        String validSubPath = ".ai/logs/exec-101.log";

        Path resolved = sandboxGuard.validateAndNormalizePath(root, validSubPath);

        assertNotNull(resolved);
        assertTrue(resolved.startsWith(tempDir.toAbsolutePath().normalize()));
    }

    @Test
    void shouldThrowExceptionWhenPathContainsRelativeTraversal(@TempDir Path tempDir) {
        String root = tempDir.toString();
        String invalidPath = ".ai/logs/../../../../etc/passwd";

        WorkspaceSecurityException ex = assertThrows(
                WorkspaceSecurityException.class,
                () -> sandboxGuard.validateAndNormalizePath(root, invalidPath)
        );
        assertTrue(ex.getMessage().contains("Path traversal pattern detected"));
    }

    @Test
    void shouldThrowExceptionWhenTargetEscapesWorkspaceRoot(@TempDir Path tempDir) {
        String root = tempDir.toString();
        String escapingPath = "/etc/shadow";

        WorkspaceSecurityException ex = assertThrows(
                WorkspaceSecurityException.class,
                () -> sandboxGuard.validateAndNormalizePath(root, escapingPath)
        );
        assertTrue(ex.getMessage().contains("escapes repository root boundary"));
    }

    @Test
    void shouldBlockDangerousCommandRedirections() {
        assertThrows(
                WorkspaceSecurityException.class,
                () -> sandboxGuard.validateCommandSafety("cat /etc/passwd > /etc/pwned")
        );

        assertThrows(
                WorkspaceSecurityException.class,
                () -> sandboxGuard.validateCommandSafety("cd ../.. && rm -rf /")
        );

        assertDoesNotThrow(() -> sandboxGuard.validateCommandSafety("echo 'Hello Agent' > .ai/logs/out.log"));
    }
}
