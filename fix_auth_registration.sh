#!/bin/bash
# Script to debug and fix Auth Service Registration

echo "1. Applying debug logging..."
# (User would need to edit docker-compose manually or I could use sed)

echo "2. Rebuilding Auth Service (this may take time)..."
docker compose build --no-cache auth-service

echo "3. Restarting Auth Service..."
docker compose up -d auth-service

echo "4. Following logs..."
docker logs -f auth-service
