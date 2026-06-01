import { expect, type Page } from '@playwright/test';
import type { E2eAccount } from './accounts';
import { readCaptchaFromLoginPage, refreshCaptcha } from './captcha';

const TOKEN_KEY = 'TOKEN__';

export async function loginViaUi(page: Page, account: E2eAccount, maxAttempts = 4): Promise<void> {
  await page.goto('/login', { waitUntil: 'domcontentloaded' });
  await page.getByPlaceholder(/用户名|账号/i).first().fill(account.username);
  await page.locator('input[type="password"]').first().fill(account.password);

  for (let i = 0; i < maxAttempts; i++) {
    try {
      const code = await readCaptchaFromLoginPage(page);
      const captchaInput = page.getByPlaceholder(/验证码/i).first();
      await captchaInput.fill('');
      await captchaInput.fill(code);
      await page.getByRole('button', { name: /登\s*录/i }).first().click();

      await expect
        .poll(
          async () => {
            const url = page.url();
            const token = await page.evaluate((k) => localStorage.getItem(k), TOKEN_KEY);
            return !url.includes('/login') || !!token;
          },
          { timeout: 12_000, intervals: [500, 800, 1200] },
        )
        .toBeTruthy();

      const token = await page.evaluate((k) => localStorage.getItem(k), TOKEN_KEY);
      if (!token && page.url().includes('/login')) {
        throw new Error('仍在登录页');
      }
      return;
    } catch {
      if (i === maxAttempts - 1) break;
      await refreshCaptcha(page);
    }
  }

  throw new Error(`${account.username} 登录失败（验证码或密码错误，已重试 ${maxAttempts} 次）`);
}
