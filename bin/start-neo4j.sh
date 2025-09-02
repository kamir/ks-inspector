#!/usr/bin/env bash
set -euo pipefail

echo "=== Starting Neo4j Instance (ks-inspector) ==="

# ---- Config ----
CONTAINER_NAME="neo4jlocal"
IMAGE="neo4j:5-community"         # explicit, avoids surprises with :latest
HTTP_PORT=7474
BOLT_PORT=7687

# Resolve script dir (works no matter where you run it from)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="${SCRIPT_DIR}"          # change if you want the graphdb dir elsewhere
NEO_DIR="${BASE_DIR}/graphdb/neo4j"

DATA_DIR="${NEO_DIR}/data"
LOGS_DIR="${NEO_DIR}/logs"
IMPORT_DIR="${NEO_DIR}/import"
PLUGINS_DIR="${NEO_DIR}/plugins"

# ---- Checks ----
if ! command -v docker >/dev/null 2>&1; then
  echo "❌ Docker is not installed or not in PATH"
  exit 1
fi

# Create host directories (consistent path)
mkdir -p "${DATA_DIR}" "${LOGS_DIR}" "${IMPORT_DIR}" "${PLUGINS_DIR}"

# Pull image if missing (optional but handy)
if ! docker image inspect "${IMAGE}" >/dev/null 2>&1; then
  echo "Pulling ${IMAGE}..."
  docker pull "${IMAGE}"
fi

# Container state handling
if docker ps --format '{{.Names}}' | grep -qx "${CONTAINER_NAME}"; then
  echo "✅ Neo4j is already running (${CONTAINER_NAME})"
  RUNNING=1
else
  if docker ps -a --format '{{.Names}}' | grep -qx "${CONTAINER_NAME}"; then
    echo "➡️  Found existing container (stopped). Starting it…"
    docker start "${CONTAINER_NAME}"
    RUNNING=1
  else
    echo "Starting new Neo4j container…"
    docker run -d \
      --name "${CONTAINER_NAME}" \
      --restart unless-stopped \
      -p ${HTTP_PORT}:7474 \
      -p ${BOLT_PORT}:7687 \
      -v "${DATA_DIR}:/data" \
      -v "${LOGS_DIR}:/logs" \
      -v "${IMPORT_DIR}:/var/lib/neo4j/import" \
      -v "${PLUGINS_DIR}:/plugins" \
      -e NEO4J_AUTH=neo4j/admin123# \
      -e NEO4J_server_default__listen__address=0.0.0.0 \
      -e NEO4J_server_http_advertised__address="localhost:${HTTP_PORT}" \
      -e NEO4J_server_bolt_advertised__address="localhost:${BOLT_PORT}" \
      "${IMAGE}"
    RUNNING=1
  fi
fi

# ---- Post-start info ----
echo "✅ Neo4j container is running."
echo "Browser:   http://localhost:${HTTP_PORT}"
echo "Bolt:      bolt://localhost:${BOLT_PORT}"
echo "User/Pass: neo4j / admin123#"
echo "Data dir:  ${DATA_DIR}"
echo "Logs dir:  ${LOGS_DIR}"
echo

# ---- Quick health wait & log hint ----
echo "Waiting for HTTP to respond (up to ~30s)…"
for i in {1..30}; do
  if curl -fsS "http://localhost:${HTTP_PORT}" >/dev/null 2>&1; then
    echo "🌐 HTTP is up."
    exit 0
  fi
  sleep 1
done

echo "⚠️  HTTP didn’t respond yet. Showing last 50 log lines:"
docker logs --tail=50 "${CONTAINER_NAME}" || true
echo
echo "Tips:"
echo "  • Check container logs continuously: docker logs -f ${CONTAINER_NAME}"
echo "  • Verify ports:                      lsof -i :${HTTP_PORT} -i :${BOLT_PORT}"
echo "  • If you mounted wrong dirs before, remove & recreate:"
echo "      docker rm -f ${CONTAINER_NAME}"
echo "      rm -rf \"${NEO_DIR}\" && mkdir -p \"${DATA_DIR}\" \"${LOGS_DIR}\" \"${IMPORT_DIR}\" \"${PLUGINS_DIR}\""
echo "      bash start-neo4j.sh"

