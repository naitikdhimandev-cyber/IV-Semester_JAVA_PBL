#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
OUT_DIR="out"
mkdir -p "$OUT_DIR"
CP="lib/sqlite-jdbc-3.51.2.0.jar:lib/opencv-480.jar"
javac -encoding UTF-8 -d "$OUT_DIR" -sourcepath src @sources.txt -cp "$CP"
echo "OK: classes in $OUT_DIR — run: ./run.sh"
