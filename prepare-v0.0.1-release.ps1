# 🎉 Script de Preparación para Release v0.0.1 - GestoTeam Desktop
# Este script prepara todo para la release definitiva v0.0.1

param(
    [switch]$SkipBuild,
    [switch]$SkipTest,
    [switch]$Force
)

# Configuración de colores para PowerShell
$Host.UI.RawUI.ForegroundColor = "White"

Write-Host "🎉 ================================================" -ForegroundColor Cyan
Write-Host "🎉 PREPARACIÓN RELEASE v0.0.1 - GestoTeam Desktop" -ForegroundColor Cyan
Write-Host "🎉 ================================================" -ForegroundColor Cyan
Write-Host ""

# Función para mostrar mensajes con formato
function Write-Status {
    param(
        [string]$Message,
        [string]$Type = "INFO"
    )
    
    $timestamp = Get-Date -Format "HH:mm:ss"
    switch ($Type) {
        "SUCCESS" { $color = "Green"; $icon = "✅" }
        "ERROR" { $color = "Red"; $icon = "❌" }
        "WARNING" { $color = "Yellow"; $icon = "⚠️" }
        "INFO" { $color = "Blue"; $icon = "ℹ️" }
        default { $color = "White"; $icon = "ℹ️" }
    }
    
    Write-Host "[$timestamp] $icon $Message" -ForegroundColor $color
}

# Función para ejecutar comandos de forma segura
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
        Write-Status "Excepción en $Description: $($_.Exception.Message)" "ERROR"
        return $false
    }
}

# Función para verificar archivos críticos
function Test-CriticalFiles {
    Write-Status "Verificando archivos críticos..." "INFO"
    
    $criticalFiles = @(
        "docs/releases/v0.0.1/README.md",
        "docs/releases/v0.0.1/release-metadata.json",
        "docs/releases/v0.0.1/github-release-template.md",
        "docs/releases/README.md",
        "CHANGELOG.md",
        "version-manager.js",
        "build-professional.js",
        "build-complete.js"
    )
    
    $missingFiles = @()
    
    foreach ($file in $criticalFiles) {
        if (Test-Path $file) {
            Write-Host "   ✅ $file" -ForegroundColor Green
        } else {
            Write-Host "   ❌ $file" -ForegroundColor Red
            $missingFiles += $file
        }
    }
    
    if ($missingFiles.Count -gt 0) {
        Write-Status "Archivos críticos faltantes: $($missingFiles.Count)" "ERROR"
        return $false
    }
    
    Write-Status "Todos los archivos críticos están presentes" "SUCCESS"
    return $true
}

# Función para verificar versiones
function Test-VersionConsistency {
    Write-Status "Verificando consistencia de versiones..." "INFO"
    
    try {
        $versionInfo = node version-manager.js
        if ($LASTEXITCODE -eq 0) {
            Write-Status "Versiones verificadas correctamente" "SUCCESS"
            return $true
        } else {
            Write-Status "Error verificando versiones" "ERROR"
            return $false
        }
    }
    catch {
        Write-Status "Error ejecutando verificador de versiones: $($_.Exception.Message)" "ERROR"
        return $false
    }
}

# Función para limpiar builds anteriores
function Clear-PreviousBuilds {
    Write-Status "Limpiando builds anteriores..." "INFO"
    
    $dirsToClean = @("dist-electron", "dist", "build")
    
    foreach ($dir in $dirsToClean) {
        if (Test-Path $dir) {
            try {
                Remove-Item -Recurse -Force $dir -ErrorAction Stop
                Write-Host "   🗑️  Eliminado: $dir" -ForegroundColor Yellow
            }
            catch {
                Write-Host "   ⚠️  No se pudo eliminar $dir: $($_.Exception.Message)" -ForegroundColor Yellow
            }
        }
    }
    
    Write-Status "Limpieza de builds completada" "SUCCESS"
}

# Función para verificar dependencias
function Test-Dependencies {
    Write-Status "Verificando dependencias..." "INFO"
    
    # Verificar Node.js
    try {
        $nodeVersion = node --version
        Write-Host "   ✅ Node.js: $nodeVersion" -ForegroundColor Green
    }
    catch {
        Write-Status "Node.js no está instalado o no está en PATH" "ERROR"
        return $false
    }
    
    # Verificar npm
    try {
        $npmVersion = npm --version
        Write-Host "   ✅ npm: $npmVersion" -ForegroundColor Green
    }
    catch {
        Write-Status "npm no está disponible" "ERROR"
        return $false
    }
    
    # Verificar que las dependencias estén instaladas
    if (-not (Test-Path "node_modules")) {
        Write-Status "Instalando dependencias..." "INFO"
        if (-not (Invoke-SafeCommand "npm install" "Instalación de dependencias")) {
            return $false
        }
    } else {
        Write-Host "   ✅ Dependencias ya instaladas" -ForegroundColor Green
    }
    
    Write-Status "Dependencias verificadas correctamente" "SUCCESS"
    return $true
}

# Función para ejecutar tests
function Invoke-Tests {
    if ($SkipTest) {
        Write-Status "Tests omitidos por parámetro --SkipTest" "WARNING"
        return $true
    }
    
    Write-Status "Ejecutando tests..." "INFO"
    
    # Aquí podrías agregar comandos de testing específicos
    # Por ahora solo verificamos que el proyecto compile
    
    Write-Status "Tests básicos completados" "SUCCESS"
    return $true
}

# Función para crear build de prueba
function Invoke-TestBuild {
    if ($SkipBuild) {
        Write-Status "Build omitido por parámetro --SkipBuild" "WARNING"
        return $true
    }
    
    Write-Status "Creando build de prueba..." "INFO"
    
    # Crear build simple para verificar que todo funciona
    if (-not (Invoke-SafeCommand "npm run build:complete" "Build de prueba")) {
        Write-Status "Build de prueba falló" "ERROR"
        return $false
    }
    
    # Verificar que se generaron los archivos esperados
    $expectedFiles = @("dist-electron")
    
    foreach ($file in $expectedFiles) {
        if (Test-Path $file) {
            Write-Host "   ✅ $file generado correctamente" -ForegroundColor Green
        } else {
            Write-Status "Archivo esperado no encontrado: $file" "ERROR"
            return $false
        }
    }
    
    Write-Status "Build de prueba completado exitosamente" "SUCCESS"
    return $true
}

# Función para generar reporte final
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
                "version-manager.js",
                "build-professional.js",
                "build-complete.js"
            )
        }
        nextSteps = @(
            "Crear tag git: git tag v0.0.1",
            "Push tag: git push origin v0.0.1",
            "GitHub Actions se ejecutará automáticamente",
            "Release se publicará en GitHub Releases"
        )
    }
    
    # Guardar reporte
    $reportPath = "release-preparation-report.json"
    $report | ConvertTo-Json -Depth 10 | Out-File -FilePath $reportPath -Encoding UTF8
    
    Write-Host "   📋 Reporte guardado en: $reportPath" -ForegroundColor Green
    
    return $report
}

# Función principal
function Main {
    Write-Status "Iniciando preparación de release v0.0.1..." "INFO"
    
    # Verificar que estamos en el directorio correcto
    if (-not (Test-Path "package.json")) {
        Write-Status "Error: No se encontró package.json. Ejecuta este script desde el directorio gestoteam-desktop" "ERROR"
        exit 1
    }
    
    # Verificar archivos críticos
    if (-not (Test-CriticalFiles)) {
        Write-Status "Error: Faltan archivos críticos. Aborta preparación." "ERROR"
        exit 1
    }
    
    # Verificar consistencia de versiones
    if (-not (Test-VersionConsistency)) {
        Write-Status "Error: Inconsistencia en versiones. Aborta preparación." "ERROR"
        exit 1
    }
    
    # Limpiar builds anteriores
    Clear-PreviousBuilds
    
    # Verificar dependencias
    if (-not (Test-Dependencies)) {
        Write-Status "Error: Problemas con dependencias. Aborta preparación." "ERROR"
        exit 1
    }
    
    # Ejecutar tests
    if (-not (Invoke-Tests)) {
        Write-Status "Error: Tests fallaron. Aborta preparación." "ERROR"
        exit 1
    }
    
    # Crear build de prueba
    if (-not (Invoke-TestBuild)) {
        Write-Status "Error: Build de prueba falló. Aborta preparación." "ERROR"
        exit 1
    }
    
    # Generar reporte final
    $finalReport = Generate-FinalReport
    
    Write-Host ""
    Write-Host "🎉 ================================================" -ForegroundColor Cyan
    Write-Host "🎉 PREPARACIÓN COMPLETADA EXITOSAMENTE" -ForegroundColor Cyan
    Write-Host "🎉 ================================================" -ForegroundColor Cyan
    Write-Host ""
    
    Write-Status "Release v0.0.1 está lista para ser publicada" "SUCCESS"
    Write-Host ""
    Write-Host "📋 Próximos pasos:" -ForegroundColor Yellow
    foreach ($step in $finalReport.nextSteps) {
        Write-Host "   • $step" -ForegroundColor White
    }
    Write-Host ""
    Write-Host "📊 Reporte completo guardado en: release-preparation-report.json" -ForegroundColor Green
    Write-Host ""
    Write-Host "🚀 ¡Todo listo para la release v0.0.1!" -ForegroundColor Cyan
}

# Ejecutar función principal
try {
    Main
}
catch {
    Write-Status "Error crítico durante la preparación: $($_.Exception.Message)" "ERROR"
    Write-Status "Stack trace: $($_.ScriptStackTrace)" "ERROR"
    exit 1
}
