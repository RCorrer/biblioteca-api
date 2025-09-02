#!/usr/bin/env bash
if command -v mvn >/dev/null 2>&1; then
  mvn "$@"
else
  apt-get update && apt-get install -y maven
  mvn "$@"
fi
