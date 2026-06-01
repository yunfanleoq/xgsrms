import type { APIRequestContext, Page } from '@playwright/test';

export interface MenuNode {
  path?: string;
  name?: string;
  meta?: { title?: string };
  children?: MenuNode[];
  component?: string;
}

const API_PREFIX = process.env.E2E_API_PREFIX || '/xgszp-boot';

export async function readToken(page: Page): Promise<string> {
  return page.evaluate((key) => {
    const direct = localStorage.getItem(key);
    if (direct?.startsWith('eyJ')) return direct;

    function extract(name: string, raw: string): string | null {
      if (raw.startsWith('eyJ')) return raw;
      try {
        const parsed = JSON.parse(raw) as Record<string, unknown>;
        const bucket = (parsed.value ?? parsed) as Record<string, unknown>;
        const tokenNode = bucket[key] as { value?: string } | string | undefined;
        if (typeof tokenNode === 'string' && tokenNode.startsWith('eyJ')) return tokenNode;
        if (tokenNode && typeof tokenNode === 'object' && typeof tokenNode.value === 'string') {
          return tokenNode.value;
        }
        const loginInfo = bucket.LOGIN__INFO__ as { value?: { token?: string } } | undefined;
        if (typeof loginInfo?.value?.token === 'string') return loginInfo.value.token;
      } catch {
        /* ignore */
      }
      if (name.includes('LOGIN__INFO__') && raw.includes('eyJ')) {
        const m = raw.match(/"token":"(eyJ[^"]+)"/);
        if (m?.[1]) return m[1];
      }
      return null;
    }

    for (let i = 0; i < localStorage.length; i++) {
      const name = localStorage.key(i)!;
      const raw = localStorage.getItem(name) ?? '';
      const token = extract(name, raw);
      if (token) return token;
    }
    return '';
  }, 'TOKEN__');
}

export async function fetchMenuTree(request: APIRequestContext, token: string): Promise<MenuNode[]> {
  const resp = await request.get(`${API_PREFIX}/sys/permission/getUserPermissionByToken`, {
    headers: {
      'X-Access-Token': token,
      Authorization: token,
    },
  });
  if (!resp.ok()) {
    throw new Error(`拉取菜单失败 HTTP ${resp.status()}`);
  }
  const body = await resp.json();
  const result = body?.result ?? body;
  const menu = result?.menu ?? result?.auth ?? result;
  if (Array.isArray(menu)) return menu;
  if (Array.isArray(result?.menu)) return result.menu;
  return [];
}

function flattenMenus(nodes: MenuNode[], out: MenuNode[] = []): MenuNode[] {
  for (const n of nodes) {
    if (n.path && n.path !== '/') out.push(n);
    if (n.children?.length) flattenMenus(n.children, out);
  }
  return out;
}

export function menuTitle(node: MenuNode): string {
  return (node.meta?.title || node.name || (node as { title?: string }).title || '').trim();
}

function menuPath(node: MenuNode): string | null {
  const p = node.path || (node as { url?: string }).url;
  return p && p !== 'null' ? p : null;
}

/** 按菜单标题链解析 path，如 ['简历审核管理','部门审核'] */
export function resolveMenuPath(menus: MenuNode[], ...titles: string[]): string | null {
  let level: MenuNode[] = menus;
  let found: MenuNode | undefined;
  for (const title of titles) {
    found = level.find((m) => menuTitle(m).includes(title) || title.includes(menuTitle(m)));
    if (!found) return null;
    level = found.children || [];
  }
  return found ? menuPath(found) : null;
}

export async function gotoByMenuTitles(page: Page, request: APIRequestContext, ...titles: string[]) {
  const token = await readToken(page);
  const menus = await fetchMenuTree(request, token);
  const path = resolveMenuPath(menus, ...titles);
  if (!path) {
    throw new Error(`未找到菜单路径：${titles.join(' → ')}`);
  }
  await page.goto(path.startsWith('/') ? path : `/${path}`, { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(800);
  return path;
}

/** 侧栏点击（门户静态路由不适用） */
export async function clickSidebarMenu(page: Page, ...labels: string[]) {
  for (const label of labels) {
    const submenu = page.locator('.ant-menu-submenu-title, .ant-menu-item').filter({ hasText: label }).first();
    await submenu.waitFor({ state: 'visible', timeout: 15_000 });
    await submenu.click();
    await page.waitForTimeout(600);
  }
}

/** 生产 sys_permission 实测路径（菜单 API 失败时回退） */
export const FALLBACK_ROUTES = {
  myApply: '/xgsUserResume/xgsUserPositionApplyList',
  myInterview: '/xgsMyInterview/XgsInterviewList',
  publishApply: '/positions/xgsPositionPublishList',
  positionCheck: '/positionsCheck/XgsPositionsList',
  positionPost: '/postionsPost/XgsPositionsList',
  depVerify: '/xgsResumeApproval/departApproval/XgsPositionApplyList',
  rlVerifyPending: '/xgsResumeApproval/hrApproval/XgsPositionApplyList',
  hrCollect: '/xgsResumeApproval/hrLookApproval/XgsPositionApplyList',
  hrFinal: '/xgsResumeApproval/hrApproval/XgsPositionApplyList',
  deptApproval: '/xgsResumeApproval/departApproval/XgsPositionApplyList',
  interviewInvite: '/xgsInviteToInterview/xgsInviteToInterviewList',
} as const;
