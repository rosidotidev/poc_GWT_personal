param(
    [switch]$KeepReports
)

$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..\..')

$processIds = Get-NetTCPConnection -State Listen -LocalPort 18080,18081 -ErrorAction SilentlyContinue |
    Select-Object -ExpandProperty OwningProcess -Unique
foreach ($processId in $processIds) {
    Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
}

$paths = @((Join-Path $root 'orders-backend\target\e2e'))
if (-not $KeepReports) {
    $paths += (Join-Path $root 'test-results')
    $paths += (Join-Path $root 'playwright-report')
}
foreach ($path in $paths) {
    if (Test-Path $path) {
        Remove-Item $path -Recurse -Force
    }
}
