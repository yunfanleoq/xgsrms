import { expect, test } from '@playwright/test';
import { getAccount } from '../helpers/accounts';
import { apiCheckOnlyUser, apiLoginAttempt } from '../helpers/login-api';

test.describe('Auth 用户名枚举安全回归', () => {
  test('Auth-02a checkOnlyUser 存在/不存在用户 message 一致', async ({ request }) => {
    const existing = getAccount('test-qz').username;
    const missing = `no-such-user-${Date.now()}`;

    const bodyExists = await apiCheckOnlyUser(request, existing);
    const bodyMissing = await apiCheckOnlyUser(request, missing);

    expect(bodyExists.success, `存在用户校验应成功: ${JSON.stringify(bodyExists)}`).toBeTruthy();
    expect(bodyMissing.success, `不存在用户校验应成功: ${JSON.stringify(bodyMissing)}`).toBeTruthy();
    expect(String(bodyExists.message ?? '')).toBe(String(bodyMissing.message ?? ''));
    expect(bodyExists.result).toBe(false);
    expect(bodyMissing.result).toBe(true);
  });

  test('Auth-02b 登录失败文案不区分用户是否存在', async ({ request }) => {
    const existing = getAccount('test-qz').username;
    const missing = `no-such-user-${Date.now()}`;
    const wrongPassword = 'WrongPass@2026!';

    const resExists = await apiLoginAttempt(request, existing, wrongPassword);
    const resMissing = await apiLoginAttempt(request, missing, wrongPassword);

    expect(resExists.body.success, `存在用户错误密码: ${JSON.stringify(resExists.body)}`).toBeFalsy();
    expect(resMissing.body.success, `不存在用户: ${JSON.stringify(resMissing.body)}`).toBeFalsy();

    const msgExists = String(resExists.body.message ?? '');
    const msgMissing = String(resMissing.body.message ?? '');
    expect(msgExists).toBe(msgMissing);
    expect(msgExists).toMatch(/用户名或密码错误/);
  });
});
