$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..\..')
Set-Location (Join-Path $root 'orders-backend')
mvn '-Dorders.http.port=18080' '-Dorders.db.url=jdbc:h2:file:./target/e2e/orders' exec:java
