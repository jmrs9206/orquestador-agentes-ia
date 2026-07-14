import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test.describe('Project Registry E2E Flow', () => {
  const tempRepoPath = path.resolve('/tmp/e2e-project-repo');

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

  test('should register, list, and archive a project', async ({ page }) => {
    const randomSuffix = Math.random().toString(36).substring(2, 5);
    const projectKey = `e2e-${randomSuffix}`;
    const projectName = `E2E Project ${randomSuffix}`;

    // 1. Visit Dashboard
    await page.goto('/');
    await expect(page.locator('h1')).toContainText('Orquestador de Agentes IA');

    // 2. Submit the form to register a new project
    await page.fill('input[placeholder="Ej. MI-PROYECTO"]', projectKey);
    await page.fill('input[placeholder="Ej. MiroFish Core"]', projectName);
    await page.fill('textarea[placeholder="Detalles sobre el proyecto..."]', 'Form E2E testing description');
    await page.fill('input[placeholder="/absolute/path/to/project"]', tempRepoPath);
    await page.fill('input[value="main"]', 'main');
    await page.fill('input[value=".ai"]', '.ai');

    await page.click('button[type="submit"]');

    // 3. Assert project is registered and visible in list
    const projectCard = page.locator('div.group', { has: page.locator('h3', { hasText: projectName }) }).first();
    await expect(projectCard).toBeVisible();
    await expect(projectCard.locator('span', { hasText: 'DRAFT' }).first()).toBeVisible();

    // 4. Archive project
    await projectCard.locator('button', { hasText: 'Archivar' }).click();

    // 5. Assert project disappears from the active projects list
    await expect(projectCard).not.toBeVisible();
  });
});
