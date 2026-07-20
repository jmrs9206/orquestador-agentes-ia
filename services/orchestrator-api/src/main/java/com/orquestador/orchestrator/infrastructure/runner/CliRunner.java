package com.orquestador.orchestrator.infrastructure.runner;

import com.orquestador.orchestrator.domain.Execution;
import com.orquestador.orchestrator.domain.ExecutionRepository;
import com.orquestador.orchestrator.domain.ExecutionStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CliRunner {

    private final ExecutionRepository executionRepository;
    private final WorkspaceSandboxGuard sandboxGuard;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public CliRunner(ExecutionRepository executionRepository, WorkspaceSandboxGuard sandboxGuard) {
        this.executionRepository = executionRepository;
        this.sandboxGuard = sandboxGuard;
    }

    public void runAsync(Execution execution, String repoPath) {
        executorService.submit(() -> {
            // Update execution state to RUNNING
            Execution running = new Execution(
                    execution.getId(),
                    execution.getTaskId(),
                    execution.getAgentId(),
                    ExecutionStatus.RUNNING,
                    execution.getCommandLine(),
                    execution.getLogFilePath(),
                    null,
                    LocalDateTime.now(),
                    null
            );
            executionRepository.save(running);

            try {
                // Validate sandbox security constraints before launching command
                sandboxGuard.validateCommandSafety(execution.getCommandLine());
                Path validatedLogPath = sandboxGuard.validateAndNormalizePath(repoPath, execution.getLogFilePath());

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", execution.getCommandLine());
                pb.directory(new File(repoPath));

                // Clear env variables and only pass safe ones to prevent secret leaks
                pb.environment().clear();
                String[] safeEnvVars = {"PATH", "JAVA_HOME", "HOME", "USER", "LANG", "SHELL"};
                for (String var : safeEnvVars) {
                    String val = System.getenv(var);
                    if (val != null) {
                        pb.environment().put(var, val);
                    }
                }

                // Inject GEMINI_API_KEY if present in system env
                String geminiKey = System.getenv("GEMINI_API_KEY");
                if (geminiKey != null) {
                    pb.environment().put("GEMINI_API_KEY", geminiKey);
                }

                pb.redirectErrorStream(true);

                // Setup output file using validated path
                File logFile = validatedLogPath.toFile();
                if (logFile.getParentFile() != null) {
                    logFile.getParentFile().mkdirs();
                }
                pb.redirectOutput(logFile);

                Process process = pb.start();
                int exitCode = process.waitFor();

                ExecutionStatus finalStatus = (exitCode == 0) ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED;
                Execution completed = new Execution(
                        execution.getId(),
                        execution.getTaskId(),
                        execution.getAgentId(),
                        finalStatus,
                        execution.getCommandLine(),
                        execution.getLogFilePath(),
                        exitCode,
                        running.getStartedAt(),
                        LocalDateTime.now()
                );
                executionRepository.save(completed);

            } catch (Exception e) {
                // Log failed process start due to error or WorkspaceSecurityException
                Execution failed = new Execution(
                        execution.getId(),
                        execution.getTaskId(),
                        execution.getAgentId(),
                        ExecutionStatus.FAILED,
                        execution.getCommandLine(),
                        execution.getLogFilePath(),
                        -1,
                        running.getStartedAt(),
                        LocalDateTime.now()
                );
                executionRepository.save(failed);
            }
        });
    }
}
