import { expect, test } from '@playwright/test';
import {
  apiPostJson,
  isAccessDenied,
  parseJsonBody,
  readTokenFromAuthFile,
} from '../helpers/api-auth';

const INTERNAL_JDBC = {
  dbType: 'MYSQL',
  dbDriver: 'com.mysql.cj.jdbc.Driver',
  dbUrl: 'jdbc:mysql://127.0.0.1:3306/jmreport_ssrf_probe',
  dbUsername: 'root',
  dbPassword: 'probe',
  name: 'ssrf-probe',
};

function isRepBlocked(body: Record<string, unknown>): boolean {
  if (body.success === true) return false;
  const msg = String(body.message ?? body.msg ?? body._raw ?? '');
  if (/连接成功|Connection successful|successfully connected/i.test(msg)) return false;
  return (
    body.success === false ||
    isAccessDenied(200, body) ||
    /不允许|禁止|安全|SSRF|内网|127\.0\.0\.1|localhost|发布模式|dataSource|操作失败|Cannot parse/i.test(msg)
  );
}

function hasSqlLeak(body: Record<string, unknown>): boolean {
  const raw = JSON.stringify(body);
  return /SQLSyntaxErrorException|java\.sql|mybatis|stacktrace|root@localhost|information_schema/i.test(raw);
}

test.describe('Rep JimuReport 安全回归', () => {
  test('Rep-02a 低权限不可 testConnection', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    const res = await apiPostJson(request, token, '/jmreport/testConnection', INTERNAL_JDBC);
    const body = await parseJsonBody(res);
    expect(
      isAccessDenied(res.status(), body),
      `test-qz 应不可 testConnection: ${JSON.stringify(body)}`,
    ).toBeTruthy();
  });

  test('Rep-02b admin testConnection 不可 SSRF 内网 JDBC', async ({ request }) => {
    const token = readTokenFromAuthFile('admin');
    const res = await apiPostJson(request, token, '/jmreport/testConnection', INTERNAL_JDBC);
    const body = await parseJsonBody(res);
    expect(
      isRepBlocked(body),
      `admin 内网 JDBC 应被阻断: ${JSON.stringify(body)}`,
    ).toBeTruthy();
  });

  test('Rep-03 admin queryFieldBySql 不可 SQL 注入', async ({ request }) => {
    const token = readTokenFromAuthFile('admin');
    const res = await apiPostJson(request, token, '/jmreport/queryFieldBySql', {
      sql: "select 1 union select user()",
      dbSource: 'master',
    });
    const body = await parseJsonBody(res);
    expect(body.success, `SQLi 不应成功: ${JSON.stringify(body)}`).not.toBeTruthy();
    expect(hasSqlLeak(body), `响应不应泄露 SQL/堆栈: ${JSON.stringify(body)}`).toBeFalsy();
  });
});
