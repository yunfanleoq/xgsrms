export interface E2eAccount {
  id: string;
  label: string;
  username: string;
  password: string;
  authFile: string;
}

export const E2E_ACCOUNTS: E2eAccount[] = [
  {
    id: 'test-qz',
    label: '求职者 A',
    username: process.env.E2E_USER_QZ || 'test-qz',
    password: process.env.E2E_PASS_QZ || 'test-qz@2026',
    authFile: '.auth/test-qz.json',
  },
  {
    id: 'test-qz-b',
    label: '求职者 B',
    username: process.env.E2E_USER_QZ_B || 'test-qz-b',
    password: process.env.E2E_PASS_QZ_B || 'test-qz-b@2026',
    authFile: '.auth/test-qz-b.json',
  },
  {
    id: 'test-bm',
    label: '部门管理员 A',
    username: process.env.E2E_USER_BM || 'test-bm',
    password: process.env.E2E_PASS_BM || 'test-bm@2026',
    authFile: '.auth/test-bm.json',
  },
  {
    id: 'test-bm-b',
    label: '部门管理员 B',
    username: process.env.E2E_USER_BM_B || 'test-bm-b',
    password: process.env.E2E_PASS_BM_B || 'test-bm-b@2026',
    authFile: '.auth/test-bm-b.json',
  },
  {
    id: 'test-rl',
    label: '人事处',
    username: process.env.E2E_USER_RL || 'test-rl',
    password: process.env.E2E_PASS_RL || 'test-rl@2026',
    authFile: '.auth/test-rl.json',
  },
  {
    id: 'admin',
    label: '系统管理员',
    username: process.env.E2E_USER_ADMIN || 'admin',
    password: process.env.E2E_PASS_ADMIN || 'zktw@2019',
    authFile: '.auth/admin.json',
  },
];

export function getAccount(id: string): E2eAccount {
  const acc = E2E_ACCOUNTS.find((a) => a.id === id);
  if (!acc) throw new Error(`未知测试账号: ${id}`);
  return acc;
}
