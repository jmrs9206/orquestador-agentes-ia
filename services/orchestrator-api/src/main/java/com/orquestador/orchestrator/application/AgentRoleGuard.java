package com.orquestador.orchestrator.application;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskStatus;
import com.orquestador.orchestrator.domain.exceptions.UnauthorizedRoleException;
import org.springframework.stereotype.Component;

@Component
public class AgentRoleGuard {

    public void validateStatusTransition(Task task, TaskStatus newStatus, Agent requestingAgent, String requestingRole) {
        if (newStatus == TaskStatus.DONE) {
            String role = (requestingAgent != null && requestingAgent.getRole() != null)
                    ? requestingAgent.getRole()
                    : requestingRole;

            if (role == null || role.isBlank()) {
                throw new UnauthorizedRoleException("Requesting role must be specified to approve task to DONE status.");
            }

            // Must be @reviewer or @admin
            if (!"@reviewer".equalsIgnoreCase(role) && !"@admin".equalsIgnoreCase(role)) {
                throw new UnauthorizedRoleException("Role '" + role + "' is not authorized to transition task to DONE. Only @reviewer or @admin roles can approve completion.");
            }

            // Preventive check: Assignee cannot approve their own task
            if (requestingAgent != null && requestingAgent.getId().equals(task.getAssigneeId()) && !"@admin".equalsIgnoreCase(role)) {
                throw new UnauthorizedRoleException("Conflict of interest: Task assignee (" + task.getAssigneeId() + ") cannot self-approve completion.");
            }
        }
    }

    public void validateExecutionTrigger(Task task, Agent triggeringAgent) {
        if (task.getAssigneeId() != null && triggeringAgent != null) {
            if (!task.getAssigneeId().equals(triggeringAgent.getId())) {
                throw new UnauthorizedRoleException("Execution denied: Agent '" + triggeringAgent.getId() + "' is not assigned to task '" + task.getId() + "' (assigned: '" + task.getAssigneeId() + "').");
            }
        }
    }
}
