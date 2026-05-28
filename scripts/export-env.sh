#!/usr/bin/env bash
# 加载 cupr 根目录 .env，供本地启动 Java 微服务前使用:
#   source scripts/export-env.sh

set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ROOT}/.env"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "缺少 ${ENV_FILE}，请先执行: cp .env.example .env" >&2
  return 1 2>/dev/null || exit 1
fi

set -a
# shellcheck source=/dev/null
source "${ENV_FILE}"
set +a

echo "已加载环境变量: ${ENV_FILE}"
