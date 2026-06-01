param (
    [string]$BaseUrl = "http://localhost:8081",
    [string]$Email = "admin@tub.pt",
    [string]$Password = "1234"
)

Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "   Simulador de Geofencing TUB (PowerShell)      " -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "A iniciar autenticação..."

# 1. Autenticação na API
$loginBody = @{
    email = $Email
    password = $Password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
} catch {
    Write-Host "Erro de autenticação! Certifica-te que o backend está a correr em $BaseUrl." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit
}

if (-not $token) {
    Write-Host "Falha na autenticação (token não recebido)!" -ForegroundColor Red
    exit
}
Write-Host "✓ Autenticado com sucesso! Token gerado." -ForegroundColor Green

# Cabeçalho para requests autorizados
$headers = @{
    "Authorization" = "Bearer $token"
}

# Dados para simulação
$zonas = @("Zona A - Centro Histórico", "Zona B - Campus Universitário", "Zona C - Estação CP", "Zona D - Hospital de Braga", "Zona E - Estádio Municipal")
$movimentos = @("ENTRADA", "SAIDA")
$viaturas = @(101, 102, 103, 104, 105, 106)

Write-Host "A iniciar a injeção contínua de eventos (Prima Ctrl+C para parar)..." -ForegroundColor Yellow

# Ciclo infinito de simulação
while ($true) {
    $viaturaId = $viaturas | Get-Random
    $zona = $zonas | Get-Random
    $movimento = $movimentos | Get-Random

    # Encode de caracteres especiais para o URL (espaços, hífens, etc)
    $zonaEncoded = [uri]::EscapeDataString($zona)
    
    $uri = "$BaseUrl/api/geofencing/registar?viaturaId=$viaturaId&nomeZona=$zonaEncoded&tipoMovimento=$movimento"

    try {
        $response = Invoke-RestMethod -Uri $uri -Method Post -Headers $headers
        
        # Log visual com cores
        if ($movimento -eq "ENTRADA") {
            Write-Host "[$([datetime]::Now.ToString('HH:mm:ss'))] ⬇ ENTRADA: Viatura #$viaturaId em $zona" -ForegroundColor Green
        } else {
            Write-Host "[$([datetime]::Now.ToString('HH:mm:ss'))] ⬆ SAÍDA: Viatura #$viaturaId de $zona" -ForegroundColor Magenta
        }
    } catch {
        Write-Host "Erro ao enviar evento: $_" -ForegroundColor Red
    }

    # Espera entre 5 e 12 segundos para enviar o próximo, simulando tráfego real
    $sleepTime = Get-Random -Minimum 5 -Maximum 13
    Start-Sleep -Seconds $sleepTime
}
