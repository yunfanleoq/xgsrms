import type { APIRequestContext, APIResponse } from '@playwright/test';
import { buildApiUrl } from './api-auth';

export async function apiUploadFile(
  request: APIRequestContext,
  token: string,
  fileName: string,
  content: string | Buffer,
  mimeType = 'application/octet-stream',
): Promise<APIResponse> {
  return request.post(buildApiUrl('/sys/common/upload'), {
    headers: { 'X-Access-Token': token },
    multipart: {
      file: {
        name: fileName,
        mimeType,
        buffer: Buffer.isBuffer(content) ? content : Buffer.from(content),
      },
    },
    failOnStatusCode: false,
  });
}

export async function apiUploadOssFile(
  request: APIRequestContext,
  token: string,
  fileName: string,
  content: string | Buffer,
): Promise<APIResponse> {
  return request.post(buildApiUrl('/sys/oss/file/upload'), {
    headers: { 'X-Access-Token': token },
    multipart: {
      file: {
        name: fileName,
        mimeType: 'application/octet-stream',
        buffer: Buffer.isBuffer(content) ? content : Buffer.from(content),
      },
    },
    failOnStatusCode: false,
  });
}

/** 上传被拒绝：success=false 或含非法类型/无权限等提示 */
export function isUploadRejected(body: Record<string, unknown>): boolean {
  if (body.success === false) return true;
  const msg = String(body.message ?? body.msg ?? body._raw ?? '');
  return /非法|不允许|无权限|上传失败|拒绝|denied|permission/i.test(msg);
}
