#!/bin/sh

# This is the Gradle wrapper script for UNIX-based systems
DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -f "$DIR/gradlew" ]; then
    exec "$DIR/gradlew" "$@"
else
    exec gradle "$@"
fi
