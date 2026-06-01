import fs from 'fs';
import path from 'path';
import type { APIRequestContext, APIResponse } from '@playwright/test';

export function buildApiUrl(relativePath: string): string {
  const base = (process.env.E2E_BASE_URL || 'https://10.28.12.15').replace(/\/$/, '');
  const prefix = (process.env.E2E_API_PREFIX || '/xgszp-boot').replace(/\/$/, '');
  const rel = relativePath.startsWith('/') ? relativePath : `/${relativePath}`;
  return `${base}${prefix}${rel}`;
}

const TOKEN_KEY = 'TOKEN__';
const AUTH_DIR = path.resolve(process.cwd(), '.auth');

function extractTokenFromEntry(name: string, raw: string): string | null {
  if (raw.startsWith('eyJ')) {
    return raw;
  }
  try {
    const parsed = JSON.parse(raw) as Record<string, unknown>;
    const direct = parsed.value;
    if (typeof direct === 'string' && direct.startsWith('eyJ')) {
      return direct;
    }
    const bucket = (direct ?? parsed) as Record<string, unknown>;
    const tokenNode = bucket[TOKEN_KEY] as { value?: string } | string | undefined;
    if (typeof tokenNode === 'string' && tokenNode.startsWith('eyJ')) {
      return tokenNode;
    }
    if (tokenNode && typeof tokenNode === 'object' && typeof tokenNode.value === 'string') {
      return tokenNode.value;
    }
    const loginInfo = bucket.LOGIN__INFO__ as { value?: { token?: string } } | undefined;
    if (typeof loginInfo?.value?.token === 'string') {
      return loginInfo.value.token;
    }
  } catch {
    // ignore malformed cache blob
  }
  if (name.includes('LOGIN__INFO__') && raw.includes('eyJ')) {
    const m = raw.match(/"token":"(eyJ[^"]+)"/);
    if (m?.[1]) return m[1];
  }
  return null;
}

export function readTokenFromAuthFile(accountId: string): string {
  const authPath = path.join(AUTH_DIR, `${accountId}.json`);
  if (!fs.existsSync(authPath)) {
    throw new Error(`缺少 ${authPath}，请先执行: npm run test:e2e:setup`);
  }
  const state = JSON.parse(fs.readFileSync(authPath, 'utf-8')) as {
    origins?: { localStorage?: { name: string; value: string }[] }[];
  };
  for (const origin of state.origins ?? []) {
    for (const item of origin.localStorage ?? []) {
      const token = extractTokenFromEntry(item.name, item.value);
      if (token) {
        return token;
      }
    }
  }
  throw new Error(`${authPath} 中未找到 JWT（${TOKEN_KEY}）`);
}

export async function parseJsonBody(response: APIResponse): Promise<Record<string, unknown>> {
  const text = await response.text();
  if (!text) return {};
  try {
    return JSON.parse(text) as Record<string, unknown>;
  } catch {
    return { _raw: text.slice(0, 500) };
  }
}

/** Jeecg/Shiro：HTTP 401/403 或业务码无权限 */
export function isAccessDenied(status: number, body: Record<string, unknown>): boolean {
  if (status === 401 || status === 403) return true;
  const code = body.code;
  if (code === 401 || code === 403 || code === 510) return true;
  const msg = String(body.message ?? body.msg ?? '');
  return /无权限|没有权限|不允许|Unauthorized|permission|denied/i.test(msg);
}

export async function apiGet(
  request: APIRequestContext,
  token: string,
  apiPath: string,
  params?: Record<string, string | number>,
) {
  const url = new URL(buildApiUrl(apiPath));
  if (params) {
    for (const [k, v] of Object.entries(params)) {
      url.searchParams.set(k, String(v));
    }
  }
  return request.get(url.toString(), {
    headers: { 'X-Access-Token': token },
    failOnStatusCode: false,
  });
}

export async function apiPostJson(
  request: APIRequestContext,
  token: string,
  apiPath: string,
  data: unknown,
) {
  return request.post(buildApiUrl(apiPath), {
    headers: {
      'X-Access-Token': token,
      'Content-Type': 'application/json',
    },
    data,
    failOnStatusCode: false,
  });
}

export interface JeecgPageResult<T = Record<string, unknown>> {
  records?: T[];
  total?: number;
}

export async function fetchPageRecords<T = Record<string, unknown>>(
  request: APIRequestContext,
  token: string,
  apiPath: string,
  params?: Record<string, string | number>,
): Promise<T[]> {
  const res = await apiGet(request, token, apiPath, { pageNo: 1, pageSize: 50, ...params });
  const body = await parseJsonBody(res);
  const page = (body.result ?? {}) as JeecgPageResult<T>;
  return page.records ?? [];
}
