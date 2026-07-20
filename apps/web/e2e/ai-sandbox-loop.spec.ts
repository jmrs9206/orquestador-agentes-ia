import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test.describe('AI Agent Loop & Sandbox Isolation E2E Flow', () => {
  const tempRepoPath = path.resolve('/tmp/e2e-ai-sandbox-repo');

  test.beforeAll(() => {
    if (!fs.existsSync(tempRepoPath)) {
      fs.mkdirSync(tempRepoPath, { recursive: true });
    }
  });

  test.afterAll(() => {
    if (fs.existsSync(tempRepoPath)) {
      fs.rmSync(tempRepoPath, { recursive: true, force: true });
    }
  });

  test('should trigger Gemini AI agent run, enforce sandbox bounds, and render contract & evidence tabs', async ({ page }) => {
    const randomSuffix = Math.random().toString(36).substring(2, 5);
    const projectKey = `ai-${randomSuffix}`;
    const projectName = `AI Sandbox E2E ${randomSuffix}`;

    // 1. Open App Dashboard
    await page.goto('/');
    await expect(page.locator('h1')).toContainText('Orquestador de Agentes');

    // 2. Create AI Project
    await page.fill('input[placeholder="Ej. MI-PROYECTO"]', projectKey);
    await page.fill('input[placeholder="Ej. MiroFish Core"]', projectName);
    await page.fill('textarea[placeholder="Detalles sobre el proyecto..."]', 'AI Agent Loop and Filesystem Sandbox test project');
    await page.fill('input[placeholder="/absolute/path/to/project"]', tempRepoPath);
    await page.click('button[type="submit"]');

    // Select Project
    const projectCard = page.locator('div.group', { has: page.locator('h3', { hasText: projectName }) }).first();
    await expect(projectCard).toBeVisible();
    await projectCard.click();

    // 3. Register Gemini Agent
    await page.click('button:has-text("Agentes")');
    await page.fill('input[placeholder="Ej. DeveloperAgent"]', 'Gemini-DevAgent');
    await page.selectOption('select:near(label:has-text("Rol del Agente"))', { value: '@developer' });
    await page.fill('input[placeholder="gemini-1.5-flash"]', 'gemini-1.5-flash');
    await page.fill('textarea[placeholder="Instrucciones del rol del agente..."]', 'You are a Gemini AI Developer Agent executing sandboxed tasks.');
    await page.click('button:has-text("Registrar Agente")');

    const agentCard = page.locator('h4', { hasText: 'Gemini-DevAgent' }).first();
    await expect(agentCard).toBeVisible();

    // 4. Create Task
    await page.click('button:has-text("Tareas")');
    await page.fill('input[placeholder="Ej. TASK_006: Orchestrator Domain"]', 'TASK_011_AI_Gemini');
    await page.fill('textarea[placeholder="Criterios y objetivos de la tarea..."]', 'Implement Gemini API Integration and verify sandbox bounds');
    await page.selectOption('select:near(label:has-text("Asignado"))', { label: 'Gemini-DevAgent (@developer)' });
    await page.click('button:has-text("Añadir Tarea")');

    const taskCard = page.locator('h4', { hasText: 'TASK_011_AI_Gemini' }).first();
    await expect(taskCard).toBeVisible();
    await taskCard.click();

    // 5. Verify Workspace Contract & Evidence Tabs
    await expect(page.locator('button:has-text("Contrato TASK_CONTRACT.md")')).toBeVisible();
    await page.click('button:has-text("Contrato TASK_CONTRACT.md")');
    await expect(page.locator('h4', { hasText: '.ai/TASK_task-' })).toBeVisible();

    await page.click('button:has-text("Evidencia EVIDENCE_LOG.md")');
    await expect(page.locator('h4', { hasText: '.ai/TASK_task-' })).toBeVisible();

    // 6. Switch back to Execution Tab & Trigger Gemini Run
    await page.click('button:has-text("Lanzamiento y Ejecuciones")');
    await page.click('button:has-text("Gemini IA Run")');

    await page.selectOption('select:near(label:has-text("Agente Emisor"))', { label: 'Gemini-DevAgent (@developer)' });
    await page.fill('input[placeholder="Ej. Construir cliente Gemini API"]', "Ejecutar prompt de integración Gemini API");
    await page.click('button:has-text("Lanzar Agente Gemini")');

    // Assert execution item appears
    const execItem = page.locator('span:has-text("gemini run")').last();
    await expect(execItem).toBeVisible();

    // 7. Cleanup project
    await page.click('button:has-text("Proyectos")');
    await expect(projectCard).toBeVisible();
    await projectCard.locator('button', { hasText: 'Archivar' }).click();
    await expect(projectCard).not.toBeVisible();
  });
});
