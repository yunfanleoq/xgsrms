import { test, expect } from '@playwright/test';
import fs from 'fs';
import os from 'os';
import path from 'path';

// Configurable: set E2E_FORM_URL to the resume form route if baseURL is configured in playwright.config.
// Example: E2E_FORM_URL=/#/xgs/resume/base
const FORM_URL = process.env.E2E_FORM_URL || '/';

function ensureTempPng(): string {
  const pngBase64 =
    'iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsSAAALEgHS3X78AAAA' +
    'B3RJTUUH5AQaDyQqq1v8NwAAAB1pVFh0Q29tbWVudAAAAAAAQ3JlYXRlZCB3aXRoIEdJTVBkLmUH' +
    'AAAAIElEQVR42u3BMQEAAADCoPVPbQhPoAAAAAAAAAAAAPwB0I4oAAE6k8iTAAAAAElFTkSuQmCC';
  const tmp = path.join(os.tmpdir(), `photo_${Date.now()}.png`);
  fs.writeFileSync(tmp, Buffer.from(pngBase64, 'base64'));
  return tmp;
}

async function fillIfVisible(page, placeholder: string, value: string) {
  const input = page.getByPlaceholder(placeholder);
  if (await input.count()) {
    await input.fill(value);
  }
}

async function selectIfVisible(page, placeholder: string, optionText: string) {
  const selector = page.getByPlaceholder(placeholder);
  if (await selector.count()) {
    await selector.click();
    await page.getByRole('option', { name: optionText }).first().click();
  }
}

async function pickDateIfVisible(page, placeholder: string, isoDate: string) {
  const input = page.getByPlaceholder(placeholder);
  if (await input.count()) {
    await input.click();
    // 直接输入字符串，AntD 日期组件会解析
    await input.fill(isoDate);
    await page.keyboard.press('Enter');
  }
}

test('resume form - fill, upload photo and submit', async ({ page, baseURL }) => {
  // Navigate
  const target = FORM_URL.startsWith('http') ? FORM_URL : `${baseURL ?? ''}${FORM_URL}`;
  await page.goto(target, { waitUntil: 'load' });

  // Basic fields
  await fillIfVisible(page, '请输入姓名', '张三');
  await selectIfVisible(page, '请选择性别', '男');
  await pickDateIfVisible(page, '请选择出生年月', '1995-01-01');
  await fillIfVisible(page, '请输入民族', '汉族');
  await fillIfVisible(page, '请输入身份证号', '110101199501010011');
  await fillIfVisible(page, '请输入籍贯', '北京');
  await selectIfVisible(page, '请选择', '是'); // 对应“是否应届毕业生”等第一个下拉
  await fillIfVisible(page, '请输入毕业院校', '清华大学');
  await fillIfVisible(page, '请输入学历', '研究生');
  await fillIfVisible(page, '请输入学位', '硕士');
  await fillIfVisible(page, '请输入专业', '计算机科学与技术');
  await pickDateIfVisible(page, '请选择毕业时间', '2020-06-30');
  await pickDateIfVisible(page, '请选择参加工作时间', '2020-07-01');

  // Upload photo
  const photoPath = ensureTempPng();
  const fileInput = page.locator('.photo-cell input[type="file"]').first();
  await expect(fileInput).toHaveCount(1);
  await fileInput.setInputFiles(photoPath);

  // Wait for image preview (or upload list item) to appear
  const largeImage = page.locator('.photo-cell .large-image');
  const uploadedThumb = page.locator('.photo-cell .ant-upload-list-item');
  await Promise.race([
    largeImage.waitFor({ state: 'visible', timeout: 10000 }),
    uploadedThumb.waitFor({ state: 'visible', timeout: 10000 }),
  ]);

  // Submit - try common buttons
  const submitBtn = page.getByRole('button', { name: /提交|提\s*交|保存/i }).first();
  if (await submitBtn.count()) {
    await submitBtn.click();
  }

  // Expect a success message/toast/dialog
  const successText = page.locator(
    'text=/成功|保存成功|操作成功|提交成功|保存完成|保存并提交/'
  );
  await expect(successText).toBeVisible({ timeout: 15000 });
});





