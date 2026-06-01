import { execFileSync } from 'child_process';
import fs from 'fs';
import path from 'path';
import type { APIRequestContext } from '@playwright/test';
import { buildApiUrl, parseJsonBody } from './api-auth';

function ocrScriptPath(): string {
  const fromEnv = process.env.E2E_CAPTCHA_OCR_SCRIPT;
  if (fromEnv && fs.existsSync(fromEnv)) return fromEnv;
  return path.resolve(__dirname, '../../../../scripts/e2e_captcha_ocr.py');
}

function runOcr(imageBase64: string): string {
  const script = ocrScriptPath();
  const py = process.env.E2E_PYTHON || 'py';
  const out = execFileSync(py, ['-3', script, imageBase64], { encoding: 'utf-8', timeout: 30_000 }).trim();
  if (out.length < 4) throw new Error(`OCR 结果无效: ${out}`);
  return out.slice(0, 4);
}

/** 拉取图形验证码并 OCR（用于 checkOnlyUser / API 登录） */
export async function fetchCaptchaChallenge(request: APIRequestContext): Promise<{ checkKey: string; captcha: string }> {
  const checkKey = `sec-${Date.now()}`;
  const res = await request.get(buildApiUrl(`/sys/randomImage/${checkKey}`), { failOnStatusCode: false });
  const body = await parseJsonBody(res);
  const dataUrl = String(body.result ?? '');
  if (!dataUrl.startsWith('data:image')) {
    throw new Error(`验证码 API 异常: ${JSON.stringify(body)}`);
  }
  return { checkKey, captcha: runOcr(dataUrl) };
}
