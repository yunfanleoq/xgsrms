import fs from 'fs';
import path from 'path';
import { test as setup } from '@playwright/test';
import { E2E_ACCOUNTS } from './helpers/accounts';
import { loginViaUi } from './helpers/login';

const AUTH_DIR = path.resolve(__dirname, '../../.auth');

setup.beforeAll(() => {
  fs.mkdirSync(AUTH_DIR, { recursive: true });
});

for (const account of E2E_ACCOUNTS) {
  setup(`authenticate ${account.id}`, async ({ page }) => {
    await loginViaUi(page, account);
    const authPath = path.resolve(AUTH_DIR, `${account.id}.json`);
    await page.context().storageState({ path: authPath });
  });
}
