#!/bin/bash

podman run -t -i \
-e EDGE_PORT=4566 \
-p 127.0.0.1:4566:4566 \
localstack/localstack:latest
