#!/bin/bash

echo "=========================================="
echo "MICROSERVICES COMMUNICATION TEST"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if a service is running
check_container() {
    local container_name=$1
    if docker ps --format '{{.Names}}' | grep -q "^${container_name}$"; then
        echo -e "${GREEN}✓${NC} $container_name is running"
        return 0
    else
        echo -e "${RED}✗${NC} $container_name is NOT running"
        return 1
    fi
}

# Function to check Eureka registration
check_eureka_registration() {
    local service_name=$1
    if curl -s http://localhost:8761/eureka/apps | grep -q "<name>${service_name}</name>"; then
        echo -e "${GREEN}✓${NC} $service_name is registered with Eureka"
        return 0
    else
        echo -e "${RED}✗${NC} $service_name is NOT registered with Eureka"
        return 1
    fi
}

# Function to check service health
check_service_health() {
    local service_name=$1
    local port=$2
    
    if [ -n "$port" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${port}/actuator/health 2>/dev/null)
        if [ "$response" = "200" ]; then
            echo -e "${GREEN}✓${NC} $service_name health check passed (port $port)"
            return 0
        else
            echo -e "${RED}✗${NC} $service_name health check failed (port $port, status: $response)"
            return 1
        fi
    fi
}

echo "1. CHECKING INFRASTRUCTURE SERVICES"
echo "-----------------------------------"
check_container "skillverse-postgres"
check_container "skillverse-redis"
check_container "discovery-service"
echo ""

echo "2. CHECKING MICROSERVICES CONTAINERS"
echo "------------------------------------"
check_container "gateway-service"
check_container "auth-service"
check_container "profil-service"
check_container "booking-service"
check_container "chat-service"
check_container "reputation-service"
check_container "matching-service"
echo ""

echo "3. CHECKING EUREKA REGISTRATIONS"
echo "--------------------------------"
if curl -s http://localhost:8761 > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Eureka Server is accessible"
    echo ""
    echo "Registered services:"
    curl -s http://localhost:8761/eureka/apps | grep -oP '(?<=<name>)[^<]+' | sort -u | while read service; do
        echo "  - $service"
    done
    echo ""
else
    echo -e "${RED}✗${NC} Eureka Server is NOT accessible"
fi
echo ""

echo "4. CHECKING SERVICE-TO-SERVICE COMMUNICATION"
echo "--------------------------------------------"

# Check if gateway can reach other services through Eureka
echo "Testing Gateway routing..."
gateway_response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null)
if [ "$gateway_response" = "200" ]; then
    echo -e "${GREEN}✓${NC} Gateway is accessible"
else
    echo -e "${YELLOW}⚠${NC} Gateway health check returned: $gateway_response"
fi

echo ""
echo "5. CHECKING DOCKER NETWORK"
echo "-------------------------"
echo "Services on skillverse-network:"
docker network inspect skillverse-network --format '{{range .Containers}}{{.Name}} - {{.IPv4Address}}
{{end}}' 2>/dev/null || echo -e "${RED}✗${NC} Network not found"

echo ""
echo "6. CHECKING SERVICE LOGS FOR ERRORS"
echo "-----------------------------------"
for service in auth-service profil-service booking-service chat-service reputation-service matching-service; do
    if docker ps --format '{{.Names}}' | grep -q "^${service}$"; then
        errors=$(docker logs $service 2>&1 | grep -i "error\|exception\|failed" | tail -3)
        if [ -n "$errors" ]; then
            echo -e "${YELLOW}⚠${NC} $service has recent errors:"
            echo "$errors" | sed 's/^/    /'
        else
            echo -e "${GREEN}✓${NC} $service has no recent errors"
        fi
    fi
done

echo ""
echo "7. TESTING INTER-SERVICE COMMUNICATION"
echo "--------------------------------------"

# Test if services can communicate through the network
echo "Testing database connectivity..."
for service in auth-service profil-service booking-service reputation-service matching-service; do
    if docker ps --format '{{.Names}}' | grep -q "^${service}$"; then
        db_logs=$(docker logs $service 2>&1 | grep -i "database\|postgres" | tail -2)
        if echo "$db_logs" | grep -qi "connected\|successfully"; then
            echo -e "${GREEN}✓${NC} $service connected to database"
        elif echo "$db_logs" | grep -qi "error\|failed"; then
            echo -e "${RED}✗${NC} $service database connection issues"
        else
            echo -e "${YELLOW}⚠${NC} $service database status unclear"
        fi
    fi
done

echo ""
echo "=========================================="
echo "TEST COMPLETE"
echo "=========================================="
