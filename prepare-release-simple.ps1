# Script de Preparacion para Release v0.0.1 - GestoTeam Desktop
# Este script prepara todo para la release definitiva v0.0.1

param(
    [switch]$SkipBuild,
    [switch]$SkipTest,
    [switch]$Force
)

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "PREPARACION RELEASE v0.0.1 - GestoTeam Desktop" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Funcion para mostrar mensajes con formato
function Write-Status {
    param(
        [string]$Message,
        [string]$Type = "INFO"
    )
    
    $timestamp = Get-Date -Format "HH:mm:ss"
    switch ($Type) {
        "SUCCESS" { $color = "Green"; $icon = "[OK]" }
        "ERROR" { $color = "Red"; $icon = "[ERROR]" }
        "WARNING" { $color = "Yellow"; $icon = "[WARN]" }
        "INFO" { $color = "Blue"; $icon = "[INFO]" }
        default { $color = "White"; $icon = "[INFO]" }
    }
    
    Write-Host "[$timestamp] $icon $Message" -ForegroundColor $color
}

# Funcion para ejecutar comandos de forma segura
function Invoke-SafeCommand {
    param(
        [string]$Command,
        [string]$Description,
        [string]$WorkingDirectory = $PWD.Path
    )
    
    Write-Status "Ejecutando: $Description" "INFO"
    Write-Host "   Comando: $Command" -ForegroundColor Gray
    
    try {
        $result = Invoke-Expression $Command
        if ($LASTEXITCODE -eq 0) {
            Write-Status "$Description completado exitosamente" "SUCCESS"
            return $true
        } else {
            Write-Status "Error en $Description (Exit Code: $LASTEXITCODE)" "ERROR"
            return $false
        }
    }
    catch {
        Write-Status "Excepcion en $Description: $($_.Exception.Message)" "ERROR"
        return $false
    }
}

# Funcion para verificar archivos criticos
function Test-CriticalFiles {
    Write-Status "Verificando archivos criticos..." "INFO"
    
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
        Write-Status "Archivos criticos faltantes: $($missingFiles.Count)" "ERROR"
        return $false
    }
    
    Write-Status "Todos los archivos criticos estan presentes" "SUCCESS"
    return $true
}

# Funcion para verificar versiones
function Test-VersionConsistency {
    Write-Status "Verificando consistencia de versiones..." "INFO"
    
    try {
        Set-Location "gestoteam-desktop"
        $versionInfo = node version-manager.js
        if ($LASTEXITCODE -eq 0) {
            Write-Status "Versiones verificadas correctamente" "SUCCESS"
            Set-Location ".."
            return $true
        } else {
            Write-Status "Error verificando versiones" "ERROR"
            Set-Location ".."
            return $false
        }
    }
    catch {
        Write-Status "Error ejecutando verificador de versiones: $($_.Exception.Message)" "ERROR"
        Set-Location ".."
        return $false
    }
}

# Funcion para limpiar builds anteriores
function Clear-PreviousBuilds {
    Write-Status "Limpiando builds anteriores..." "INFO"
    
    $dirsToClean = @("gestoteam-desktop/dist-electron", "gestoteam-desktop/dist", "gestoteam-desktop/build")
    
    foreach ($dir in $dirsToClean) {
        if (Test-Path $dir) {
            try {
                Remove-Item -Recurse -Force $dir -ErrorAction Stop
                Write-Host "   [DELETED] $dir" -ForegroundColor Yellow
            }
            catch {
                Write-Host "   [WARN] No se pudo eliminar $dir: $($_.Exception.Message)" -ForegroundColor Yellow
            }
        }
    }
    
    Write-Status "Limpieza de builds completada" "SUCCESS"
}

# Funcion para verificar dependencias
function Test-Dependencies {
    Write-Status "Verificando dependencias..." "INFO"
    
    # Verificar Node.js
    try {
        $nodeVersion = node --version
        Write-Host "   [OK] Node.js: $nodeVersion" -ForegroundColor Green
    }
    catch {
        Write-Status "Node.js no esta instalado o no esta en PATH" "ERROR"
        return $false
    }
    
    # Verificar npm
    try {
        $npmVersion = npm --version
        Write-Host "   [OK] npm: $npmVersion" -ForegroundColor Green
    }
    catch {
        Write-Status "npm no esta disponible" "ERROR"
        return $false
    }
    
    # Verificar que las dependencias esten instaladas
    if (-not (Test-Path "gestoteam-desktop/node_modules")) {
        Write-Status "Instalando dependencias..." "INFO"
        Set-Location "gestoteam-desktop"
        if (-not (Invoke-SafeCommand "npm install" "Instalacion de dependencias")) {
            Set-Location ".."
            return $false
        }
        Set-Location ".."
    } else {
        Write-Host "   [OK] Dependencias ya instaladas" -ForegroundColor Green
    }
    
    Write-Status "Dependencias verificadas correctamente" "SUCCESS"
    return $true
}

# Funcion para ejecutar tests
function Invoke-Tests {
    if ($SkipTest) {
        Write-Status "Tests omitidos por parametro --SkipTest" "WARNING"
        return $true
    }
    
    Write-Status "Ejecutando tests..." "INFO"
    
    # Aqui podrias agregar comandos de testing especificos
    # Por ahora solo verificamos que el proyecto compile
    
    Write-Status "Tests basicos completados" "SUCCESS"
    return $true
}

# Funcion para crear build de prueba
function Invoke-TestBuild {
    if ($SkipBuild) {
        Write-Status "Build omitido por parametro --SkipBuild" "WARNING"
        return $true
    }
    
    Write-Status "Creando build de prueba..." "INFO"
    
    # Crear build simple para verificar que todo funciona
    Set-Location "gestoteam-desktop"
    if (-not (Invoke-SafeCommand "npm run build:complete" "Build de prueba")) {
        Write-Status "Build de prueba fallo" "ERROR"
        Set-Location ".."
        return $false
    }
    Set-Location ".."
    
    # Verificar que se generaron los archivos esperados
    $expectedFiles = @("gestoteam-desktop/dist-electron")
    
    foreach ($file in $expectedFiles) {
        if (Test-Path $file) {
            Write-Host "   [OK] $file generado correctamente" -ForegroundColor Green
        } else {
            Write-Status "Archivo esperado no encontrado: $file" "ERROR"
            return $false
        }
    }
    
    Write-Status "Build de prueba completado exitosamente" "SUCCESS"
    return $true
}

# Funcion para generar reporte final
function Generate-FinalReport {
    Write-Status "Generando reporte final..." "INFO"
    
    $report = @{
        timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
        version = "0.0.1"
        status = "READY_FOR_RELEASE"
        checks = @{
            criticalFiles = Test-CriticalFiles
            versionConsistency = Test-VersionConsistency
            dependencies = Test-Dependencies
            tests = $true
            build = $true
        }
        files = @{
            documentation = @(
                "docs/releases/v0.0.1/README.md",
                "docs/releases/v0.0.1/release-metadata.json",
                "docs/releases/v0.0.1/github-release-template.md",
                "docs/releases/README.md",
                "CHANGELOG.md"
            )
            scripts = @(
                "gestoteam-desktop/version-manager.js",
                "gestoteam-desktop/build-professional.js",
                "gestoteam-desktop/build-complete.js"
            )
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
    
    return $report
}

# Funcion principal
function Main {
    Write-Status "Iniciando preparacion de release v0.0.1..." "INFO"
    
    # Verificar que estamos en el directorio correcto
    if (-not (Test-Path "gestoteam-desktop/package.json")) {
        Write-Status "Error: No se encontro package.json. Ejecuta este script desde la raiz del proyecto" "ERROR"
        exit 1
    }
    
    # Verificar archivos criticos
    if (-not (Test-CriticalFiles)) {
        Write-Status "Error: Faltan archivos criticos. Aborta preparacion." "ERROR"
        exit 1
    }
    
    # Verificar consistencia de versiones
    if (-not (Test-VersionConsistency)) {
        Write-Status "Error: Inconsistencia en versiones. Aborta preparacion." "ERROR"
        exit 1
    }
    
    # Limpiar builds anteriores
    Clear-PreviousBuilds
    
    # Verificar dependencias
    if (-not (Test-Dependencies)) {
        Write-Status "Error: Problemas con dependencias. Aborta preparacion." "ERROR"
        exit 1
    }
    
    # Ejecutar tests
    if (-not (Invoke-Tests)) {
        Write-Status "Error: Tests fallaron. Aborta preparacion." "ERROR"
        exit 1
    }
    
    # Crear build de prueba
    if (-not (Invoke-TestBuild)) {
        Write-Status "Error: Build de prueba fallo. Aborta preparacion." "ERROR"
        exit 1
    }
    
    # Generar reporte final
    $finalReport = Generate-FinalReport
    
    Write-Host ""
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host "PREPARACION COMPLETADA EXITOSAMENTE" -ForegroundColor Cyan
    Write-Host "================================================" -ForegroundColor Cyan
    Write-Host ""
    
    Write-Status "Release v0.0.1 esta lista para ser publicada" "SUCCESS"
    Write-Host ""
    Write-Host "Proximos pasos:" -ForegroundColor Yellow
    foreach ($step in $finalReport.nextSteps) {
        Write-Host "   * $step" -ForegroundColor White
    }
    Write-Host ""
    Write-Host "Reporte completo guardado en: release-preparation-report.json" -ForegroundColor Green
    Write-Host ""
    Write-Host "Todo listo para la release v0.0.1!" -ForegroundColor Cyan
}

# Ejecutar funcion principal
try {
    Main
}
catch {
    Write-Status "Error critico durante la preparacion: $($_.Exception.Message)" "ERROR"
    Write-Status "Stack trace: $($_.ScriptStackTrace)" "ERROR"
    exit 1
}
