import JSEncrypt from 'jsencrypt';
import type { APIRequestContext } from '@playwright/test';
import { buildApiUrl, parseJsonBody } from './api-auth';
import { fetchCaptchaChallenge } from './captcha-api';

interface LoginRsaBundle {
  rsaKeyId: string;
  publicKey: string;
}

async function fetchLoginRsaPublicKey(request: APIRequestContext): Promise<LoginRsaBundle> {
  const res = await request.get(buildApiUrl('/sys/loginRsaPublicKey'), { failOnStatusCode: false });
  const body = await parseJsonBody(res);
  const bundle = body.result as LoginRsaBundle;
  if (!bundle?.rsaKeyId || !bundle?.publicKey) {
    throw new Error(`获取 RSA 公钥失败: ${JSON.stringify(body)}`);
  }
  return bundle;
}

function encryptPasswordWithRsa(plainPassword: string, bundle: LoginRsaBundle): string {
  const enc = new JSEncrypt();
  enc.setPublicKey(bundle.publicKey);
  const cipher = enc.encrypt(plainPassword);
  if (!cipher) throw new Error('RSA 加密密码失败');
  return cipher;
}

export interface ApiLoginResult {
  status: number;
  body: Record<string, unknown>;
}

/** 通过 API 发起一次登录（含验证码 + RSA） */
export async function apiLoginAttempt(
  request: APIRequestContext,
  username: string,
  password: string,
): Promise<ApiLoginResult> {
  const { checkKey, captcha } = await fetchCaptchaChallenge(request);
  const rsa = await fetchLoginRsaPublicKey(request);
  const res = await request.post(buildApiUrl('/sys/login'), {
    headers: { 'Content-Type': 'application/json' },
    data: {
      username,
      password: encryptPasswordWithRsa(password, rsa),
      rsaKeyId: rsa.rsaKeyId,
      captcha,
      checkKey,
    },
    failOnStatusCode: false,
  });
  return { status: res.status(), body: await parseJsonBody(res) };
}

/** checkOnlyUser：存在用户 result=false，不存在 result=true；message 应统一 */
export async function apiCheckOnlyUser(
  request: APIRequestContext,
  username: string,
): Promise<Record<string, unknown>> {
  const { checkKey, captcha } = await fetchCaptchaChallenge(request);
  const res = await request.get(buildApiUrl('/sys/user/checkOnlyUser'), {
    params: { username, captcha, checkKey },
    failOnStatusCode: false,
  });
  return parseJsonBody(res);
}
