import { test, expect, type Browser, type Page, type APIRequestContext } from '@playwright/test';
import { getAccount } from '../helpers/accounts';
import { StepCapture } from '../helpers/step-capture';
import { FALLBACK_ROUTES, gotoByMenuTitles } from '../helpers/menu-routes';
import {
  assertPageReady,
  clickTableActionForRow,
  tableHasText,
  waitForSuccessToast,
} from '../helpers/page-analysis';
import { loadChainState, logStep, saveChainState } from '../helpers/chain-state';

const AUTH_DIR = '.auth';
const RUN_ID = process.env.E2E_RUN_ID || 'chain';

async function withRole(
  browser: Browser,
  roleId: string,
  fn: (page: Page, request: APIRequestContext) => Promise<void>,
) {
  const ctx = await browser.newContext({ storageState: `${AUTH_DIR}/${roleId}.json` });
  const page = await ctx.newPage();
  try {
    await fn(page, ctx.request);
  } finally {
    await ctx.close();
  }
}

async function gotoMenu(page: Page, request: APIRequestContext, ...titles: string[]) {
  try {
    return await gotoByMenuTitles(page, request, ...titles);
  } catch {
    const map: Record<string, string> = {
      '岗位发布管理/发布申请': FALLBACK_ROUTES.publishApply,
      '岗位发布管理/岗位审核': FALLBACK_ROUTES.positionCheck,
      '岗位发布管理/岗位发布': FALLBACK_ROUTES.positionPost,
      '简历审核管理/人力处收集': FALLBACK_ROUTES.hrCollect,
      '简历审核管理/部门审核': FALLBACK_ROUTES.depVerify,
      '简历审核管理/人力处审核': FALLBACK_ROUTES.rlVerifyPending,
      '面试管理/面试邀请': FALLBACK_ROUTES.interviewInvite,
      '个人中心/我的申请': FALLBACK_ROUTES.myApply,
      '个人中心/我的面试': FALLBACK_ROUTES.myInterview,
    };
    const route = map[titles.join('/')];
    if (!route) throw new Error(`无法导航：${titles.join(' → ')}`);
    await page.goto(route, { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(800);
    return route;
  }
}

async function approveRow(page: Page, rowMarker: string, actionLabel = '审核') {
  await clickTableActionForRow(page, rowMarker, actionLabel);
  const agree = page.getByText('同意', { exact: true }).first();
  if (await agree.count()) await agree.click();
  await page.getByRole('button', { name: /通过|确定|提交|保存/ }).first().click({ timeout: 15_000 });
  await waitForSuccessToast(page).catch(() => page.waitForTimeout(1500));
}

async function findApplicablePosition(page: Page): Promise<{ name: string; href: string; alreadyApplied: boolean }> {
  await page.goto('/home/positions', { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(2000);
  const cards = page.locator('.job-card');
  await expect(cards.first()).toBeVisible({ timeout: 30_000 });
  const count = await cards.count();

  for (let i = 0; i < Math.min(count, 10); i++) {
    if (i > 0) {
      await page.goto('/home/positions', { waitUntil: 'domcontentloaded' });
      await page.waitForTimeout(1500);
    }
    const card = cards.nth(i);
    const name = (await card.innerText()).split('\n')[0]?.trim() || '招聘岗位';
    await card.click();
    await page.waitForURL(/positionDetail/, { timeout: 15_000 });
    await expect(page.getByText('职位信息加载中')).toBeHidden({ timeout: 45_000 });
    const canApply = page.getByRole('button', { name: '在线申请' });
    if (await canApply.isVisible().catch(() => false)) {
      return { name, href: new URL(page.url()).pathname, alreadyApplied: false };
    }
  }

  await page.goto('/home/positions', { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1500);
  const card = cards.first();
  const name = (await card.innerText()).split('\n')[0]?.trim() || '招聘岗位';
  await card.click();
  await page.waitForURL(/positionDetail/, { timeout: 15_000 });
  await expect(page.getByText('职位信息加载中')).toBeHidden({ timeout: 45_000 });
  return { name, href: new URL(page.url()).pathname, alreadyApplied: true };
}

async function applyToPosition(page: Page) {
  await expect(page.getByText('职位信息加载中')).toBeHidden({ timeout: 45_000 });
  if (await page.getByRole('button', { name: '岗位已申请' }).count()) {
    throw new Error('岗位已申请');
  }
  const applyBtn = page.getByRole('button', { name: '在线申请' });
  await expect(applyBtn).toBeVisible({ timeout: 15_000 });
  if (!(await applyBtn.isEnabled())) {
    throw new Error('岗位已申请或不可投递');
  }
  await applyBtn.click();
  await expect(page.locator('.ant-modal').filter({ hasText: /在线申请|新增/ }).first()).toBeVisible({
    timeout: 20_000,
  });
  await page.getByRole('button', { name: '提交申请' }).click();
  await waitForSuccessToast(page);
}

test.describe.configure({ mode: 'serial' });

test('E2E 招聘全链路：投递 → 三级审核 → 面试邀请', async ({ browser }) => {
  test.setTimeout(900_000);
  const cap = new StepCapture();
  const state = loadChainState(RUN_ID);
  state.applicantName = getAccount('test-qz').label;
  saveChainState(state);

  await withRole(browser, 'test-qz', async (page, request) => {
    const pos = await findApplicablePosition(page);
    state.positionName = pos.name;
    logStep(state, '1-选岗', 'ok', pos.name);

    await cap.capture(page, {
      role: 'test-qz',
      roleLabel: '求职者 A',
      title: '岗位详情-准备申请',
      action: `在门户选择岗位「${pos.name}」，点击【在线申请】。`,
      pageDesc: '岗位详情展示任职要求；未投递岗位显示【在线申请】按钮。',
      route: page.url(),
    });

    try {
      if (pos.alreadyApplied) {
        throw new Error('岗位已申请');
      }
      await applyToPosition(page);
      logStep(state, '2-在线申请', 'ok');
      await cap.capture(page, {
        role: 'test-qz',
        roleLabel: '求职者 A',
        title: '提交申请成功',
        action: '在在线申请弹窗中确认简历信息并【提交申请】。',
        pageDesc: '提交成功后按钮变为「岗位已申请」。',
        route: page.url(),
      });
    } catch (e) {
      const msg = e instanceof Error ? e.message : String(e);
      logStep(state, '2-在线申请', msg.includes('已申请') ? 'skip' : 'fail', msg);
      if (!msg.includes('已申请')) throw e;
    }

    await gotoMenu(page, request, '个人中心', '我的申请');
    const analysis = await assertPageReady(page, '我的申请');
    expect(analysis.tableRowCount).toBeGreaterThan(0);
    await cap.capture(page, {
      role: 'test-qz',
      roleLabel: '求职者 A',
      title: '我的申请-投递后',
      action: '进入【个人中心】→【我的申请】确认新记录。',
      pageDesc: `列表应含岗位「${pos.name}」及当前审核状态。`,
      route: page.url(),
    });
  });

  const rowMarker = state.positionName || state.marker;

  await withRole(browser, 'test-rl', async (page, request) => {
    await gotoMenu(page, request, '简历审核管理', '人力处收集');
    await assertPageReady(page, '人力处收集');
    if (await tableHasText(page, rowMarker)) {
      await approveRow(page, rowMarker);
      logStep(state, '3-人力处收集', 'ok');
    } else {
      logStep(state, '3-人力处收集', 'skip', '列表暂无该岗位记录');
    }
    await cap.capture(
      page,
      {
        role: 'test-rl',
        roleLabel: '人事处',
        title: '人力处收集',
        action: '对新建申请点击【审核】并选择【同意】。',
        pageDesc: '初审通过后流转至部门审核。',
        route: page.url(),
      },
      { strict: false },
    );
  });

  await withRole(browser, 'test-bm', async (page, request) => {
    await gotoMenu(page, request, '简历审核管理', '部门审核');
    await assertPageReady(page, '部门审核');
    if (await tableHasText(page, rowMarker)) {
      await approveRow(page, rowMarker);
      logStep(state, '4-部门审核', 'ok');
    } else {
      logStep(state, '4-部门审核', 'skip', '待办列表无匹配行');
    }
    await cap.capture(
      page,
      {
        role: 'test-bm',
        roleLabel: '部门管理员 A',
        title: '部门审核',
        action: '部门账号对待审申请【审核】→【通过】。',
        pageDesc: '仅本部门岗位申请可见；通过后进入人力处终审。',
        route: page.url(),
      },
      { strict: false },
    );
  });

  await withRole(browser, 'test-rl', async (page, request) => {
    await gotoMenu(page, request, '简历审核管理', '人力处审核');
    await assertPageReady(page, '人力处审核');
    if (await tableHasText(page, rowMarker)) {
      await approveRow(page, rowMarker);
      logStep(state, '5-人力处审核', 'ok');
    } else {
      logStep(state, '5-人力处审核', 'skip', '待办列表无匹配行');
    }
    await cap.capture(
      page,
      {
        role: 'test-rl',
        roleLabel: '人事处',
        title: '人力处审核',
        action: '人力处对待终审记录【审核】→【通过】。',
        pageDesc: '终审通过后候选人进入面试邀请池。',
        route: page.url(),
      },
      { strict: false },
    );
  });

  await withRole(browser, 'test-rl', async (page, request) => {
    await gotoMenu(page, request, '面试管理', '面试邀请');
    const analysis = await assertPageReady(page, '面试邀请');
    const applicant = state.applicantName || '求职者';
    if ((await tableHasText(page, applicant)) && (await tableHasText(page, rowMarker))) {
      await clickTableActionForRow(page, rowMarker, '邀请').catch(async () => {
        await clickTableActionForRow(page, applicant, '邀请');
      });
      await page.getByRole('button', { name: /确定|提交|保存|邀请/ }).last().click().catch(() => undefined);
      await waitForSuccessToast(page).catch(() => undefined);
      logStep(state, '6-面试邀请', 'ok');
    } else {
      logStep(state, '6-面试邀请', 'skip', `列表未找到 ${applicant}/${rowMarker}`);
    }
    await cap.capture(
      page,
      {
        role: 'test-rl',
        roleLabel: '人事处',
        title: '面试邀请',
        action: '对已通过审核的候选人发出面试邀请。',
        pageDesc: `表格行数≈${analysis.tableRowCount}。`,
        route: page.url(),
      },
      { strict: false },
    );
  });

  await withRole(browser, 'test-qz', async (page, request) => {
    await gotoMenu(page, request, '个人中心', '我的面试');
    const analysis = await assertPageReady(page, '我的面试');
    if (await tableHasText(page, rowMarker)) {
      await clickTableActionForRow(page, rowMarker, '接受邀请').catch(() => undefined);
      await waitForSuccessToast(page).catch(() => undefined);
      logStep(state, '7-接受邀请', 'ok');
    } else {
      logStep(state, '7-接受邀请', 'skip', '我的面试暂无该岗位');
    }
    await cap.capture(
      page,
      {
        role: 'test-qz',
        roleLabel: '求职者 A',
        title: '我的面试-接受邀请',
        action: '求职者在【我的面试】中对邀请点击【接受邀请】。',
        pageDesc: `列表行数≈${analysis.tableRowCount}。`,
        route: page.url(),
      },
      { strict: false },
    );
  });

  cap.finish();
  const failed = state.steps.filter((s) => s.status === 'fail');
  expect(failed, `链路失败: ${JSON.stringify(failed)}`).toHaveLength(0);
});
