import { test, expect, type Browser, type APIRequestContext } from '@playwright/test';
import { getAccount } from '../helpers/accounts';
import { StepCapture } from '../helpers/step-capture';
import { FALLBACK_ROUTES, gotoByMenuTitles } from '../helpers/menu-routes';

const AUTH_DIR = '.auth';

interface PageVisit {
  route: string;
  menuPath?: string[];
  title: string;
  action: string;
  pageDesc: string;
  waitMs?: number;
}

const ROLE_PAGES: Record<string, PageVisit[]> = {
  'test-qz': [
    {
      route: '/home/index',
      title: '招聘门户首页',
      action: '登录成功后点击【返回首页】或访问门户首页，浏览滚动条、导航与公告区域。',
      pageDesc:
        '门户首页展示单位简介、新闻公告入口与「招聘信息」导航，求职者可在此进入岗位列表，无需管理端菜单权限。',
    },
    {
      route: '/home/positions',
      title: '招聘岗位列表',
      action: '点击顶部或首页中的「招聘信息」，进入岗位筛选列表页，可按部门、类别筛选。',
      pageDesc: '列表展示已发布岗位名称、部门、学历要求等；点击岗位名称可进入详情并发起在线申请。',
    },
    {
      route: FALLBACK_ROUTES.myApply,
      menuPath: ['个人中心', '我的申请'],
      title: '个人中心-我的申请',
      action: '进入后台后打开【个人中心】→【我的申请】，查看历史投递记录与当前状态。',
      pageDesc: '展示求职者本人提交的岗位申请及审核进度，仅可见本人数据。',
      waitMs: 1500,
    },
    {
      route: FALLBACK_ROUTES.myInterview,
      menuPath: ['个人中心', '我的面试'],
      title: '个人中心-我的面试',
      action: '打开【个人中心】→【我的面试】，查看面试邀请与结果。',
      pageDesc: '列出人事处发出的面试邀请，求职者可在此接受/拒绝邀请并查看面试结论。',
      waitMs: 1500,
    },
  ],
  'test-bm': [
    {
      route: FALLBACK_ROUTES.publishApply,
      menuPath: ['岗位发布管理', '发布申请'],
      title: '岗位发布申请',
      action: '部门账号登录后台，进入【岗位发布管理】→【发布申请】。',
      pageDesc: '部门管理员在此新增岗位、填写招聘需求并提交发布申请，供人事处审核。',
    },
    {
      route: FALLBACK_ROUTES.depVerify,
      menuPath: ['简历审核管理', '部门审核'],
      title: '部门审核-待办',
      action: '进入【简历审核】→【部门审核】待办列表，查看本部门待审申请。',
      pageDesc: '仅展示本部门岗位的应聘者材料，部门审核员可填写意见并通过/驳回。',
      waitMs: 1500,
    },
  ],
  'test-rl': [
    {
      route: FALLBACK_ROUTES.positionCheck,
      menuPath: ['岗位发布管理', '岗位审核'],
      title: '岗位审核',
      action: '人事处账号进入【岗位审核】，查看各部门提交的岗位发布申请。',
      pageDesc: '人力处审核岗位信息是否符合发布规范，审核通过后可进入岗位发布环节。',
      waitMs: 1500,
    },
    {
      route: FALLBACK_ROUTES.positionPost,
      menuPath: ['岗位发布管理', '岗位发布'],
      title: '岗位发布',
      action: '进入【岗位发布】列表，确认已审核岗位并执行发布操作。',
      pageDesc: '发布后岗位在门户「招聘信息」中对外可见，求职者方可投递。',
      waitMs: 1500,
    },
    {
      route: FALLBACK_ROUTES.hrCollect,
      menuPath: ['简历审核管理', '人力处收集'],
      title: '人力处收集',
      action: '打开【简历审核管理】→【人力处收集】，处理新投递的应聘材料。',
      pageDesc: '人力处初审收集各渠道申请，通过后进入部门审核环节。',
      waitMs: 1500,
    },
    {
      route: FALLBACK_ROUTES.rlVerifyPending,
      menuPath: ['简历审核管理', '人力处审核'],
      title: '人力处审核-待办',
      action: '打开【简历审核】→【人力处审核】待办，处理部门已通过的材料。',
      pageDesc: '人力处终审，通过后进入面试邀请候选池。',
      waitMs: 1500,
    },
    {
      route: FALLBACK_ROUTES.interviewInvite,
      menuPath: ['面试管理', '面试邀请'],
      title: '面试邀请',
      action: '进入【面试管理】→【面试邀请】，查看待邀请人员并发出邀请或录入结论。',
      pageDesc: '展示已通过审核的候选人，可填写面试时间地点、发送邀请，并标记通过/未通过。',
      waitMs: 1500,
    },
  ],
  admin: [
    {
      route: '/system/user',
      menuPath: ['系统管理', '用户管理'],
      title: '系统管理-用户管理',
      action: '管理员登录后进入【系统管理】→【用户管理】。',
      pageDesc: '维护系统登录账号、绑定角色与部门，测试账号 test-qz/test-bm 等均在此配置。',
      waitMs: 1500,
    },
    {
      route: '/system/role',
      menuPath: ['系统管理', '角色管理'],
      title: '系统管理-角色管理',
      action: '打开【角色管理】，查看求职者、部门、人力处等角色权限树。',
      pageDesc: '配置菜单与按钮权限，决定各角色可见的功能模块。',
      waitMs: 1500,
    },
  ],
};

async function navigateVisit(page: import('@playwright/test').Page, request: APIRequestContext, visit: PageVisit) {
  if (visit.menuPath?.length) {
    try {
      return await gotoByMenuTitles(page, request, ...visit.menuPath);
    } catch {
      /* fallback */
    }
  }
  await page.goto(visit.route, { waitUntil: 'domcontentloaded' });
  return visit.route;
}

async function captureLoginPage(browser: Browser, cap: StepCapture) {
  const anonCtx = await browser.newContext();
  const anonPage = await anonCtx.newPage();
  await anonPage.goto('/login', { waitUntil: 'domcontentloaded', timeout: 60_000 });
  await cap.capture(anonPage, {
    role: 'anonymous',
    roleLabel: '未登录访客',
    title: '系统登录页',
    action: '浏览器访问系统地址，展示登录表单：用户名、密码、图形验证码、记住我、登录/注册/返回首页。',
    pageDesc: '统一认证入口；生产环境强制图形验证码，防止暴力破解与用户名枚举。',
    route: '/login',
  });
  await anonCtx.close();

  const acc = getAccount('test-qz');
  const ctx = await browser.newContext({ storageState: `${AUTH_DIR}/test-qz.json` });
  const page = await ctx.newPage();
  await page.goto('/dashboard/analysis', { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(1200);
  await cap.capture(page, {
    role: acc.id,
    roleLabel: acc.label,
    title: '登录成功-工作台',
    action: `使用账号 ${acc.username} 完成登录（复用 setup 登录态），进入后台工作台。`,
    pageDesc: '登录成功后加载用户菜单与权限路由，右上角显示当前用户姓名。',
    route: page.url(),
  });
  await ctx.close();
}

async function walkRolePages(browser: Browser, cap: StepCapture, roleId: string) {
  const acc = getAccount(roleId);
  const ctx = await browser.newContext({ storageState: `${AUTH_DIR}/${roleId}.json` });
  const page = await ctx.newPage();
  const request = ctx.request;

  for (const v of ROLE_PAGES[roleId] || []) {
    const route = await navigateVisit(page, request, v);
    if (v.waitMs) await page.waitForTimeout(v.waitMs);
    await cap.capture(page, {
      role: acc.id,
      roleLabel: acc.label,
      title: v.title,
      action: v.action,
      pageDesc: v.pageDesc,
      route,
    });
    expect(page.url()).not.toContain('/login');
  }
  await ctx.close();
}

test.describe.configure({ mode: 'serial' });

test('E2E 文档采集：登录与各角色核心页面', async ({ browser }) => {
  test.setTimeout(600_000);
  const cap = new StepCapture();

  await captureLoginPage(browser, cap);
  for (const roleId of ['test-qz', 'test-bm', 'test-rl', 'admin']) {
    await walkRolePages(browser, cap, roleId);
  }

  for (const roleId of ['test-qz-b', 'test-bm-b']) {
    const acc = getAccount(roleId);
    const ctx = await browser.newContext({ storageState: `${AUTH_DIR}/${roleId}.json` });
    const page = await ctx.newPage();
    await page.goto('/dashboard/analysis', { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(1200);
    await cap.capture(page, {
      role: acc.id,
      roleLabel: acc.label,
      title: '后台首页',
      action: `使用 ${acc.username} 登录后进入系统默认首页，用于 IDOR 测试对照账号验证。`,
      pageDesc: `${acc.label} 与主账号权限相同但数据归属不同，安全测试用于水平越权对比。`,
      route: '/dashboard/analysis',
    });
    await ctx.close();
  }

  process.env.E2E_LAST_RUN_DIR = cap.finish();
});
