package com.orquestador.orchestrator.infrastructure.persistence;

import com.orquestador.orchestrator.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class OrchestrationRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Test
    void shouldPersistOrchestrationEntities() {
        // 1. Create and persist project
        Project project = new Project(
                "proj-uuid-1", "my-orchestrated-proj", "Orchestrated Project", "Desc",
                ProjectStatus.DRAFT, "/tmp/orchestrator", "main", ".ai",
                LocalDateTime.now(), LocalDateTime.now(), null
        );
        projectRepository.save(project);

        // 2. Create and persist agents
        Agent developerAgent = new Agent(
                "agent-dev-1", "Dev Agent", "DEVELOPER", "Write code", "gemini-1.5-flash", 0.2
        );
        Agent reviewerAgent = new Agent(
                "agent-rev-1", "Reviewer Agent", "REVIEWER", "Review code", "gemini-1.5-pro", 0.1
        );
        agentRepository.save(developerAgent);
        agentRepository.save(reviewerAgent);

        // 3. Create and persist a task
        Task task = new Task(
                "task-uuid-1", "proj-uuid-1", "Implement CLI Runner", "Implement task-007",
                TaskStatus.DRAFT, "agent-dev-1", "agent-rev-1", LocalDateTime.now(), LocalDateTime.now()
        );
        taskRepository.save(task);

        // 4. Create and persist an execution
        Execution execution = new Execution(
                "exec-uuid-1", "task-uuid-1", "agent-dev-1", ExecutionStatus.PENDING,
                "echo 'test'", "/tmp/logs/exec-1.log", null, LocalDateTime.now(), null
        );
        executionRepository.save(execution);

        // -- Asserts --
        // Retrieve Agent
        Optional<Agent> retrievedAgent = agentRepository.findById("agent-dev-1");
        assertTrue(retrievedAgent.isPresent());
        assertEquals("Dev Agent", retrievedAgent.get().getName());
        assertEquals(0.2, retrievedAgent.get().getTemperature());

        // Retrieve Task
        Optional<Task> retrievedTask = taskRepository.findById("task-uuid-1");
        assertTrue(retrievedTask.isPresent());
        assertEquals("Implement CLI Runner", retrievedTask.get().getTitle());
        assertEquals("agent-dev-1", retrievedTask.get().getAssigneeId());
        assertEquals("agent-rev-1", retrievedTask.get().getReviewerId());

        // Retrieve Execution
        Optional<Execution> retrievedExec = executionRepository.findById("exec-uuid-1");
        assertTrue(retrievedExec.isPresent());
        assertEquals(ExecutionStatus.PENDING, retrievedExec.get().getStatus());
        assertEquals("echo 'test'", retrievedExec.get().getCommandLine());

        // List tasks by project
        List<Task> projectTasks = taskRepository.findByProjectId("proj-uuid-1");
        assertEquals(1, projectTasks.size());
        assertEquals("task-uuid-1", projectTasks.get(0).getId());

        // List executions by task
        List<Execution> taskExecs = executionRepository.findByTaskId("task-uuid-1");
        assertEquals(1, taskExecs.size());
        assertEquals("exec-uuid-1", taskExecs.get(0).getId());
    }
}
