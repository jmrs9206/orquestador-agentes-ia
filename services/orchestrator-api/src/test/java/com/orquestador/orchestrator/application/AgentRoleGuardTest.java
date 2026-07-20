package com.orquestador.orchestrator.application;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskStatus;
import com.orquestador.orchestrator.domain.exceptions.UnauthorizedRoleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AgentRoleGuardTest {

    private AgentRoleGuard roleGuard;

    @BeforeEach
    void setUp() {
        roleGuard = new AgentRoleGuard();
    }

    @Test
    void shouldAllowReviewerToApproveTaskToDone() {
        Task task = new Task("t-1", "p-1", "Title", "Desc", TaskStatus.IN_PROGRESS, "agent-dev", "agent-rev", LocalDateTime.now(), LocalDateTime.now());
        Agent reviewer = new Agent("agent-rev", "Reviewer Agent", "@reviewer", "System prompt", "model", 0.1);

        assertDoesNotThrow(() -> roleGuard.validateStatusTransition(task, TaskStatus.DONE, reviewer, "@reviewer"));
    }

    @Test
    void shouldRejectNonReviewerAttemptToApproveTaskToDone() {
        Task task = new Task("t-1", "p-1", "Title", "Desc", TaskStatus.IN_PROGRESS, "agent-dev", "agent-rev", LocalDateTime.now(), LocalDateTime.now());
        Agent developer = new Agent("agent-dev", "Dev Agent", "@developer", "System prompt", "model", 0.1);

        UnauthorizedRoleException ex = assertThrows(
                UnauthorizedRoleException.class,
                () -> roleGuard.validateStatusTransition(task, TaskStatus.DONE, developer, "@developer")
        );
        assertTrue(ex.getMessage().contains("is not authorized to transition task to DONE"));
    }

    @Test
    void shouldRejectSelfApprovalByAssignee() {
        Task task = new Task("t-1", "p-1", "Title", "Desc", TaskStatus.IN_PROGRESS, "agent-same", "agent-same", LocalDateTime.now(), LocalDateTime.now());
        Agent agent = new Agent("agent-same", "Dual Agent", "@reviewer", "System prompt", "model", 0.1);

        UnauthorizedRoleException ex = assertThrows(
                UnauthorizedRoleException.class,
                () -> roleGuard.validateStatusTransition(task, TaskStatus.DONE, agent, "@reviewer")
        );
        assertTrue(ex.getMessage().contains("Conflict of interest"));
    }

    @Test
    void shouldRejectExecutionTriggerByUnassignedAgent() {
        Task task = new Task("t-1", "p-1", "Title", "Desc", TaskStatus.IN_PROGRESS, "agent-assigned", "agent-rev", LocalDateTime.now(), LocalDateTime.now());
        Agent unassignedAgent = new Agent("agent-other", "Other Agent", "@developer", "System prompt", "model", 0.1);

        UnauthorizedRoleException ex = assertThrows(
                UnauthorizedRoleException.class,
                () -> roleGuard.validateExecutionTrigger(task, unassignedAgent)
        );
        assertTrue(ex.getMessage().contains("is not assigned to task"));
    }
}
