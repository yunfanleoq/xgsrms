import fs from 'fs';
import path from 'path';

export interface ChainState {
  runId: string;
  marker: string;
  positionName?: string;
  positionId?: string;
  applicantName?: string;
  steps: Array<{ phase: string; status: 'ok' | 'fail' | 'skip'; note?: string; at: string }>;
}

function statePath(runId: string): string {
  return path.resolve(__dirname, '../../../../docs/e2e-runs', runId, 'chain-state.json');
}

export function loadChainState(runId: string): ChainState {
  const p = statePath(runId);
  if (fs.existsSync(p)) {
    return JSON.parse(fs.readFileSync(p, 'utf-8'));
  }
  const marker = `E2E-${runId.replace(/[:.]/g, '').slice(-12)}`;
  return { runId, marker, steps: [] };
}

export function saveChainState(state: ChainState) {
  const p = statePath(state.runId);
  fs.mkdirSync(path.dirname(p), { recursive: true });
  fs.writeFileSync(p, JSON.stringify(state, null, 2), 'utf-8');
}

export function logStep(state: ChainState, phase: string, status: ChainState['steps'][0]['status'], note?: string) {
  state.steps.push({ phase, status, note, at: new Date().toISOString() });
  saveChainState(state);
}
