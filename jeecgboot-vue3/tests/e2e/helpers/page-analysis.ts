import { expect, type Page } from '@playwright/test';

export interface PageAnalysis {
  is404: boolean;
  hasBasicTable: boolean;
  tableRowCount: number;
  pageTitle: string;
  url: string;
  bodySnippet: string;
  errors: string[];
}

const NOT_FOUND_PATTERNS = [/404/, /页面不存在/, /抱歉.*不存在/];

export async function analyzePage(page: Page): Promise<PageAnalysis> {
  const errors: string[] = [];
  const bodyText = (await page.locator('body').innerText({ timeout: 8_000 }).catch(() => '')).slice(0, 2000);
  const is404 = NOT_FOUND_PATTERNS.some((re) => re.test(bodyText));
  if (is404) errors.push('页面显示 404 / 不存在');

  const tableRows = page.locator('.ant-table-tbody tr.ant-table-row');
  const tableRowCount = await tableRows.count().catch(() => 0);
  const hasBasicTable = (await page.locator('.ant-table').count()) > 0;

  const pageTitle =
    (await page.locator('.jeecg-basic-table-form-container, .ant-tabs-tab-active, h1, .page-title')
      .first()
      .innerText()
      .catch(() => '')) || '';

  return {
    is404,
    hasBasicTable,
    tableRowCount,
    pageTitle: pageTitle.trim().slice(0, 120),
    url: page.url(),
    bodySnippet: bodyText.replace(/\s+/g, ' ').slice(0, 280),
    errors,
  };
}

export async function assertPageReady(page: Page, label: string): Promise<PageAnalysis> {
  const analysis = await analyzePage(page);
  if (analysis.is404) {
    throw new Error(`${label}：路由未注册或无权访问（404）。URL=${analysis.url}`);
  }
  return analysis;
}

export async function waitForSuccessToast(page: Page, timeout = 20_000): Promise<void> {
  const toast = page.locator(
    '.ant-message-notice, .ant-notification-notice, .ant-modal-confirm-body',
  ).filter({ hasText: /成功|已提交|操作成功|保存成功|提交成功|审核|邀请/ });
  await expect(toast.first()).toBeVisible({ timeout });
}

export async function tableHasText(page: Page, text: string): Promise<boolean> {
  return (await page.locator('.ant-table-tbody').getByText(text, { exact: false }).count()) > 0;
}

export async function clickTableActionForRow(page: Page, rowText: string, actionLabel: string) {
  const row = page.locator('.ant-table-tbody tr').filter({ hasText: rowText }).first();
  await expect(row).toBeVisible({ timeout: 30_000 });
  await row.getByRole('button', { name: actionLabel }).click({ timeout: 10_000 }).catch(async () => {
    await row.getByText(actionLabel, { exact: true }).click();
  });
}
