# Test: anonymous GET checkOnlyUser must not return success=true/result=true without valid captcha.
param([string] $BaseUrl = "http://127.0.0.1:8080/xgszp-boot")

$ErrorActionPreference = "Stop"
$uri = "$BaseUrl/sys/user/checkOnlyUser?username=admin"
Write-Host "GET $uri"

try {
    $r = Invoke-RestMethod -Uri $uri -Method Get -TimeoutSec 10
    $json = $r | ConvertTo-Json -Depth 6 -Compress
    Write-Host "Response: $json"

    $success = $null -ne $r.success -and $r.success
    $resultOk = $null -ne $r.result -and $r.result

    if ($success -and $resultOk) {
        Write-Host 'FAIL: enumeration risk (success=true, result=true without captcha).'
        exit 1
    }
    Write-Host 'PASS: did not return username-available without captcha.'
    exit 0
} catch {
    Write-Host "INFO: backend not reachable or non-JSON error. BaseUrl=$BaseUrl"
    Write-Host $_.Exception.Message
    exit 2
}
