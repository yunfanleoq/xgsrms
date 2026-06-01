import { expect, test } from '@playwright/test';
import {
  apiGet,
  apiPostJson,
  fetchPageRecords,
  isAccessDenied,
  parseJsonBody,
  readTokenFromAuthFile,
} from '../helpers/api-auth';

test.describe('Authz IDOR 安全回归', () => {
  test('Authz-01 低权限不可访问 sys/user/list', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    const res = await apiGet(request, token, '/sys/user/list', { pageNo: 1, pageSize: 5 });
    const body = await parseJsonBody(res);
    expect(isAccessDenied(res.status(), body), `test-qz 不应访问用户列表: ${JSON.stringify(body)}`).toBeTruthy();
  });

  test('Authz-02 求职者 B 不可读/改求职者 A 的简历', async ({ request }) => {
    const tokenA = readTokenFromAuthFile('test-qz');
    const tokenB = readTokenFromAuthFile('test-qz-b');

    const resumes = await fetchPageRecords(request, tokenA, '/xgsResume/xgsResumeBase/listMine');
    test.skip(resumes.length === 0, 'test-qz 暂无简历，跳过 IDOR 读改测');

    const victimId = String(resumes[0].id);
    const readRes = await apiGet(request, tokenB, '/xgsResume/xgsResumeBase/queryById', { id: victimId });
    const readBody = await parseJsonBody(readRes);
    expect(
      isAccessDenied(readRes.status(), readBody),
      `B 不应读取 A 简历 ${victimId}: ${JSON.stringify(readBody)}`,
    ).toBeTruthy();

    const editRes = await apiPostJson(request, tokenB, '/xgsResume/xgsResumeBase/edit', {
      id: victimId,
      name: 'idor-probe',
    });
    const editBody = await parseJsonBody(editRes);
    expect(
      isAccessDenied(editRes.status(), editBody),
      `B 不应编辑 A 简历 ${victimId}: ${JSON.stringify(editBody)}`,
    ).toBeTruthy();
  });

  test('Authz-03 求职者 B 不可导出求职者 A 的岗位申请', async ({ request }) => {
    const tokenA = readTokenFromAuthFile('test-qz');
    const tokenB = readTokenFromAuthFile('test-qz-b');

    const applies = await fetchPageRecords(request, tokenA, '/positions/xgsPositionApply/listMine');
    test.skip(applies.length === 0, 'test-qz 暂无申请，跳过导出 IDOR');

    const applyId = String(applies[0].id);
    const exportRes = await apiGet(request, tokenB, '/positions/xgsPositionApply/exportXls', {
      selections: applyId,
    });
    const exportBody = await parseJsonBody(exportRes);
    expect(
      isAccessDenied(exportRes.status(), exportBody),
      `B 导出 A 申请应拒绝，实际 HTTP ${exportRes.status()} ${JSON.stringify(exportBody)}`,
    ).toBeTruthy();
  });

  test('Authz-04 求职者不可访问岗位申请全量 list', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    const res = await apiGet(request, token, '/positions/xgsPositionApply/list', {
      pageNo: 1,
      pageSize: 10,
      approvalNode: 'dept',
    });
    const body = await parseJsonBody(res);
    expect(isAccessDenied(res.status(), body), `test-qz 不应访问全量申请 list: ${JSON.stringify(body)}`).toBeTruthy();
  });

  test('Authz-04b 简历 list 仅返回本人数据（无管理权限）', async ({ request }) => {
    const tokenQz = readTokenFromAuthFile('test-qz');
    const tokenQzB = readTokenFromAuthFile('test-qz-b');

    const listA = await fetchPageRecords(request, tokenQz, '/xgsResume/xgsResumeBase/list');
    const listB = await fetchPageRecords(request, tokenQzB, '/xgsResume/xgsResumeBase/list');

    for (const row of listA) {
      expect(String(row.createBy ?? '')).toMatch(/test-qz$/);
    }
    for (const row of listB) {
      expect(String(row.createBy ?? '')).toMatch(/test-qz-b$/);
    }

    const idsA = new Set(listA.map((r) => String(r.id)));
    const overlap = listB.filter((r) => idsA.has(String(r.id)));
    expect(overlap, 'A/B 简历 list 不应交叉').toHaveLength(0);
  });

  test('Authz-05 listMine 响应不含他人绝对路径字段泄露', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    const res = await apiGet(request, token, '/positions/xgsPositionApply/listMine', {
      pageNo: 1,
      pageSize: 20,
    });
    const body = await parseJsonBody(res);
    expect(body.success).toBeTruthy();
    const raw = JSON.stringify(body.result ?? {});
    expect(raw).not.toMatch(/\/www\/wwwroot|C:\\\\|\/etc\//i);
  });

  test('Authz-06 低权限不可访问数据源与 Quartz', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');

    for (const path of ['/sys/dataSource/list', '/sys/quartzJob/list'] as const) {
      const res = await apiGet(request, token, path, { pageNo: 1, pageSize: 5 });
      const body = await parseJsonBody(res);
      expect(isAccessDenied(res.status(), body), `${path} 应拒绝 test-qz: ${JSON.stringify(body)}`).toBeTruthy();
    }
  });

  test('Authz-06b admin 可访问数据源 list（垂直权限对照）', async ({ request }) => {
    const token = readTokenFromAuthFile('admin');
    const res = await apiGet(request, token, '/sys/dataSource/list', { pageNo: 1, pageSize: 5 });
    const body = await parseJsonBody(res);
    expect(body.success, `admin 应可访问数据源: ${JSON.stringify(body)}`).toBeTruthy();
  });
});
