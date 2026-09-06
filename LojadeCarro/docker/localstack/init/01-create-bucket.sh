#!/bin/bash
set -e

BUCKET="${STORAGE_BUCKET:-loja-veiculos-local}"

if ! awslocal s3api head-bucket \
    --bucket "$BUCKET" >/dev/null 2>&1; then

    awslocal s3api create-bucket \
        --bucket "$BUCKET"
fi