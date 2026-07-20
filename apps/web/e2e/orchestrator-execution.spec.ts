import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test.describe('Orchestration E2E Flow', () => {
  const tempRepoPath = path.resolve('/tmp/e2e-orchestrator-repo');

  test.beforeAll(() => {
    // Ensure the mock repository directory exists locally so the backend path validation passes
    if (!fs.existsSync(tempRepoPath)) {
      fs.mkdirSync(tempRepoPath, { recursive: true });
    }
  });

  test.afterAll(() => {
    // Cleanup the mock repository folder
    if (fs.existsSync(tempRepoPath)) {
      fs.rmSync(tempRepoPath, { recursive: true, force: true });
    }
  });

  test('should manage agents, tasks, run command, and trigger Human Gate block/rejection', async ({ page }) => {
    const randomSuffix = Math.random().toString(36).substring(2, 5);
    const projectKey = `ex-${randomSuffix}`;
    const projectName = `E2E Run ${randomSuffix}`;

    // 1. Visit Dashboard
    await page.goto('/');
    await expect(page.locator('h1')).toContainText('Orquestador de Agentes');

    // 2. Submit project registration form
    await page.fill('input[placeholder="Ej. MI-PROYECTO"]', projectKey);
    await page.fill('input[placeholder="Ej. MiroFish Core"]', projectName);
    await page.fill('textarea[placeholder="Detalles sobre el proyecto..."]', 'E2E project workspace description');
    await page.fill('input[placeholder="/absolute/path/to/project"]', tempRepoPath);
    await page.click('button[type="submit"]');

    // 3. Find and click project card to open Workspace Console
    const projectCard = page.locator('div.group', { has: page.locator('h3', { hasText: projectName }) }).first();
    await expect(projectCard).toBeVisible();
    await projectCard.click();

    // 4. Register an Agent
    await page.click('button:has-text("Agentes")');
    await page.fill('input[placeholder="Ej. DeveloperAgent"]', 'E2E-Agent');
    await page.fill('input[placeholder="gemini-1.5-flash"]', 'gemini-1.5-flash');
    await page.fill('textarea[placeholder="Instrucciones del rol del agente..."]', 'System Prompt E2E Agent');
    await page.click('button:has-text("Registrar Agente")');

    // Assert agent card is visible
    const agentCard = page.locator('h4', { hasText: 'E2E-Agent' }).first();
    await expect(agentCard).toBeVisible();

    // 5. Create a Task Contract
    await page.click('button:has-text("Tareas")');
    await page.fill('input[placeholder="Ej. TASK_006: Orchestrator Domain"]', 'E2E-Task-001');
    await page.fill('textarea[placeholder="Criterios y objetivos de la tarea..."]', 'Objectives of E2E task');
    
    // Assign developer
    await page.selectOption('select:near(label:has-text("Asignado"))', { label: 'E2E-Agent (DEVELOPER)' });
    await page.click('button:has-text("Añadir Tarea")');

    // Assert task is listed
    const taskCard = page.locator('h4', { hasText: 'E2E-Task-001' }).first();
    await expect(taskCard).toBeVisible();

    // 6. Select the Task
    await taskCard.click();
    await expect(page.locator('h3', { hasText: 'E2E-Task-001' })).toBeVisible();

    // 7. Trigger a Safe Command Line Execution
    await page.selectOption('select:near(label:has-text("Agente Emisor"))', { label: 'E2E-Agent' });
    await page.fill('input[placeholder="Ej. echo \'Hello orquestador\'"]', "echo 'hello-from-e2e-test'");
    await page.click('button:has-text("Ejecutar")');

    // Assert execution item appears in history and is marked SUCCESS or RUNNING
    const execItemSafe = page.locator('span:has-text("hello-from-e2e-test")').last();
    await expect(execItemSafe).toBeVisible();
    await execItemSafe.click();

    // Assert console terminal output matches our echo
    await expect(page.locator('div.text-emerald-400')).toContainText('hello-from-e2e-test');

    // 8. Trigger a Risky Command (Human Gate matching)
    await page.fill('input[placeholder="Ej. echo \'Hello orquestador\'"]', "git push origin main");
    await page.click('button:has-text("Ejecutar")');

    // Assert execution item is marked WAITING_APPROVAL
    const execItemRisky = page.locator('span:has-text("git push origin main")').last();
    await expect(execItemRisky).toBeVisible();
    
    const warningLabel = page.locator('span', { hasText: 'WAITING_APPROVAL' }).last();
    await expect(warningLabel).toBeVisible();

    // Click execution to view Security Banner
    await execItemRisky.click();

    const securityBanner = page.locator('div', { hasText: 'ALERTA DE SEGURIDAD' }).first();
    await expect(securityBanner).toBeVisible();

    // 9. Reject the Risky Command
    await page.click('button:has-text("Rechazar Comando")');

    // Assert status switches to FAILED and exit code is -2
    await expect(page.locator('span', { hasText: 'FAILED' }).first()).toBeVisible();
    await expect(page.locator('span', { hasText: 'Exit: -2' }).first()).toBeVisible();

    // 10. Archive the project (Clean up from active list)
    await page.click('button:has-text("Proyectos")');
    await expect(projectCard).toBeVisible();
    await projectCard.locator('button', { hasText: 'Archivar' }).click();
    await expect(projectCard).not.toBeVisible();
  });
});
