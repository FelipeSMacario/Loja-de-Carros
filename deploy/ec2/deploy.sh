#!/usr/bin/env bash

set -Eeuo pipefail

IMAGE_TAG="${1:?Informe a tag das imagens.}"
APP_DIR="/opt/loja-de-carros"
ENV_FILE="${APP_DIR}/.env"
SOURCE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_DIR="${APP_DIR}/backups/$(date -u +%Y%m%dT%H%M%SZ)"

if [[ ! "${IMAGE_TAG}" =~ ^sha-[0-9a-f]{40}-run-[1-9][0-9]*$ ]]; then
    echo "Tag de imagem inválida: ${IMAGE_TAG}" >&2
    exit 1
fi

if [[ ! -f "${ENV_FILE}" ]]; then
    echo "Arquivo ${ENV_FILE} não encontrado." >&2
    exit 1
fi

mkdir -p \
    "${APP_DIR}/caddy" \
    "${BACKUP_DIR}/caddy"

cp -a "${ENV_FILE}" "${BACKUP_DIR}/.env"
cp -a "${APP_DIR}/compose.yaml" "${BACKUP_DIR}/compose.yaml"
cp -a "${APP_DIR}/compose.override.yaml" "${BACKUP_DIR}/compose.override.yaml"
cp -a "${APP_DIR}/caddy/Caddyfile" "${BACKUP_DIR}/caddy/Caddyfile"

rollback() {
    local status=$?

    trap - EXIT

    if (( status != 0 )); then
        echo "Deploy falhou. Restaurando configuração anterior."

        cp -a "${BACKUP_DIR}/.env" "${ENV_FILE}"
        cp -a "${BACKUP_DIR}/compose.yaml" "${APP_DIR}/compose.yaml"
        cp -a "${BACKUP_DIR}/compose.override.yaml" "${APP_DIR}/compose.override.yaml"
        cp -a "${BACKUP_DIR}/caddy/Caddyfile" "${APP_DIR}/caddy/Caddyfile"

        cd "${APP_DIR}"

        docker compose up \
            -d \
            --remove-orphans \
            --wait \
            --wait-timeout 240 || true
    fi

    exit "${status}"
}

trap rollback EXIT

install -m 0644 \
    "${SOURCE_DIR}/compose.yaml" \
    "${APP_DIR}/compose.yaml"

install -m 0644 \
    "${SOURCE_DIR}/compose.override.yaml" \
    "${APP_DIR}/compose.override.yaml"

install -m 0644 \
    "${SOURCE_DIR}/caddy/Caddyfile" \
    "${APP_DIR}/caddy/Caddyfile"

sed -i \
    -e "s|^API_IMAGE_TAG=.*$|API_IMAGE_TAG=${IMAGE_TAG}|" \
    -e "s|^WEB_IMAGE_TAG=.*$|WEB_IMAGE_TAG=${IMAGE_TAG}|" \
    "${ENV_FILE}"

ECR_REGISTRY="$(
    sed -n 's/^ECR_REGISTRY=//p' "${ENV_FILE}" |
    tail -n 1
)"

AWS_REGION="$(
    sed -n 's/^AWS_REGION=//p' "${ENV_FILE}" |
    tail -n 1
)"

PUBLIC_ORIGIN="$(
    sed -n 's/^PUBLIC_ORIGIN=//p' "${ENV_FILE}" |
    tail -n 1
)"

aws ecr get-login-password \
    --region "${AWS_REGION}" |
docker login \
    --username AWS \
    --password-stdin "${ECR_REGISTRY}"

cd "${APP_DIR}"

docker compose config --quiet

docker compose pull api web

docker compose up \
    -d \
    --remove-orphans \
    --wait \
    --wait-timeout 240

curl \
    --fail \
    --silent \
    --show-error \
    --retry 12 \
    --retry-delay 5 \
    --retry-all-errors \
    "${PUBLIC_ORIGIN}/health"

curl \
    --fail \
    --silent \
    --show-error \
    --retry 12 \
    --retry-delay 5 \
    --retry-all-errors \
    "${PUBLIC_ORIGIN}/auth/realms/loja-de-carros/.well-known/openid-configuration" \
    >/dev/null

echo
echo "Deploy concluído com a tag ${IMAGE_TAG}."

trap - EXIT