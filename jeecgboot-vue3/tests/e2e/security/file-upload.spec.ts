import { expect, test } from '@playwright/test';
import {
  apiGet,
  apiPostJson,
  fetchPageRecords,
  isAccessDenied,
  parseJsonBody,
  readTokenFromAuthFile,
} from '../helpers/api-auth';
import { apiUploadFile, apiUploadOssFile, isUploadRejected } from '../helpers/api-upload';

const DANGEROUS_FILES: { name: string; content: string; mime: string }[] = [
  { name: 'evil.html', content: '<html><script>alert(1)</script></html>', mime: 'text/html' },
  { name: 'evil.svg', content: '<svg xmlns="http://www.w3.org/2000/svg"><script>alert(1)</script></svg>', mime: 'image/svg+xml' },
  { name: 'evil.jsp', content: '<%@ page contentType="text/html" %><script>alert(1)</script>', mime: 'application/octet-stream' },
  { name: 'evil.php.jpg', content: '<?php echo 1; ?>', mime: 'image/jpeg' },
];

test.describe('File 上传与 XSS 安全回归', () => {
  test('File-01 危险扩展名上传应拒绝', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    for (const sample of DANGEROUS_FILES) {
      const res = await apiUploadFile(request, token, sample.name, sample.content, sample.mime);
      const body = await parseJsonBody(res);
      expect(
        isUploadRejected(body),
        `${sample.name} 应被拒绝: ${JSON.stringify(body)}`,
      ).toBeTruthy();
    }
  });

  test('File-02 低权限不可使用 OSS 管理上传', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    const res = await apiUploadOssFile(request, token, 'probe.txt', 'hello');
    const body = await parseJsonBody(res);
    expect(
      isAccessDenied(res.status(), body) || isUploadRejected(body),
      `test-qz OSS 上传应拒绝: ${JSON.stringify(body)}`,
    ).toBeTruthy();
  });

  test('File-03 合法 PNG 可上传（对照）', async ({ request }) => {
    const token = readTokenFromAuthFile('test-qz');
    // 最小合法 PNG 1x1
    const png = Buffer.from(
      'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==',
      'base64',
    );
    const res = await apiUploadFile(request, token, 'probe-1x1.png', png, 'image/png');
    const body = await parseJsonBody(res);
    expect(body.success, `合法 PNG 应可上传: ${JSON.stringify(body)}`).toBeTruthy();
  });

  test('File-04 岗位富文本 XSS 入库应被净化', async ({ request }) => {
    const token = readTokenFromAuthFile('test-bm');
    const positions = await fetchPageRecords(request, token, '/positions/xgsPositions/list');
    test.skip(positions.length === 0, 'test-bm 无可编辑岗位，跳过 File-04');

    const target = positions[0];
    const id = String(target.id);
    const xssPayload = '<p>sec-probe</p><script>alert("xss")</script><img src=x onerror=alert(1)>';
    const originalDuty = String(target.duty ?? '');

    const editRes = await apiPostJson(request, token, '/positions/xgsPositions/edit', {
      ...target,
      id,
      duty: xssPayload,
    });
    const editBody = await parseJsonBody(editRes);
    expect(editBody.success, `编辑岗位应成功: ${JSON.stringify(editBody)}`).toBeTruthy();

    try {
      const readRes = await apiGet(request, token, '/positions/xgsPositions/queryById', { id });
      const readBody = await parseJsonBody(readRes);
      const duty = String((readBody.result as Record<string, unknown>)?.duty ?? '');
      expect(duty).not.toMatch(/<script|onerror\s*=/i);
      expect(duty).toContain('sec-probe');
    } finally {
      await apiPostJson(request, token, '/positions/xgsPositions/edit', { ...target, id, duty: originalDuty });
    }
  });
});
