#!/usr/bin/env bash
set -euo pipefail

echo "==> [1/5] Actualizando paquetes del sistema..."
sudo apt-get update -y
sudo apt-get install -y ca-certificates curl gnupg git ufw

echo "==> [2/5] Instalando Docker Engine y Docker Compose plugin..."
if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://get.docker.com | sudo sh
  sudo usermod -aG docker "$USER"
fi

echo "==> [3/5] Abriendo puertos en firewall de Ubuntu OCI (22, 80, 443, 8000)..."
# En imágenes Ubuntu de Oracle Cloud, iptables suele bloquear todo por defecto salvo el puerto 22.
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 8000 -j ACCEPT || true
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 80 -j ACCEPT || true
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 443 -j ACCEPT || true
if command -v netfilter-persistent >/dev/null 2>&1; then
  sudo netfilter-persistent save || true
fi

if sudo ufw status | grep -q "Status: active"; then
  sudo ufw allow 8000/tcp
  sudo ufw allow 80/tcp
  sudo ufw allow 443/tcp
fi

echo "==> [4/5] Configurando archivo .env..."
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$(dirname "$SCRIPT_DIR")"
cd "$BACKEND_DIR"

if [ ! -f .env ]; then
  SECRET="$(python3 -c 'import secrets; print(secrets.token_urlsafe(48))')"
  cp .env.example .env
  sed -i "s|reemplaza-esto-con-una-clave-secreta-larga-y-aleatoria|$SECRET|g" .env
  echo "    Archivo .env creado con SECRET_KEY aleatoria."
fi

echo "==> [5/5] Levantando TaskFlow API con Docker Compose..."
sudo docker compose -f deploy/docker-compose.yml up -d --build

echo ""
echo "Listo. Verifica con:"
echo "  curl http://127.0.0.1:8000/health"
echo "Y desde tu PC / navegador:"
echo "  http://<IP_PUBLICA_DE_ORACLE>:8000/docs"
