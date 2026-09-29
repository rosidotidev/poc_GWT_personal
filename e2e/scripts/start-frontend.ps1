$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..\..')

Set-Location $root
mvn -pl orders-backend,orders-frontend -am -DskipTests install
if ($LASTEXITCODE -ne 0) {
	exit $LASTEXITCODE
}

Set-Location (Join-Path $root 'orders-frontend')
mvn '-DskipTests' '-Dorders.frontend.port=18081' '-Dorders.soap.url=http://127.0.0.1:18080/soap/orders' jetty:run-war
