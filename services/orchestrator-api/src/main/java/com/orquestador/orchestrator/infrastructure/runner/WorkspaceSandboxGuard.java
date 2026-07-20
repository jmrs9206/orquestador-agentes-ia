package com.orquestador.orchestrator.infrastructure.runner;

import com.orquestador.orchestrator.domain.exceptions.WorkspaceSecurityException;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class WorkspaceSandboxGuard {

    /**
     * Validates that targetPath resides strictly within the rootRepositoryPath boundary.
     * Throws WorkspaceSecurityException if a path traversal attempt is detected.
     */
    public Path validateAndNormalizePath(String rootRepositoryPath, String targetPath) {
        if (rootRepositoryPath == null || rootRepositoryPath.isBlank()) {
            throw new WorkspaceSecurityException("Root repository path cannot be null or empty.");
        }
        if (targetPath == null || targetPath.isBlank()) {
            throw new WorkspaceSecurityException("Target path cannot be null or empty.");
        }

        // Quick string check for traversal patterns
        if (targetPath.contains("../") || targetPath.contains("..\\")) {
            throw new WorkspaceSecurityException("Path traversal pattern detected in path: " + targetPath);
        }

        Path rootPath = Paths.get(rootRepositoryPath).toAbsolutePath().normalize();
        Path resolvedPath = rootPath.resolve(targetPath).toAbsolutePath().normalize();

        if (!resolvedPath.startsWith(rootPath)) {
            throw new WorkspaceSecurityException("Access denied: path '" + targetPath + "' escapes repository root boundary '" + rootRepositoryPath + "'");
        }

        return resolvedPath;
    }

    /**
     * Validates command string for dangerous out-of-bounds file redirection or traversal commands.
     */
    public void validateCommandSafety(String commandLine) {
        if (commandLine == null || commandLine.isBlank()) {
            return;
        }

        // Block explicit attempt to cd out or redirect to sensitive host directories
        String lowerCmd = commandLine.toLowerCase();
        if (lowerCmd.contains("../") || lowerCmd.contains("..\\") ||
            lowerCmd.contains("> /etc/") || lowerCmd.contains("> /var/") ||
            lowerCmd.contains("> ~/.ssh") || lowerCmd.contains("> /root")) {
            throw new WorkspaceSecurityException("Command execution blocked due to unsafe path redirection: " + commandLine);
        }
    }
}
