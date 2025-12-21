#!/bin/bash

# Quick test script to get auth token and test protected endpoints

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "========================================="
echo "Testing Pagination with Authentication"
echo "========================================="
echo ""

# Try to login and get token (adjust credentials as needed)
echo "Attempting to login..."

# Example login request - adjust username/password as needed
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@fms.com",
    "password": "admin123"
  }')

# Extract token from response
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Could not get authentication token"
    echo "Response: $LOGIN_RESPONSE"
    echo ""
    echo "Please update the credentials in this script or run:"
    echo "  TOKEN=your_token ./test-pagination.sh"
    exit 1
fi

echo "✅ Successfully authenticated"
echo ""

# Test protected endpoints with token
echo "Testing protected endpoints..."
echo ""

test_with_auth() {
    local endpoint=$1
    local name=$2
    
    echo "Testing: $name"
    response=$(curl -s -w "\nHTTP_CODE:%{http_code}" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        "${BASE_URL}${endpoint}?page=0&size=5")
    
    http_code=$(echo "$response" | grep "HTTP_CODE:" | cut -d: -f2)
    
    if [ "$http_code" = "200" ]; then
        body=$(echo "$response" | sed '/HTTP_CODE:/d')
        total_elements=$(echo "$body" | grep -o '"totalElements":[0-9]*' | cut -d: -f2)
        echo "  ✅ Success - Total items: $total_elements"
    else
        echo "  ❌ Failed (HTTP $http_code)"
    fi
    echo ""
}

test_with_auth "/api/planes" "Planes"
test_with_auth "/api/flights" "Flights"
test_with_auth "/api/tickets" "Tickets"
test_with_auth "/api/flight-details" "Flight Details"
test_with_auth "/api/flight-ticket-classes" "Flight Ticket Classes"
test_with_auth "/api/employees" "Employees"
test_with_auth "/api/customers" "Customers"
test_with_auth "/api/accounts" "Accounts"

echo "========================================="
echo "Test Complete!"
echo "========================================="
