<#
.SYNOPSIS
Script de build para o projeto de seguros

.DESCRIPTION
Substitui o Makefile com comandos equivalentes em PowerShell
#>

param (
    [string]$command = "help",
    [switch]$help = $false
)

$projectName = "insurance-service"
$mavenCommand = ".\mvnw.cmd"
$dockerComposeCommand = "docker-compose"
$dockerEnvPath = "."

function Show-Help {
    Write-Host "Uso: .\build.ps1 [comando]"
    Write-Host ""
    Write-Host "Comandos disponíveis:"
    Write-Host "  build       - Compila o projeto"
    Write-Host "  run         - Inicia a aplicação"
    Write-Host "  test        - Executa os testes"
    Write-Host "  docker-build- Constrói a imagem Docker"
    Write-Host "  compose-up  - Inicia os containers"
    Write-Host "  compose-down- Para os containers"
    Write-Host "  logs        - Mostra os logs dos containers"
    Write-Host "  clean       - Limpa os arquivos de build"
    Write-Host "  restart     - Reinicia toda a stack"
    Write-Host "  check-env   - Verifica as dependências"
    Write-Host "  help        - Mostra esta ajuda"
}

function Invoke-Build {
    Write-Host "Construindo o projeto..."
    & $mavenCommand clean package
}

function Invoke-Run {
    Write-Host "Iniciando a aplicação..."
    & $mavenCommand spring-boot:run
}

function Invoke-Test {
    Write-Host "Executando testes..."
    & $mavenCommand test
}

function Invoke-DockerBuild {
    Write-Host "Construindo imagem Docker a partir de $dockerEnvPath..."
    Set-Location $dockerEnvPath
    try {
        docker build -t $projectName .
    } finally {
        Set-Location .
    }
}

function Invoke-ComposeUp {
    Write-Host "Iniciando containers a partir de $dockerEnvPath..."
    Set-Location $dockerEnvPath
    try {
        & $dockerComposeCommand up -d --build
    } finally {
        Set-Location .
    }
}

function Invoke-ComposeDown {
    Write-Host "Parando containers a partir de $dockerEnvPath..."
    Set-Location $dockerEnvPath
    try {
        & $dockerComposeCommand down
    } finally {
        Set-Location .
    }
}

function Invoke-Logs {
    Write-Host "Mostrando logs dos containers..."
    Set-Location $dockerEnvPath
    try {
        & $dockerComposeCommand logs -f
    } finally {
        Set-Location .
    }
}

function Invoke-Clean {
    Write-Host "Limpando arquivos de build..."
    & $mavenCommand clean
    if (Test-Path "target") {
        Remove-Item -Recurse -Force "target"
    }
}

function Invoke-Restart {
    Invoke-ComposeDown
    Invoke-Clean
    Invoke-ComposeUp
}

function Invoke-CheckEnv {
    Write-Host "Verificando ambiente..."
    
    $tools = @("mvn", "docker", "java")
    foreach ($tool in $tools) {
        if (Get-Command $tool -ErrorAction SilentlyContinue) {
            Write-Host "[OK] $tool encontrado"
        } else {
            Write-Host "[ERRO] $tool não encontrado no PATH"
        }
    }
    
    Write-Host "Verificação completa."
}

# Main execution
if ($help) {
    Show-Help
    exit
}

switch ($command.ToLower()) {
    "build"       { Invoke-Build }
    "run"         { Invoke-Run }
    "test"        { Invoke-Test }
    "docker-build"{ Invoke-DockerBuild }
    "compose-up"  { Invoke-ComposeUp }
    "compose-down"{ Invoke-ComposeDown }
    "logs"        { Invoke-Logs }
    "clean"       { Invoke-Clean }
    "restart"     { Invoke-Restart }
    "check-env"   { Invoke-CheckEnv }
    "help"        { Show-Help }
    default       {
        Write-Host "Comando desconhecido: $command"
        Show-Help
    }
}