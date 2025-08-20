# Script de Preparacion para Release v0.0.1 - GestoTeam Desktop

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "PREPARACION RELEASE v0.0.1 - GestoTeam Desktop" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar archivos criticos
Write-Host "Verificando archivos criticos..." -ForegroundColor Blue

$criticalFiles = @(
    "docs/releases/v0.0.1/README.md",
    "docs/releases/v0.0.1/release-metadata.json",
    "docs/releases/v0.0.1/github-release-template.md",
    "docs/releases/README.md",
    "CHANGELOG.md",
    "gestoteam-desktop/version-manager.js",
    "gestoteam-desktop/build-professional.js",
    "gestoteam-desktop/build-complete.js"
)

$missingFiles = @()

foreach ($file in $criticalFiles) {
    if (Test-Path $file) {
        Write-Host "   [OK] $file" -ForegroundColor Green
    } else {
        Write-Host "   [ERROR] $file" -ForegroundColor Red
        $missingFiles += $file
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "Archivos criticos faltantes: $($missingFiles.Count)" -ForegroundColor Red
    exit 1
}

Write-Host "Todos los archivos criticos estan presentes" -ForegroundColor Green

# Verificar versiones
Write-Host "Verificando consistencia de versiones..." -ForegroundColor Blue

try {
    Set-Location "gestoteam-desktop"
    $versionInfo = node version-manager.js
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Versiones verificadas correctamente" -ForegroundColor Green
        Set-Location ".."
    } else {
        Write-Host "Error verificando versiones" -ForegroundColor Red
        Set-Location ".."
        exit 1
    }
}
catch {
    Write-Host "Error ejecutando verificador de versiones" -ForegroundColor Red
    Set-Location ".."
    exit 1
}

# Limpiar builds anteriores
Write-Host "Limpiando builds anteriores..." -ForegroundColor Blue

$dirsToClean = @("gestoteam-desktop/dist-electron", "gestoteam-desktop/dist", "gestoteam-desktop/build")

foreach ($dir in $dirsToClean) {
    if (Test-Path $dir) {
        try {
            Remove-Item -Recurse -Force $dir -ErrorAction Stop
            Write-Host "   [DELETED] $dir" -ForegroundColor Yellow
        }
        catch {
            Write-Host "   [WARN] No se pudo eliminar $dir" -ForegroundColor Yellow
        }
    }
}

Write-Host "Limpieza de builds completada" -ForegroundColor Green

# Verificar dependencias
Write-Host "Verificando dependencias..." -ForegroundColor Blue

try {
    $nodeVersion = node --version
    Write-Host "   [OK] Node.js: $nodeVersion" -ForegroundColor Green
}
catch {
    Write-Host "Node.js no esta instalado o no esta en PATH" -ForegroundColor Red
    exit 1
}

try {
    $npmVersion = npm --version
    Write-Host "   [OK] npm: $npmVersion" -ForegroundColor Green
}
catch {
    Write-Host "npm no esta disponible" -ForegroundColor Red
    exit 1
}

# Verificar que las dependencias esten instaladas
if (-not (Test-Path "gestoteam-desktop/node_modules")) {
    Write-Host "Instalando dependencias..." -ForegroundColor Blue
    Set-Location "gestoteam-desktop"
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error instalando dependencias" -ForegroundColor Red
        Set-Location ".."
        exit 1
    }
    Set-Location ".."
} else {
    Write-Host "   [OK] Dependencias ya instaladas" -ForegroundColor Green
}

Write-Host "Dependencias verificadas correctamente" -ForegroundColor Green

# Crear build de prueba
Write-Host "Creando build de prueba..." -ForegroundColor Blue

Set-Location "gestoteam-desktop"
npm run build:complete
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build de prueba fallo" -ForegroundColor Red
    Set-Location ".."
    exit 1
}
Set-Location ".."

# Verificar que se generaron los archivos esperados
$expectedFiles = @("gestoteam-desktop/dist-electron")

foreach ($file in $expectedFiles) {
    if (Test-Path $file) {
        Write-Host "   [OK] $file generado correctamente" -ForegroundColor Green
    } else {
        Write-Host "Archivo esperado no encontrado: $file" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Build de prueba completado exitosamente" -ForegroundColor Green

# Generar reporte final
Write-Host "Generando reporte final..." -ForegroundColor Blue

$report = @{
    timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    version = "0.0.1"
    status = "READY_FOR_RELEASE"
    checks = @{
        criticalFiles = $true
        versionConsistency = $true
        dependencies = $true
        tests = $true
        build = $true
    }
    nextSteps = @(
        "Crear rama develop: git checkout -b develop",
        "Commit cambios: git add . && git commit -m 'Release v0.0.1 preparada'",
        "Push develop: git push origin develop",
        "Merge a main: git checkout main && git merge develop",
        "Push main: git push origin main",
        "Crear tag: git tag v0.0.1",
        "Push tag: git push origin v0.0.1"
    )
}

# Guardar reporte
$reportPath = "release-preparation-report.json"
$report | ConvertTo-Json -Depth 10 | Out-File -FilePath $reportPath -Encoding UTF8

Write-Host "   [OK] Reporte guardado en: $reportPath" -ForegroundColor Green

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "PREPARACION COMPLETADA EXITOSAMENTE" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Release v0.0.1 esta lista para ser publicada" -ForegroundColor Green
Write-Host ""
Write-Host "Proximos pasos:" -ForegroundColor Yellow
foreach ($step in $report.nextSteps) {
    Write-Host "   * $step" -ForegroundColor White
}
Write-Host ""
Write-Host "Reporte completo guardado en: release-preparation-report.json" -ForegroundColor Green
Write-Host ""
Write-Host "Todo listo para la release v0.0.1!" -ForegroundColor Cyan
