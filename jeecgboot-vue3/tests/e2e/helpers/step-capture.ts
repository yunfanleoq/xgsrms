import fs from 'fs';
import path from 'path';
import type { Page } from '@playwright/test';
import { analyzePage, type PageAnalysis } from './page-analysis';

export interface StepRecord {
  index: number;
  id: string;
  role: string;
  roleLabel: string;
  title: string;
  action: string;
  pageDesc: string;
  route: string;
  url: string;
  screenshot: string;
  status: 'ok' | 'fail';
  error?: string;
  analysis?: PageAnalysis;
  timestamp: string;
}

export interface RunManifest {
  runId: string;
  baseURL: string;
  startedAt: string;
  finishedAt?: string;
  softwareName: string;
  softwareVersion: string;
  steps: StepRecord[];
}

function slug(s: string): string {
  return s
    .replace(/[^\w\u4e00-\u9fff-]+/g, '-')
    .replace(/-+/g, '-')
    .slice(0, 48);
}

export class StepCapture {
  private stepIndex = 0;
  readonly runDir: string;
  readonly manifestPath: string;
  private manifest: RunManifest;

  constructor(runId?: string) {
    const id =
      runId ||
      process.env.E2E_RUN_ID ||
      new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
    this.runDir = path.resolve(__dirname, '../../../../docs/e2e-runs', id);
    fs.mkdirSync(path.join(this.runDir, 'screenshots'), { recursive: true });
    this.manifestPath = path.join(this.runDir, 'manifest.json');
    this.manifest = {
      runId: id,
      baseURL: process.env.E2E_BASE_URL || 'http://10.28.12.15',
      startedAt: new Date().toISOString(),
      softwareName: process.env.E2E_SOFTWARE_NAME || '信工所招聘管理系统',
      softwareVersion: process.env.E2E_SOFTWARE_VERSION || 'V1.0',
      steps: [],
    };
    if (fs.existsSync(this.manifestPath)) {
      try {
        this.manifest = JSON.parse(fs.readFileSync(this.manifestPath, 'utf-8'));
        this.stepIndex = this.manifest.steps.length;
      } catch {
        /* fresh run */
      }
    }
    this.flush();
  }

  async capture(
    page: Page,
    opts: {
      role: string;
      roleLabel: string;
      title: string;
      action: string;
      pageDesc: string;
      route?: string;
    },
    options?: { strict?: boolean },
  ): Promise<void> {
    this.stepIndex += 1;
    const file = `${String(this.stepIndex).padStart(2, '0')}-${slug(opts.role)}-${slug(opts.title)}.png`;
    const rel = path.posix.join('screenshots', file);
    const full = path.join(this.runDir, rel);
    await page.waitForLoadState('domcontentloaded').catch(() => undefined);
    await page.waitForTimeout(800);
    const analysis = await analyzePage(page);
    await page.screenshot({ path: full, timeout: 45_000 });

    const step: StepRecord = {
      index: this.stepIndex,
      id: `S${String(this.stepIndex).padStart(2, '0')}`,
      role: opts.role,
      roleLabel: opts.roleLabel,
      title: opts.title,
      action: opts.action,
      pageDesc: opts.pageDesc,
      route: opts.route || '',
      url: page.url(),
      screenshot: rel.replace(/\\/g, '/'),
      status: analysis.is404 ? 'fail' : 'ok',
      error: analysis.is404 ? '页面 404 / 路由未注册' : undefined,
      analysis,
      timestamp: new Date().toISOString(),
    };
    this.manifest.steps.push(step);
    this.flush();
    const strict = options?.strict !== false;
    if (analysis.is404 && strict) {
      throw new Error(`截图步骤失败（404）：${opts.title} @ ${page.url()}`);
    }
  }

  markFail(page: Page | null, opts: { role: string; roleLabel: string; title: string; error: string }) {
    this.stepIndex += 1;
    const step: StepRecord = {
      index: this.stepIndex,
      id: `S${String(this.stepIndex).padStart(2, '0')}`,
      role: opts.role,
      roleLabel: opts.roleLabel,
      title: opts.title,
      action: '',
      pageDesc: '',
      route: '',
      url: page?.url() || '',
      screenshot: '',
      status: 'fail',
      error: opts.error,
      timestamp: new Date().toISOString(),
    };
    this.manifest.steps.push(step);
    this.flush();
  }

  finish() {
    this.manifest.finishedAt = new Date().toISOString();
    this.flush();
    return this.runDir;
  }

  flush() {
    fs.writeFileSync(this.manifestPath, JSON.stringify(this.manifest, null, 2), 'utf-8');
  }
}
