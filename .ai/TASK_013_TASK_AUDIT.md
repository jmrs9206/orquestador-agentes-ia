# Task Contract - TASK_013: Task Contract & Evidence Generation

- Iteration ID: `it-03`
- Task ID: `task-013`
- Title: Generación de Contratos y Evidencia de Auditoría
- Status: `DONE`
- Assigned role: `@backend`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/ai-integration-layer`

## Objective
Implement dynamic file generation in the project's `.ai/` directory to document task contracts before execution and compile execution evidence after completion.

## Acceptance Criteria
1. **Dynamic Task Contract generation**: Upon starting a task execution, automatically generate a `TASK_CONTRACT.md` under `{repositoryPath}/.ai/` documenting assigned agent, target objectives, inputs, and acceptance criteria.
2. **Dynamic Evidence Log compilation**: Upon task completion (status `DONE`), automatically generate an `EVIDENCE_LOG.md` detailing final execution logs, Git diff, and test validation checks.
3. **Audit Log Persistence**: Ensure generated Markdown files are persisted physically in the sandbox directory and visible to users.
4. **Integration Testing**: Write tests verifying file creation and content formatting on command trigger.

## Verification Method
- Execute tests verifying creation of Markdown audit files in target workspace folders.
