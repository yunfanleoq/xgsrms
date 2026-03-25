<#
.SYNOPSIS
  Replace local jeecg-boot with official JeecgBoot v3.9.1 (Spring Boot 3 / JDK 17) and attach jeecg-module-recruitment.

.PARAMETER OfficialJeecgBootRoot
  Root of cloned JeecgBoot repo (must contain jeecg-boot folder).
  If empty: use sibling folder ..\JeecgBoot (e.g. I:\cursorProjects\JeecgBoot) when jeecg-boot\pom.xml exists; else .upgrade-cache\JeecgBoot-3.9.1

.PARAMETER Tag
  Git tag for shallow clone. Default: v3.9.1

.EXAMPLE
  powershell -ExecutionPolicy Bypass -File .\scripts\upgrade-to-jeecg391.ps1
.EXAMPLE
  powershell -ExecutionPolicy Bypass -File .\scripts\upgrade-to-jeecg391.ps1 -OfficialJeecgBootRoot "I:\cursorProjects\JeecgBoot"
#>

param(
    [string] $OfficialJeecgBootRoot = "",
    [string] $Tag = "v3.9.1"
)

$ErrorActionPreference = "Stop"
$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$CacheRoot = Join-Path $RepoRoot ".upgrade-cache"
$DefaultOfficial = Join-Path $CacheRoot "JeecgBoot-3.9.1"
# Same parent as xgsrms: e.g. I:\cursorProjects\xgsrms -> I:\cursorProjects\JeecgBoot
$RepoParent = Split-Path -Parent $RepoRoot.Path
$SiblingJeecgBoot = Join-Path $RepoParent "JeecgBoot"

function Ensure-OfficialSource {
    param([string]$Root)
    $jb = Join-Path $Root "jeecg-boot\pom.xml"
    if (-not (Test-Path -LiteralPath $jb)) {
        Write-Host "[INFO] Official jeecg-boot not found, shallow clone $Tag ..." -ForegroundColor Cyan
        if (-not (Test-Path -LiteralPath $CacheRoot)) { New-Item -ItemType Directory -Path $CacheRoot | Out-Null }
        $cloneDest = Join-Path $CacheRoot "JeecgBoot-3.9.1"
        if (Test-Path -LiteralPath $cloneDest) {
            Write-Host "[WARN] Already exists: $cloneDest (delete to re-clone)" -ForegroundColor Yellow
        }
        else {
            Push-Location $CacheRoot
            try {
                git clone --depth 1 --branch $Tag "https://github.com/jeecgboot/JeecgBoot.git" "JeecgBoot-3.9.1"
            }
            finally { Pop-Location }
        }
        $Root = $cloneDest
    }
    return $Root
}

if ([string]::IsNullOrWhiteSpace($OfficialJeecgBootRoot)) {
    if (Test-Path -LiteralPath (Join-Path $SiblingJeecgBoot "jeecg-boot\pom.xml")) {
        $OfficialJeecgBootRoot = $SiblingJeecgBoot
        Write-Host "[INFO] Using local JeecgBoot source: $OfficialJeecgBootRoot" -ForegroundColor Green
    }
    else {
        $OfficialJeecgBootRoot = $DefaultOfficial
    }
}
$OfficialJeecgBootRoot = Ensure-OfficialSource -Root $OfficialJeecgBootRoot

$SrcBoot = Join-Path $OfficialJeecgBootRoot "jeecg-boot"
if (-not (Test-Path -LiteralPath (Join-Path $SrcBoot "pom.xml"))) {
    throw "Official jeecg-boot not found: $SrcBoot (clone JeecgBoot $Tag or use -OfficialJeecgBootRoot)"
}

$DstBoot = Join-Path $RepoRoot "jeecg-boot"
$RecruitmentSrc = Join-Path $DstBoot "jeecg-module-recruitment"
$BackupDir = Join-Path $RepoRoot "backup"
$Ts = Get-Date -Format "yyyyMMdd-HHmmss"
$RecBackup = Join-Path $BackupDir "jeecg-module-recruitment-$Ts"
$YmlBackup = Join-Path $BackupDir "system-start-resources-$Ts"

Write-Host ""
Write-Host "=== JeecgBoot 3.9.1 monolith upgrade ===" -ForegroundColor Cyan
Write-Host "Repo: $RepoRoot"
Write-Host "Source jeecg-boot: $SrcBoot"
Write-Host ""

if (-not (Test-Path -LiteralPath $BackupDir)) { New-Item -ItemType Directory -Path $BackupDir | Out-Null }

if (Test-Path -LiteralPath $RecruitmentSrc) {
    Write-Host "[1/6] Backup jeecg-module-recruitment -> $RecBackup"
    Copy-Item -Recurse -Force -LiteralPath $RecruitmentSrc -Destination $RecBackup
}
else {
    throw "Missing: $RecruitmentSrc"
}

$startRes = Join-Path $DstBoot "jeecg-module-system\jeecg-system-start\src\main\resources"
if (Test-Path -LiteralPath $startRes) {
    Write-Host "[1/6] Backup system-start resources -> $YmlBackup"
    New-Item -ItemType Directory -Force -Path $YmlBackup | Out-Null
    Copy-Item -Recurse -Force -LiteralPath $startRes -Destination $YmlBackup
}

$OldBootBackup = Join-Path $RepoRoot "jeecg-boot.backup-$Ts"
Write-Host "[2/6] Move jeecg-boot -> $OldBootBackup"
if (Test-Path -LiteralPath $DstBoot) {
    Move-Item -LiteralPath $DstBoot -Destination $OldBootBackup
}

Write-Host "[3/6] Copy official jeecg-boot ..."
Copy-Item -Recurse -Force -LiteralPath $SrcBoot -Destination $DstBoot

Write-Host "[4/6] Restore jeecg-module-recruitment ..."
if (-not (Test-Path -LiteralPath $RecBackup)) { throw "Backup missing: $RecBackup" }
$RecDest = Join-Path $DstBoot "jeecg-module-recruitment"
Copy-Item -Recurse -Force -LiteralPath $RecBackup -Destination $RecDest

# Parent pom: add module after jeecg-boot-module (avoid < in double quotes)
$ParentPom = Join-Path $DstBoot "pom.xml"
$pp = Get-Content -Raw -Encoding UTF8 -LiteralPath $ParentPom
if ($pp -notmatch "jeecg-module-recruitment") {
    $needle = "<module>jeecg-boot-module</module>"
    if ($pp.Contains($needle)) {
        $nl = [Environment]::NewLine
        $modLine = '        <module>jeecg-module-recruitment</module>'
        $replacement = $needle + $nl + $modLine
        $pp = $pp.Replace($needle, $replacement)
        Set-Content -LiteralPath $ParentPom -Value $pp -Encoding utf8 -NoNewline
        Write-Host "      Parent pom: added module jeecg-module-recruitment"
    }
    else {
        Write-Host "[WARN] Pattern not found in parent pom; add module manually." -ForegroundColor Yellow
    }
}

$StartPom = Join-Path $DstBoot "jeecg-module-system\jeecg-system-start\pom.xml"
$sp = Get-Content -Raw -Encoding UTF8 -LiteralPath $StartPom
$sp = $sp -replace '<artifactId>jeecg-module-demo</artifactId>', '<artifactId>jeecg-module-recruitment</artifactId>'
Set-Content -LiteralPath $StartPom -Value $sp -Encoding utf8 -NoNewline
Write-Host "      system-start: dependency set to jeecg-module-recruitment"

# Single-quoted here-string: no parsing of < or $
$RecPom = @'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>jeecg-boot-parent</artifactId>
        <groupId>org.jeecgframework.boot3</groupId>
        <version>3.9.1</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>jeecg-module-recruitment</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.jeecgframework.boot3</groupId>
            <artifactId>jeecg-boot-base-core</artifactId>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-cache</artifactId>
            <version>${hutool.version}</version>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-http</artifactId>
            <version>${hutool.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.10.2</version>
        </dependency>
        <dependency>
            <groupId>org.jeecgframework.boot3</groupId>
            <artifactId>jeecg-system-local-api</artifactId>
        </dependency>
    </dependencies>

</project>
'@

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
$recPomPath = Join-Path $RecDest "pom.xml"
[System.IO.File]::WriteAllText($recPomPath, $RecPom, $utf8NoBom)
Write-Host "[5/6] Wrote jeecg-module-recruitment/pom.xml (boot3)"

Write-Host "[6/6] javax -> jakarta in recruitment ..."
$srcRoot = Join-Path $RecDest "src"
$javaFiles = Get-ChildItem -Path $srcRoot -Filter "*.java" -Recurse -ErrorAction SilentlyContinue
foreach ($f in $javaFiles) {
    $c = Get-Content -Raw -Encoding UTF8 -LiteralPath $f.FullName
    if ($c.Length -gt 0 -and [int][char]$c[0] -eq 0xFEFF) { $c = $c.Substring(1) }
    $n = $c `
        -replace 'javax\.servlet\.', 'jakarta.servlet.' `
        -replace 'javax\.annotation\.', 'jakarta.annotation.' `
        -replace 'javax\.websocket\.', 'jakarta.websocket.'
    if ($n -ne $c) {
        [System.IO.File]::WriteAllText($f.FullName, $n, $utf8NoBom)
    }
}

Write-Host ""
Write-Host "Done. Next steps:" -ForegroundColor Green
Write-Host "  1) Merge application-dev.yml / application-prod.yml from backup into:"
Write-Host "     jeecg-module-system/jeecg-system-start/src/main/resources/"
Write-Host "     Backup: $YmlBackup"
Write-Host "  2) Merge Shiro anon paths and other customizations (see docs/UPGRADE-JeecgBoot-3.9.md)"
Write-Host "  3) cd jeecg-boot ; mvn -pl jeecg-module-system/jeecg-system-start -am package -DskipTests"
Write-Host ""
Write-Host "Old backend kept at: $OldBootBackup"
Write-Host ""
