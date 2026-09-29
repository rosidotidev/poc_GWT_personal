param(
    [switch]$Headed
)

$ErrorActionPreference = 'Stop'
$cleanup = Join-Path $PSScriptRoot 'cleanup.ps1'

& $cleanup
$exitCode = 1
try {
    if ($Headed) {
        & npx playwright test --headed
    } else {
        & npx playwright test
    }
    $exitCode = $LASTEXITCODE
}
finally {
    & $cleanup -KeepReports
}
exit $exitCode
