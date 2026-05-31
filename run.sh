#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/Backend/tub-backend"
DATABASE_DIR="$BACKEND_DIR/database"
FRONTEND_DIR="$PROJECT_DIR/Frontend/index.html"

JAVA_HOME="/usr/lib/jvm/java-1.21.0-openjdk-amd64"
M2_HOME="$HOME/apache-maven-3.9.16"
export JAVA_HOME
export M2_HOME
export PATH="$JAVA_HOME/bin:$M2_HOME/bin:$PATH"

FRONTEND_PORT=5501
BACKEND_PID=""
FRONTEND_PID=""

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

log()   { echo -e "${CYAN}[TUB]${NC} $1"; }
ok()    { echo -e "${GREEN}[TUB ✔]${NC} $1"; }
warn()  { echo -e "${YELLOW}[TUB ⚠]${NC} $1"; }
error() { echo -e "${RED}[TUB ✖]${NC} $1"; }

# ── Cleanup ao sair (Ctrl+C ou erro) ────────────────────────
cleanup() {
    echo ""
    log "A parar todos os serviços..."

    if [ -n "$FRONTEND_PID" ] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
        kill "$FRONTEND_PID" 2>/dev/null
        ok "Frontend parado"
    fi

    if [ -n "$BACKEND_PID" ] && kill -0 "$BACKEND_PID" 2>/dev/null; then
        kill "$BACKEND_PID" 2>/dev/null
        ok "Backend parado"
    fi

    log "A parar MySQL Docker..."
    docker compose -f "$DATABASE_DIR/docker-compose.yml" down 2>/dev/null
    ok "MySQL Docker parado"

    echo ""
    ok "Todos os serviços foram encerrados. Até logo!"
    exit 0
}

trap cleanup SIGINT SIGTERM

# ── Verificações ─────────────────────────────────────────────
echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}║     🚌  TUB - Plataforma de Gestão Urbana       ║${NC}"
echo -e "${BOLD}╚══════════════════════════════════════════════════╝${NC}"
echo ""

log "A verificar dependências..."

if ! command -v docker &>/dev/null; then
    error "Docker não encontrado. Instale com: sudo apt install docker.io"
    exit 1
fi

if ! docker info &>/dev/null; then
    error "Sem permissão para usar Docker. Execute: sudo usermod -aG docker \$USER && newgrp docker"
    exit 1
fi

if [ ! -f "$JAVA_HOME/bin/javac" ]; then
    error "JDK 21 não encontrado em $JAVA_HOME. Instale com: sudo apt install -y openjdk-21-jdk-headless"
    exit 1
fi

if [ ! -f "$M2_HOME/bin/mvn" ]; then
    error "Maven não encontrado em $M2_HOME."
    error "Instale com: curl -fsSL https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.tar.gz | tar xz -C \$HOME"
    exit 1
fi

if ! command -v python3 &>/dev/null; then
    error "Python3 não encontrado. Instale com: sudo apt install python3"
    exit 1
fi

ok "Todas as dependências encontradas"

# ── 1. MySQL Docker ──────────────────────────────────────────
echo ""
log "${BOLD}[1/3]${NC} A iniciar MySQL Docker..."
docker compose -f "$DATABASE_DIR/docker-compose.yml" up -d

log "A aguardar MySQL ficar pronto..."
RETRIES=30
until docker exec tub_mysql mysqladmin ping -u tub_user -ptub_password --silent 2>/dev/null; do
    RETRIES=$((RETRIES - 1))
    if [ "$RETRIES" -le 0 ]; then
        error "MySQL não ficou pronto a tempo. Verifique: docker logs tub_mysql"
        exit 1
    fi
    sleep 2
done
ok "MySQL pronto na porta 3307"

# ── 2. Backend Spring Boot ───────────────────────────────────
echo ""
log "${BOLD}[2/3]${NC} A iniciar Backend Spring Boot..."
cd "$BACKEND_DIR"
mvn spring-boot:run -DskipTests -q &
BACKEND_PID=$!

log "A aguardar Backend ficar pronto na porta 8081..."
RETRIES=60
until curl -s http://localhost:8081 >/dev/null 2>&1; do
    # Verificar se o processo ainda está vivo
    if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
        error "Backend falhou ao iniciar. Verifique os logs acima."
        cleanup
        exit 1
    fi
    RETRIES=$((RETRIES - 1))
    if [ "$RETRIES" -le 0 ]; then
        error "Backend não ficou pronto a tempo."
        cleanup
        exit 1
    fi
    sleep 2
done
ok "Backend pronto em http://localhost:8081"

# ── 3. Frontend ──────────────────────────────────────────────
echo ""
log "${BOLD}[3/3]${NC} A iniciar Frontend..."
cd "$FRONTEND_DIR"
python3 -m http.server "$FRONTEND_PORT" --bind 127.0.0.1 &>/dev/null &
FRONTEND_PID=$!
sleep 1

if kill -0 "$FRONTEND_PID" 2>/dev/null; then
    ok "Frontend pronto em http://127.0.0.1:$FRONTEND_PORT"
else
    error "Frontend falhou ao iniciar na porta $FRONTEND_PORT"
    cleanup
    exit 1
fi

# ── Pronto! ──────────────────────────────────────────────────
echo ""
echo -e "${BOLD}╔══════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}║            ✅  Projeto TUB a correr!             ║${NC}"
echo -e "${BOLD}╠══════════════════════════════════════════════════╣${NC}"
echo -e "${BOLD}║${NC}  🐳 MySQL:    ${GREEN}localhost:3307${NC}                     ${BOLD}║${NC}"
echo -e "${BOLD}║${NC}  ☕ Backend:  ${GREEN}http://localhost:8081${NC}               ${BOLD}║${NC}"
echo -e "${BOLD}║${NC}  🌐 Frontend: ${GREEN}http://127.0.0.1:$FRONTEND_PORT${NC}              ${BOLD}║${NC}"
echo -e "${BOLD}╠══════════════════════════════════════════════════╣${NC}"
echo -e "${BOLD}║${NC}  Pressione ${RED}Ctrl+C${NC} para parar tudo              ${BOLD}║${NC}"
echo -e "${BOLD}╚══════════════════════════════════════════════════╝${NC}"
echo ""

wait "$BACKEND_PID"
