import fs from 'fs';
import path from 'path';
import { defineConfig, devices } from '@playwright/test';

const runId =
  process.env.E2E_RUN_ID ||
  new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
process.env.E2E_RUN_ID = runId;

const envE2e = path.resolve(__dirname, '.env.e2e');
if (fs.existsSync(envE2e)) {
  for (const line of fs.readFileSync(envE2e, 'utf-8').split(/\r?\n/)) {
    const t = line.trim();
    if (!t || t.startsWith('#')) continue;
    const i = t.indexOf('=');
    if (i <= 0) continue;
    const k = t.slice(0, i).trim();
    const v = t.slice(i + 1).trim();
    if (!process.env[k]) process.env[k] = v;
  }
}

const headed = process.env.PLAYWRIGHT_HEADLESS === '0';
const slowMo = headed ? Number(process.env.E2E_SLOW_MO || 200) : 0;

export default defineConfig({
  timeout: 120_000,
  expect: { timeout: 20_000 },
  testDir: 'tests/e2e',
  fullyParallel: false,
  workers: 1,
  retries: 0,
  reporter: [
    ['list'],
    ['html', { open: 'never', outputFolder: `../docs/e2e-runs/${runId}/playwright-report` }],
  ],
  use: {
    baseURL: process.env.E2E_BASE_URL || 'http://10.28.12.15',
    navigationTimeout: 60_000,
    actionTimeout: 30_000,
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: headed ? 'on' : 'off',
    viewport: { width: 1366, height: 768 },
    headless: !headed,
    ignoreHTTPSErrors: true,
    launchOptions: slowMo ? { slowMo } : undefined,
  },
  projects: [
    {
      name: 'setup',
      testMatch: /auth\.setup\.ts/,
    },
    {
      name: 'doc-capture',
      testMatch: /flows\/doc-capture\.spec\.ts/,
      dependencies: ['setup'],
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'full-chain',
      testMatch: /flows\/recruitment-full-chain\.spec\.ts/,
      dependencies: ['setup'],
      timeout: 900_000,
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'security',
      testMatch: /security\/.*\.spec\.ts/,
      dependencies: ['setup'],
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
