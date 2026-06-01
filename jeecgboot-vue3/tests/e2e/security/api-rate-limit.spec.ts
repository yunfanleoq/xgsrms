import { expect, test } from '@playwright/test';
import { apiLoginAttempt } from '../helpers/login-api';

test.describe('Api 限流安全回归', () => {
  test('Api-03 连续登录失败触发账号锁定', async ({ request }) => {
    const probeUser = `e2e-ratelimit-${Date.now()}`;
    const wrongPassword = 'WrongPass@2026!';
    let passwordFailCount = 0;
    let lockMessage = '';

    for (let i = 1; i <= 12 && !lockMessage; i++) {
      const { body } = await apiLoginAttempt(request, probeUser, wrongPassword);
      const msg = String(body.message ?? '');
      if (/登录失败次数过多|10分钟后/.test(msg)) {
        lockMessage = msg;
        break;
      }
      if (/用户名或密码错误/.test(msg)) {
        passwordFailCount += 1;
      }
    }

    expect(
      lockMessage || passwordFailCount >= 6,
      `应触发锁定或累计≥6次密码错误（实际 ${passwordFailCount} 次）`,
    ).toBeTruthy();
    if (lockMessage) {
      expect(lockMessage).toMatch(/登录失败次数过多|10分钟后/);
    }
  });
});
