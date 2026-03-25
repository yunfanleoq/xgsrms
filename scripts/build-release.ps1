# Build backend JAR + frontend dist. Frontend gets VITE_GLOB_SIGNATURE_SECRET = same as JEECG_SIGNATURE_SECRET at runtime.
# Usage:
#   1) $env:JEECG_SIGNATURE_SECRET='hex'; powershell -ExecutionPolicy Bypass -File .\scripts\build-release.ps1
#   2) Create repo root local-signature.env (gitignored): JEECG_SIGNATURE_SECRET=hex
#   3) powershell -ExecutionPolicy Bypass -File .\scripts\build-release.ps1 -SignatureSecret 'hex'

param(
    [string] $SignatureSecret = $env:JEECG_SIGNATURE_SECRET
)

$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..')

if ([string]::IsNullOrWhiteSpace($SignatureSecret)) {
    $localFile = Join-Path $root 'local-signature.env'
    if (Test-Path $localFile) {
        foreach ($line in Get-Content $localFile) {
            $t = $line.Trim()
            if ($t -match '^\s*#' -or $t -eq '') { continue }
            if ($t -match '^\s*JEECG_SIGNATURE_SECRET\s*=\s*(.+)$') {
                $SignatureSecret = $matches[1].Trim()
                break
            }
        }
    }
}

if ([string]::IsNullOrWhiteSpace($SignatureSecret)) {
    Write-Error "Set JEECG_SIGNATURE_SECRET, or local-signature.env, or -SignatureSecret (must match server rms.env)."
}

$s = $SignatureSecret.Trim()
$env:VITE_GLOB_SIGNATURE_SECRET = $s
$env:JEECG_SIGNATURE_SECRET = $s

$jdk = 'C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot'
if (Test-Path $jdk) {
    $env:JAVA_HOME = $jdk
    $env:Path = "$jdk\bin;" + $env:Path
}

Write-Host "==> Maven package jeecg-system-start..."
Set-Location (Join-Path $root 'jeecg-boot')
mvn -pl jeecg-module-system/jeecg-system-start -am package -DskipTests

Write-Host "==> npm run build (VITE_GLOB_SIGNATURE_SECRET set)..."
Set-Location (Join-Path $root 'jeecgboot-vue3')
npm run build

Write-Host "Done. JAR: jeecg-boot\jeecg-module-system\jeecg-system-start\target\jeecg-system-start-*.jar"
Write-Host "      dist: jeecgboot-vue3\dist\"
