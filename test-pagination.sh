#!/bin/bash

# Pagination Endpoints Testing Script
# Tests all the paginated getAll endpoints in the Flight Management System

# Configuration
BASE_URL="${BASE_URL:-http://localhost:8080}"
PAGE="${PAGE:-0}"
SIZE="${SIZE:-5}"
SORT="${SORT:-}"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print section header
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

# Function to test endpoint
test_endpoint() {
    local endpoint=$1
    local name=$2
    local auth_header=$3
    
    echo -e "${YELLOW}Testing: ${NC}${name}"
    echo -e "${YELLOW}Endpoint: ${NC}${BASE_URL}${endpoint}"
    
    if [ -n "$auth_header" ]; then
        echo -e "${YELLOW}Auth: ${NC}Required"
        response=$(curl -s -w "\nHTTP_CODE:%{http_code}" \
            -H "Authorization: Bearer ${TOKEN}" \
            -H "Content-Type: application/json" \
            "${BASE_URL}${endpoint}?page=${PAGE}&size=${SIZE}${SORT}")
    else
        echo -e "${YELLOW}Auth: ${NC}Not Required"
        response=$(curl -s -w "\nHTTP_CODE:%{http_code}" \
            -H "Content-Type: application/json" \
            "${BASE_URL}${endpoint}?page=${PAGE}&size=${SIZE}${SORT}")
    fi
    
    http_code=$(echo "$response" | grep "HTTP_CODE:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_CODE:/d')
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✓ Success (HTTP $http_code)${NC}"
        
        # Extract pagination info using basic text processing
        total_elements=$(echo "$body" | grep -o '"totalElements":[0-9]*' | cut -d: -f2)
        total_pages=$(echo "$body" | grep -o '"totalPages":[0-9]*' | cut -d: -f2)
        number=$(echo "$body" | grep -o '"number":[0-9]*' | cut -d: -f2)
        size=$(echo "$body" | grep -o '"size":[0-9]*' | cut -d: -f2)
        
        if [ -n "$total_elements" ]; then
            echo -e "  Total Elements: ${total_elements}"
            echo -e "  Total Pages: ${total_pages}"
            echo -e "  Current Page: ${number}"
            echo -e "  Page Size: ${size}"
        else
            echo -e "  Response: ${body:0:200}..."
        fi
    else
        echo -e "${RED}✗ Failed (HTTP $http_code)${NC}"
        echo -e "  Error: ${body:0:200}"
    fi
    
    echo ""
}

# Main script
print_header "Flight Management System - Pagination Test"

echo "Configuration:"
echo "  Base URL: $BASE_URL"
echo "  Page: $PAGE"
echo "  Size: $SIZE"
if [ -n "$SORT" ]; then
    echo "  Sort: $SORT"
fi
echo ""

# Check if server is running
echo -e "${YELLOW}Checking server status...${NC}"
if curl -s --connect-timeout 5 "${BASE_URL}/actuator/health" > /dev/null 2>&1 || \
   curl -s --connect-timeout 5 "${BASE_URL}/api/airports?page=0&size=1" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Server is running${NC}\n"
else
    echo -e "${RED}✗ Server is not reachable at ${BASE_URL}${NC}"
    echo -e "${YELLOW}Please start the backend server first${NC}\n"
    exit 1
fi

# Test all pagination endpoints

print_header "Testing Public Endpoints"

test_endpoint "/api/airports" "Airports" ""
test_endpoint "/api/planes" "Planes" ""
test_endpoint "/api/flights" "Flights" ""
test_endpoint "/api/tickets" "Tickets" ""
test_endpoint "/api/ticket-classes" "Ticket Classes" ""
test_endpoint "/api/flight-details" "Flight Details" ""
test_endpoint "/api/passengers" "Passengers" ""
test_endpoint "/api/flight-ticket-classes" "Flight Ticket Classes" ""

print_header "Testing Protected Endpoints (May require authentication)"

echo -e "${YELLOW}Note: These endpoints may require authentication${NC}"
echo -e "${YELLOW}Set TOKEN environment variable with a valid JWT token${NC}\n"

if [ -n "$TOKEN" ]; then
    test_endpoint "/api/employees" "Employees" "Bearer"
    test_endpoint "/api/customers" "Customers" "Bearer"
    test_endpoint "/api/accounts" "Accounts" "Bearer"
else
    echo -e "${YELLOW}Attempting without authentication...${NC}\n"
    test_endpoint "/api/employees" "Employees" ""
    test_endpoint "/api/customers" "Customers" ""
    test_endpoint "/api/accounts" "Accounts" ""
fi

print_header "Testing Different Pagination Parameters"

echo -e "${YELLOW}Testing with different page sizes...${NC}\n"
test_endpoint "/api/airports?page=0&size=3" "Airports (size=3)" ""
test_endpoint "/api/airports?page=0&size=10" "Airports (size=10)" ""

echo -e "${YELLOW}Testing different pages...${NC}\n"
test_endpoint "/api/airports?page=0&size=5" "Airports (page=0)" ""
test_endpoint "/api/airports?page=1&size=5" "Airports (page=1)" ""

echo -e "${YELLOW}Testing with sorting...${NC}\n"
test_endpoint "/api/airports?page=0&size=5&sort=airportName,asc" "Airports (sorted by name asc)" ""
test_endpoint "/api/airports?page=0&size=5&sort=airportId,desc" "Airports (sorted by id desc)" ""

print_header "Test Summary"

echo -e "${GREEN}All pagination endpoints have been tested!${NC}"
echo ""
echo "Usage examples:"
echo "  Basic test:                ./test-pagination.sh"
echo "  Custom base URL:           BASE_URL=http://localhost:8080 ./test-pagination.sh"
echo "  Custom page size:          SIZE=10 ./test-pagination.sh"
echo "  Custom page number:        PAGE=1 ./test-pagination.sh"
echo "  With authentication:       TOKEN=your_jwt_token ./test-pagination.sh"
echo "  Combined:                  BASE_URL=http://localhost:8080 PAGE=0 SIZE=10 ./test-pagination.sh"
echo ""
echo "To test with sorting:"
echo "  SORT='&sort=fieldName,asc' ./test-pagination.sh"
echo ""
