#!/usr/bin/env bash
set -e

# Overload Development Server Runner
# Automatically handles database checking/starting, environment variables, and booting Spring Boot.

echo "=========================================="
echo "         OVERLOAD DEV LAUNCHER            "
echo "=========================================="

# 1. Load environment variables from .env
if [ -f .env ]; then
    echo "✔ Loading .env variables..."
    export $(grep -v '^#' .env | xargs)
else
    echo "✖ .env file not found! Please create one from .env.example or template."
    exit 1
fi

# 2. Ensure Docker and overload-db are running
echo "✔ Checking Docker database container (overload-db)..."
if ! docker info >/dev/null 2>&1; then
    echo "===================================================================="
    echo "✖ Docker Desktop is currently not running!"
    echo "Please open 'Docker Desktop' from your Applications/Spotlight folder."
    echo "Once Docker Desktop is open and running, run ./dev.sh again."
    echo "===================================================================="
    exit 1
fi

echo "✔ Starting overload-db container with your existing data..."
/usr/local/bin/docker-compose up db -d

# 3. Locate Maven
MVN_CMD="mvn"
if [ -f "./mvnw" ]; then
    MVN_CMD="./mvnw"
elif [ -x "/opt/homebrew/bin/mvn" ]; then
    MVN_CMD="/opt/homebrew/bin/mvn"
fi

# 4. Start Spring Boot application
echo "=========================================="
echo "✔ Launching Spring Boot server on http://localhost:8080 ..."
echo "=========================================="

exec "$MVN_CMD" spring-boot:run
