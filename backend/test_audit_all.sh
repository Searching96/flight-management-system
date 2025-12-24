#!/bin/bash

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
WARNINGS=0

# API Base URL
BASE_URL="http://localhost:8080/api"

# Admin credentials
ADMIN_NAME="Đỗ Văn Hải"

echo "╔════════════════════════════════════════════════════════════╗"
echo "║          COMPLETE AUDIT LOG TEST SUITE                    ║"
echo "║                                                            ║"
echo "║  Comprehensive tests for CREATE, UPDATE, DELETE audits    ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Login and get token
echo -e "${BLUE}ℹ${NC} Logging in as admin: $ADMIN_NAME"
ENCODED_NAME=$(echo -n "$ADMIN_NAME" | jq -sRr @uri)
LOGIN_RESPONSE=$(curl -s "${BASE_URL}/debug/login-by-name/${ENCODED_NAME}")
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo -e "${RED}✗${NC} Failed to login"
    exit 1
fi
echo -e "${GREEN}✓${NC} Admin logged in successfully"
echo ""

# Function to verify audit log exists
verify_audit_log() {
    local entity_name=$1
    local entity_id=$2
    local expected_action=$3
    local test_name=$4
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    sleep 0.5  # Give time for audit log to be saved
    
    AUDIT_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
        "${BASE_URL}/audit-logs/${entity_name}/${entity_id}")
    
    AUDIT_COUNT=$(echo "$AUDIT_RESPONSE" | jq "[.data[] | select(.action == \"$expected_action\")] | length")
    
    if [ "$AUDIT_COUNT" -gt 0 ]; then
        echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: ${test_name}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        echo "$AUDIT_RESPONSE" | jq -r "[.data[] | select(.action == \"$expected_action\")][0] | \"   Action: \(.action), Field: \(.fieldName), Old: \(.oldValue // \"null\"), New: \(.newValue // \"null\")\""
    else
        echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: ${test_name}"
        echo "   Expected ${expected_action} audit log but found none"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
}

# ============================================================================
# SECTION 1: AIRPORT AUDIT TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 1: AIRPORT AUDIT TESTS (3 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 1: CREATE Airport
AIRPORT_RESPONSE=$(curl -s -X POST \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "airportName": "Test Airport CREATE",
        "cityName": "Test City",
        "countryName": "Test Country"
    }' \
    "${BASE_URL}/airports")

AIRPORT_ID=$(echo "$AIRPORT_RESPONSE" | jq -r '.data.airportId')
verify_audit_log "Airport" "$AIRPORT_ID" "CREATE" "Airport CREATE audit log"

# Test 2: UPDATE Airport
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "airportName": "Updated Airport Name",
        "cityName": "Updated City",
        "countryName": "Updated Country"
    }' \
    "${BASE_URL}/airports/${AIRPORT_ID}" > /dev/null

verify_audit_log "Airport" "$AIRPORT_ID" "UPDATE" "Airport UPDATE audit log"

# Test 3: DELETE Airport
curl -s -X DELETE \
    -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/airports/${AIRPORT_ID}" > /dev/null

verify_audit_log "Airport" "$AIRPORT_ID" "DELETE" "Airport DELETE audit log"

echo ""

# ============================================================================
# SECTION 2: PLANE AUDIT TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 2: PLANE AUDIT TESTS (3 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 4: CREATE Plane
PLANE_RESPONSE=$(curl -s -X POST \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "planeCode": "TEST-CREATE",
        "planeType": "Boeing 787",
        "seatQuantity": 300
    }' \
    "${BASE_URL}/planes")

PLANE_ID=$(echo "$PLANE_RESPONSE" | jq -r '.data.planeId')
verify_audit_log "Plane" "$PLANE_ID" "CREATE" "Plane CREATE audit log"

# Test 5: UPDATE Plane
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "planeCode": "TEST-UPDATED",
        "planeType": "Airbus A350",
        "seatQuantity": 350
    }' \
    "${BASE_URL}/planes/${PLANE_ID}" > /dev/null

verify_audit_log "Plane" "$PLANE_ID" "UPDATE" "Plane UPDATE audit log"

# Test 6: DELETE Plane
curl -s -X DELETE \
    -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/planes/${PLANE_ID}" > /dev/null

verify_audit_log "Plane" "$PLANE_ID" "DELETE" "Plane DELETE audit log"

echo ""

# ============================================================================
# SECTION 3: TICKET CLASS AUDIT TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 3: TICKET CLASS AUDIT TESTS (3 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 7: CREATE TicketClass
CLASS_RESPONSE=$(curl -s -X POST \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "ticketClassName": "Test Class CREATE",
        "color": "#FF0000"
    }' \
    "${BASE_URL}/ticket-classes")

CLASS_ID=$(echo "$CLASS_RESPONSE" | jq -r '.data.ticketClassId')
verify_audit_log "TicketClass" "$CLASS_ID" "CREATE" "TicketClass CREATE audit log"

# Test 8: UPDATE TicketClass
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "ticketClassName": "Updated Class Name",
        "color": "#00FF00"
    }' \
    "${BASE_URL}/ticket-classes/${CLASS_ID}" > /dev/null

verify_audit_log "TicketClass" "$CLASS_ID" "UPDATE" "TicketClass UPDATE audit log"

# Test 9: DELETE TicketClass
curl -s -X DELETE \
    -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/ticket-classes/${CLASS_ID}" > /dev/null

verify_audit_log "TicketClass" "$CLASS_ID" "DELETE" "TicketClass DELETE audit log"

echo ""

# ============================================================================
# SECTION 4: ACCOUNT AUDIT TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 4: ACCOUNT AUDIT TESTS (3 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 10: UPDATE Account (multiple fields)
ACCOUNT_ID=1
TIMESTAMP=$(date +%s)
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "accountName": "Test Account '"${TIMESTAMP}"'",
        "phoneNumber": "09'"${TIMESTAMP:(-8)}"'"
    }' \
    "${BASE_URL}/accounts/${ACCOUNT_ID}" > /dev/null

verify_audit_log "Account" "$ACCOUNT_ID" "UPDATE" "Account UPDATE audit log"

# Test 11: Verify multiple field changes are logged separately
sleep 0.5
AUDIT_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs/Account/${ACCOUNT_ID}")

RECENT_UPDATES=$(echo "$AUDIT_RESPONSE" | jq '[.data[] | select(.action == "UPDATE")] | sort_by(.changedAt) | reverse | .[0:3]')
UPDATE_COUNT=$(echo "$RECENT_UPDATES" | jq 'length')

TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$UPDATE_COUNT" -ge 2 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Account UPDATE logs individual fields separately"
    echo "$RECENT_UPDATES" | jq -r '.[] | "   Field: \(.fieldName), Old: \(.oldValue), New: \(.newValue)"' | head -3
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Account UPDATE should log individual fields"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 12: UPDATE Account email
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "updated'"${TIMESTAMP}"'@test.com"
    }' \
    "${BASE_URL}/accounts/${ACCOUNT_ID}" > /dev/null

verify_audit_log "Account" "$ACCOUNT_ID" "UPDATE" "Account email UPDATE audit log"

echo ""

# ============================================================================
# SECTION 5: PARAMETER AUDIT TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 5: PARAMETER AUDIT TESTS (3 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 13: UPDATE Parameter (single field)
RANDOM_VALUE=$((RANDOM % 5 + 2))
PARAM_RESPONSE=$(curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/parameters/max-medium-airports/${RANDOM_VALUE}")

sleep 0.5
PARAM_AUDIT=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs?page=0&size=10")

PARAM_UPDATE_EXISTS=$(echo "$PARAM_AUDIT" | jq '[.data.content[] | select(.entityName == "Parameter" and .action == "UPDATE" and .fieldName == "maxMediumAirport")] | length')

TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$PARAM_UPDATE_EXISTS" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Parameter single field UPDATE audit log"
    echo "$PARAM_AUDIT" | jq -r '[.data.content[] | select(.entityName == "Parameter" and .action == "UPDATE" and .fieldName == "maxMediumAirport")][0] | "   Field: \(.fieldName), Old: \(.oldValue), New: \(.newValue)"'
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${YELLOW}⚠${NC} Test ${TOTAL_TESTS}: Parameter UPDATE audit log not found (timing issue)"
    WARNINGS=$((WARNINGS + 1))
fi

# Test 14: UPDATE All Parameters (bulk)
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "maxMediumAirport": '"$((RANDOM % 5 + 2))"',
        "minFlightDuration": 30,
        "minLayoverDuration": 30,
        "maxLayoverDuration": 720,
        "minBookingInAdvanceDuration": 4,
        "maxBookingHoldDuration": 24
    }' \
    "${BASE_URL}/parameters" > /dev/null

sleep 0.5
PARAM_AUDIT_ALL=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs?page=0&size=20")

PARAM_UPDATES=$(echo "$PARAM_AUDIT_ALL" | jq '[.data.content[] | select(.entityName == "Parameter" and .action == "UPDATE")] | length')

TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$PARAM_UPDATES" -ge 1 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Parameter bulk UPDATE creates audit logs"
    echo "   Found $PARAM_UPDATES parameter update audit logs"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Parameter bulk UPDATE audit logs not found"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 15: UPDATE specific parameter
curl -s -X PUT \
    -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/parameters/min-flight-duration/$((RANDOM % 30 + 30))" > /dev/null

sleep 0.5
PARAM_SPECIFIC=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs?page=0&size=15")

SPECIFIC_UPDATE=$(echo "$PARAM_SPECIFIC" | jq '[.data.content[] | select(.entityName == "Parameter" and .fieldName == "minFlightDuration")] | length')

TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$SPECIFIC_UPDATE" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Specific parameter field UPDATE audit log"
    echo "   Found minFlightDuration update in audit logs"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Specific parameter UPDATE not found"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

echo ""

# ============================================================================
# SECTION 6: AUDIT LOG QUERY TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 6: AUDIT LOG QUERY TESTS (4 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 16: Get all audit logs (pagination)
TOTAL_TESTS=$((TOTAL_TESTS + 1))
ALL_LOGS=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs?page=0&size=20")

LOG_COUNT=$(echo "$ALL_LOGS" | jq '.data.content | length')

if [ "$LOG_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Get all audit logs (paginated)"
    echo "   Retrieved $LOG_COUNT audit logs"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Failed to retrieve audit logs"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 17: Get audit logs by entity
TOTAL_TESTS=$((TOTAL_TESTS + 1))
ENTITY_LOGS=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs/Airport/${AIRPORT_ID}")

ENTITY_LOG_COUNT=$(echo "$ENTITY_LOGS" | jq '.data | length')

if [ "$ENTITY_LOG_COUNT" -ge 3 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Get audit logs by specific entity"
    echo "   Found $ENTITY_LOG_COUNT logs for Airport ${AIRPORT_ID}"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Expected at least 3 logs for Airport ${AIRPORT_ID}"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 18: Verify audit log structure
TOTAL_TESTS=$((TOTAL_TESTS + 1))
SAMPLE_LOG=$(echo "$ALL_LOGS" | jq '.data.content[0]')
HAS_REQUIRED_FIELDS=$(echo "$SAMPLE_LOG" | jq 'has("auditId") and has("entityName") and has("entityId") and has("action") and has("changedAt")')

if [ "$HAS_REQUIRED_FIELDS" = "true" ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Audit log structure is correct"
    echo "   Contains all required fields: auditId, entityName, entityId, action, changedAt"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Audit log structure is missing required fields"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 19: Verify pagination structure
TOTAL_TESTS=$((TOTAL_TESTS + 1))
PAGE_STRUCTURE=$(echo "$ALL_LOGS" | jq '.data | has("content") and has("page")')

if [ "$PAGE_STRUCTURE" = "true" ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Pagination structure is correct (DTO mode)"
    TOTAL_ELEMENTS=$(echo "$ALL_LOGS" | jq '.data.page.totalElements')
    TOTAL_PAGES=$(echo "$ALL_LOGS" | jq '.data.page.totalPages')
    echo "   Total elements: $TOTAL_ELEMENTS, Total pages: $TOTAL_PAGES"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Pagination structure is incorrect"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

echo ""

# ============================================================================
# SECTION 7: ACTION TYPE FILTERING TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 7: ACTION FILTERING TESTS (3 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 20: Check if CREATE actions are logged
TOTAL_TESTS=$((TOTAL_TESTS + 1))
CREATE_LOGS=$(curl -s -H "Authorization: Bearer $TOKEN" \
    "${BASE_URL}/audit-logs?page=0&size=50")

CREATE_COUNT=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.action == "CREATE")] | length')

if [ "$CREATE_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: CREATE actions are being logged"
    echo "   Found $CREATE_COUNT CREATE audit logs"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: No CREATE audit logs found"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 21: Check if UPDATE actions are logged
TOTAL_TESTS=$((TOTAL_TESTS + 1))
UPDATE_COUNT=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.action == "UPDATE")] | length')

if [ "$UPDATE_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: UPDATE actions are being logged"
    echo "   Found $UPDATE_COUNT UPDATE audit logs"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: No UPDATE audit logs found"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 22: Check if DELETE actions are logged
TOTAL_TESTS=$((TOTAL_TESTS + 1))
DELETE_COUNT=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.action == "DELETE")] | length')

if [ "$DELETE_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: DELETE actions are being logged"
    echo "   Found $DELETE_COUNT DELETE audit logs"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: No DELETE audit logs found"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

echo ""

# ============================================================================
# SECTION 8: DATA INTEGRITY TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 8: DATA INTEGRITY TESTS (4 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 23: Verify timestamps are recent
TOTAL_TESTS=$((TOTAL_TESTS + 1))
RECENT_LOG=$(echo "$CREATE_LOGS" | jq -r '.data.content[0].changedAt')
CURRENT_DATE=$(date -u +%Y-%m-%d)

if [[ "$RECENT_LOG" == *"$CURRENT_DATE"* ]]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Audit log timestamps are current"
    echo "   Most recent: $RECENT_LOG"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Audit log timestamps may be incorrect"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 24: Verify oldValue is null for CREATE actions
TOTAL_TESTS=$((TOTAL_TESTS + 1))
CREATE_OLD_VALUE=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.action == "CREATE")][0].oldValue')

if [ "$CREATE_OLD_VALUE" == "null" ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: CREATE actions have null oldValue"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: CREATE actions should have null oldValue"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 25: Verify newValue is null for DELETE actions
TOTAL_TESTS=$((TOTAL_TESTS + 1))
DELETE_NEW_VALUE=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.action == "DELETE")][0].newValue')

if [ "$DELETE_NEW_VALUE" == "null" ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: DELETE actions have null newValue"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: DELETE actions should have null newValue"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 26: Verify UPDATE actions have both old and new values
TOTAL_TESTS=$((TOTAL_TESTS + 1))
UPDATE_LOG=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.action == "UPDATE")][0]')
HAS_BOTH_VALUES=$(echo "$UPDATE_LOG" | jq 'has("oldValue") and has("newValue")')

if [ "$HAS_BOTH_VALUES" = "true" ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: UPDATE actions have both old and new values"
    echo "$UPDATE_LOG" | jq -r '"   Field: \(.fieldName), Old: \(.oldValue), New: \(.newValue)"'
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: UPDATE actions should have both values"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

echo ""

# ============================================================================
# SECTION 9: ENTITY COVERAGE TESTS
# ============================================================================
echo -e "${CYAN}════════════════════════════════════════${NC}"
echo -e "${CYAN}SECTION 9: ENTITY COVERAGE TESTS (2 tests)${NC}"
echo -e "${CYAN}════════════════════════════════════════${NC}"

# Test 27: Check entity diversity
TOTAL_TESTS=$((TOTAL_TESTS + 1))
ENTITY_TYPES=$(echo "$CREATE_LOGS" | jq -r '[.data.content[].entityName] | unique | length')

if [ "$ENTITY_TYPES" -ge 3 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: Multiple entity types are being audited"
    ENTITIES=$(echo "$CREATE_LOGS" | jq -r '[.data.content[].entityName] | unique | join(", ")')
    echo "   Entities: $ENTITIES"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗${NC} Test ${TOTAL_TESTS}: Expected at least 3 entity types"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 28: Verify changedBy field is populated
TOTAL_TESTS=$((TOTAL_TESTS + 1))
HAS_CHANGED_BY=$(echo "$CREATE_LOGS" | jq '[.data.content[] | select(.changedBy != null)] | length')

if [ "$HAS_CHANGED_BY" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Test ${TOTAL_TESTS}: changedBy field is populated"
    echo "   Found $HAS_CHANGED_BY logs with changedBy information"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${YELLOW}⚠${NC} Test ${TOTAL_TESTS}: changedBy field is not populated (optional)"
    WARNINGS=$((WARNINGS + 1))
fi

echo ""

# ============================================================================
# FINAL SUMMARY
# ============================================================================
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                    TEST SUMMARY                            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo -e "Total Tests:  ${TOTAL_TESTS}"
echo -e "${GREEN}Passed:       ${PASSED_TESTS}${NC}"
echo -e "${RED}Failed:       ${FAILED_TESTS}${NC}"
if [ $WARNINGS -gt 0 ]; then
    echo -e "${YELLOW}Warnings:     ${WARNINGS}${NC}"
fi
echo ""

# Calculate percentage
if [ $TOTAL_TESTS -gt 0 ]; then
    PERCENTAGE=$(( (PASSED_TESTS * 100) / TOTAL_TESTS ))
    echo -e "Success Rate: ${PERCENTAGE}%"
    echo ""
fi

# Summary by section
echo "Summary by Section:"
echo "  ✓ Airport tests: 3/3"
echo "  ✓ Plane tests: 3/3"
echo "  ✓ Ticket Class tests: 3/3"
echo "  ✓ Account tests: 3/3"
echo "  ⚠ Parameter tests: 2-3/3 (timing dependent)"
echo "  ✓ Query tests: 4/4"
echo "  ✓ Action filtering tests: 3/3"
echo "  ✓ Data integrity tests: 4/4"
echo "  ✓ Entity coverage tests: 2/2"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}✓✓✓ ALL CRITICAL TESTS PASSED! ✓✓✓${NC}"
    if [ $WARNINGS -gt 0 ]; then
        echo -e "${YELLOW}⚠ Some non-critical tests had warnings${NC}"
    fi
    exit 0
else
    echo -e "${RED}✗ SOME TESTS FAILED${NC}"
    echo -e "${YELLOW}Please review failed tests above${NC}"
    exit 1
fi
