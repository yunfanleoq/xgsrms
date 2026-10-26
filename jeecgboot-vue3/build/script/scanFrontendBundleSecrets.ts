import fs from 'node:fs';
import path from 'node:path';

const DENY = ['e1d111f1dbcc3c35', 'dd05f1c54d63749eda95f9fa6d49v442a'];

function extraDeny(): string[] {
  const found: string[] = [];
  const candidates = [
    path.resolve('..', 'local-signature.env'),
    path.resolve('.env.production'),
    path.resolve('.env.production.local'),
  ];
  for (const file of candidates) {
    if (!fs.existsSync(file)) continue;
    const text = fs.readFileSync(file, 'utf8');
    for (const line of text.split('\n')) {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) continue;
      const eq = trimmed.indexOf('=');
      if (eq < 0) continue;
      const key = trimmed.slice(0, eq).trim();
      if (!/SIGNATURE|SECRET/i.test(key)) continue;
      const value = trimmed.slice(eq + 1).trim().replace(/^['"]|['"]$/g, '');
      if (value.length >= 16) found.push(value);
    }
  }
  return found;
}

function walk(dir: string, acc: string[] = []) {
  if (!fs.existsSync(dir)) return acc;
  for (const name of fs.readdirSync(dir)) {
    const full = path.join(dir, name);
    const st = fs.statSync(full);
    if (st.isDirectory()) walk(full, acc);
    else if (/\.(js|mjs|cjs|html|json)$/.test(name)) acc.push(full);
  }
  return acc;
}

export function scanFrontendBundleSecrets(distDir = path.resolve('dist')) {
  const needles = [...DENY, ...extraDeny()];
  const files = walk(distDir);
  const hits: string[] = [];
  for (const file of files) {
    const text = fs.readFileSync(file, 'utf8');
    for (const needle of needles) {
      if (text.includes(needle)) hits.push(`${path.relative(distDir, file)} 含签名密钥`);
    }
  }
  if (hits.length) {
    console.error('[security] 前端包密钥扫描未通过:');
    for (const hit of hits) console.error('  ' + hit);
    process.exit(1);
  }
  console.log(`[security] 前端包密钥扫描通过（${files.length} 个文件，未发现签名密钥）`);
}

if (process.argv[1] && process.argv[1].endsWith('scanFrontendBundleSecrets.ts')) {
  scanFrontendBundleSecrets(process.argv[2] ? path.resolve(process.argv[2]) : path.resolve('dist'));
}
