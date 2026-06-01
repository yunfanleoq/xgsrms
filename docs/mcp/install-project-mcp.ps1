# 从模板生成 .cursor/mcp.json（项目级 Obsidian + CodeGraph）
# Usage: powershell -ExecutionPolicy Bypass -File .\docs\mcp\install-project-mcp.ps1

$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$tplPath = Join-Path $PSScriptRoot 'cursor-mcp.template.json'
$tpl = Get-Content $tplPath -Raw -Encoding UTF8
$escaped = $root.Replace('\', '\\')
$json = $tpl -replace '\{\{REPO_ROOT\}\}', $escaped

$outDir = Join-Path $root '.cursor'
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$outFile = Join-Path $outDir 'mcp.json'
[System.IO.File]::WriteAllText($outFile, $json.Trim() + "`n", [System.Text.UTF8Encoding]::new($false))

Write-Host "OK: $outFile"
Write-Host "  obsidian-vault -> $root\docs\obsidian"
Write-Host "  codegraph      -> $root"
Write-Host 'Restart Cursor to load MCP.'
