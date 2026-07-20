# Task Contract - TASK_014: Agent Role & Permission Enforcement

- Iteration ID: `it-03`
- Task ID: `task-014`
- Title: Filtros de Control de Roles de Agente
- Status: `DONE`
- Assigned role: `@backend`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/ai-integration-layer`

## Objective
Enforce role validations restricting status transitions and execution calls based on agent roles and assignments.

## Acceptance Criteria
1. **Approval Role Check**: Reject attempts to transition a task to approved or done if the agent or user requesting the action is not assigned the `@reviewer` or `@admin` role.
2. **Assignee Execution check**: Ensure only the agent assigned to the task can trigger non-risky CLI commands or API runs under that task.
3. **HTTP Bad Request Handling**: Return explicit validation errors and status codes on authorization guard failures.
4. **Unit testing**: JUnit controller tests verifying access rejection on unauthorized role requests.

## Verification Method
- Execute Spring MVC unit tests validating role interceptor behavior.
