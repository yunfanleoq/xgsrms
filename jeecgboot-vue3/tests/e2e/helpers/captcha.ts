import { execFileSync } from 'child_process';
import fs from 'fs';
import os from 'os';
import path from 'path';
import { expect, type Page } from '@playwright/test';

function ocrScriptPath(): string {
  const fromEnv = process.env.E2E_CAPTCHA_OCR_SCRIPT;
  if (fromEnv && fs.existsSync(fromEnv)) return fromEnv;
  return path.resolve(__dirname, '../../../../scripts/e2e_captcha_ocr.py');
}

function runOcr(imageBase64: string): string {
  const script = ocrScriptPath();
  const py = process.env.E2E_PYTHON || 'py';
  const args = ['-3', script, imageBase64];
  const out = execFileSync(py, args, { encoding: 'utf-8', timeout: 30_000 }).trim();
  if (out.length < 4) throw new Error(`OCR 结果无效: ${out}`);
  return out.slice(0, 4);
}

/** 从登录页验证码图片识别 4 位字符（须为 API 返回的 data: URL） */
export async function readCaptchaFromLoginPage(page: Page): Promise<string> {
  const img = page.locator('form img').first();
  await img.waitFor({ state: 'visible', timeout: 15_000 });

  await expect
    .poll(
      async () => {
        const src = (await img.getAttribute('src')) ?? '';
        return src.startsWith('data:image');
      },
      { timeout: 20_000, message: '验证码 API 未返回 data URL（后端可能未启动或 /sys/randomImage 502）' },
    )
    .toBeTruthy();

  const src = (await img.getAttribute('src'))!;
  if (src.startsWith('data:')) {
    return runOcr(src);
  }

  const absolute = new URL(src, page.url()).toString();
  const resp = await page.request.get(absolute);
  if (!resp.ok()) {
    throw new Error(`验证码图片 HTTP ${resp.status()} — ${absolute}`);
  }
  const buf = await resp.body();
  const tmp = path.join(os.tmpdir(), `e2e-captcha-${Date.now()}.png`);
  fs.writeFileSync(tmp, buf);
  try {
    return runOcr(tmp);
  } finally {
    fs.unlinkSync(tmp);
  }
}
export async function refreshCaptcha(page: Page): Promise<void> {
  await page.locator('form img').first().click();
  await page.waitForTimeout(400);
}
