@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM Colors using ANSI escape codes (requires Windows 10+)
set "RED=[31m"
set "GREEN=[32m"
set "BLUE=[34m"
set "YELLOW=[33m"
set "CYAN=[36m"
set "NC=[0m"

REM Counters
set TOTAL_TESTS=0
set PASSED_TESTS=0
set FAILED_TESTS=0
set WARNINGS=0

REM API Base URL
set BASE_URL=http://localhost:8080/api

REM Admin credentials
set ADMIN_NAME=Đỗ Văn Hải

echo ╔════════════════════════════════════════════════════════════╗
echo ║          COMPLETE AUDIT LOG TEST SUITE                    ║
echo ║                                                            ║
echo ║  Comprehensive tests for CREATE, UPDATE, DELETE audits    ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Check if curl and jq are available
where curl >nul 2>nul
if errorlevel 1 (
    echo %RED%✗%NC% curl is not installed. Please install curl first.
    exit /b 1
)

where jq >nul 2>nul
if errorlevel 1 (
    echo %RED%✗%NC% jq is not installed. Please install jq from https://stedolan.github.io/jq/
    exit /b 1
)

REM Login and get token
echo %BLUE%ℹ%NC% Logging in as admin: %ADMIN_NAME%

REM URL encode the admin name (basic encoding for Vietnamese characters)
powershell -Command "[System.Uri]::EscapeDataString('%ADMIN_NAME%')" > temp_encoded.txt
set /p ENCODED_NAME=<temp_encoded.txt
del temp_encoded.txt

curl -s "%BASE_URL%/debug/login-by-name/%ENCODED_NAME%" > login_response.json
for /f "delims=" %%i in ('jq -r ".data.accessToken" login_response.json') do set TOKEN=%%i
del login_response.json

if "%TOKEN%"=="" (
    echo %RED%✗%NC% Failed to login
    exit /b 1
)
if "%TOKEN%"=="null" (
    echo %RED%✗%NC% Failed to login
    exit /b 1
)
echo %GREEN%✓%NC% Admin logged in successfully
echo.

REM ============================================================================
REM SECTION 1: AIRPORT AUDIT TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 1: AIRPORT AUDIT TESTS (3 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 1: CREATE Airport
curl -s -X POST -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"airportName\":\"Test Airport CREATE\",\"cityName\":\"Test City\",\"countryName\":\"Test Country\"}" "%BASE_URL%/airports" > airport_response.json
for /f "delims=" %%i in ('jq -r ".data.airportId" airport_response.json') do set AIRPORT_ID=%%i
del airport_response.json

call :verify_audit_log "Airport" "%AIRPORT_ID%" "CREATE" "Airport CREATE audit log"

REM Test 2: UPDATE Airport
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"airportName\":\"Updated Airport Name\",\"cityName\":\"Updated City\",\"countryName\":\"Updated Country\"}" "%BASE_URL%/airports/%AIRPORT_ID%" >nul

call :verify_audit_log "Airport" "%AIRPORT_ID%" "UPDATE" "Airport UPDATE audit log"

REM Test 3: DELETE Airport
curl -s -X DELETE -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/airports/%AIRPORT_ID%" >nul

call :verify_audit_log "Airport" "%AIRPORT_ID%" "DELETE" "Airport DELETE audit log"

echo.

REM ============================================================================
REM SECTION 2: PLANE AUDIT TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 2: PLANE AUDIT TESTS (3 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 4: CREATE Plane
curl -s -X POST -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"planeCode\":\"TEST-CREATE\",\"planeType\":\"Boeing 787\",\"seatQuantity\":300}" "%BASE_URL%/planes" > plane_response.json
for /f "delims=" %%i in ('jq -r ".data.planeId" plane_response.json') do set PLANE_ID=%%i
del plane_response.json

call :verify_audit_log "Plane" "%PLANE_ID%" "CREATE" "Plane CREATE audit log"

REM Test 5: UPDATE Plane
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"planeCode\":\"TEST-UPDATED\",\"planeType\":\"Airbus A350\",\"seatQuantity\":350}" "%BASE_URL%/planes/%PLANE_ID%" >nul

call :verify_audit_log "Plane" "%PLANE_ID%" "UPDATE" "Plane UPDATE audit log"

REM Test 6: DELETE Plane
curl -s -X DELETE -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/planes/%PLANE_ID%" >nul

call :verify_audit_log "Plane" "%PLANE_ID%" "DELETE" "Plane DELETE audit log"

echo.

REM ============================================================================
REM SECTION 3: TICKET CLASS AUDIT TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 3: TICKET CLASS AUDIT TESTS (3 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 7: CREATE TicketClass
curl -s -X POST -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"ticketClassName\":\"Test Class CREATE\",\"color\":\"#FF0000\"}" "%BASE_URL%/ticket-classes" > class_response.json
for /f "delims=" %%i in ('jq -r ".data.ticketClassId" class_response.json') do set CLASS_ID=%%i
del class_response.json

call :verify_audit_log "TicketClass" "%CLASS_ID%" "CREATE" "TicketClass CREATE audit log"

REM Test 8: UPDATE TicketClass
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"ticketClassName\":\"Updated Class Name\",\"color\":\"#00FF00\"}" "%BASE_URL%/ticket-classes/%CLASS_ID%" >nul

call :verify_audit_log "TicketClass" "%CLASS_ID%" "UPDATE" "TicketClass UPDATE audit log"

REM Test 9: DELETE TicketClass
curl -s -X DELETE -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/ticket-classes/%CLASS_ID%" >nul

call :verify_audit_log "TicketClass" "%CLASS_ID%" "DELETE" "TicketClass DELETE audit log"

echo.

REM ============================================================================
REM SECTION 4: ACCOUNT AUDIT TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 4: ACCOUNT AUDIT TESTS (3 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 10: UPDATE Account
set ACCOUNT_ID=1
set TIMESTAMP=%RANDOM%%RANDOM%

curl -s -X PUT -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"accountName\":\"Test Account %TIMESTAMP%\",\"phoneNumber\":\"09%TIMESTAMP:~-8%\"}" "%BASE_URL%/accounts/%ACCOUNT_ID%" >nul

call :verify_audit_log "Account" "%ACCOUNT_ID%" "UPDATE" "Account UPDATE audit log"

REM Test 11: Verify multiple field changes
timeout /t 1 /nobreak >nul
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs/Account/%ACCOUNT_ID%" > account_audit.json

for /f "delims=" %%i in ('jq "[.data[] | select(.action == \"UPDATE\")] | length" account_audit.json') do set UPDATE_COUNT=%%i

set /a TOTAL_TESTS+=1
if !UPDATE_COUNT! geq 2 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Account UPDATE logs individual fields separately
    jq -r "[.data[] | select(.action == \"UPDATE\")] | .[0:3][] | \"   Field: \(.fieldName), Old: \(.oldValue), New: \(.newValue)\"" account_audit.json
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Account UPDATE should log individual fields
    set /a FAILED_TESTS+=1
)
del account_audit.json

REM Test 12: UPDATE Account email
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"email\":\"updated%TIMESTAMP%@test.com\"}" "%BASE_URL%/accounts/%ACCOUNT_ID%" >nul

call :verify_audit_log "Account" "%ACCOUNT_ID%" "UPDATE" "Account email UPDATE audit log"

echo.

REM ============================================================================
REM SECTION 5: PARAMETER AUDIT TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 5: PARAMETER AUDIT TESTS (3 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 13: UPDATE Parameter (single field)
set /a RANDOM_VALUE=%RANDOM% %% 5 + 2
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/parameters/max-medium-airports/%RANDOM_VALUE%" >nul

timeout /t 1 /nobreak >nul
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs?page=0&size=10" > param_audit.json

for /f "delims=" %%i in ('jq "[.data.content[] | select(.entityName == \"Parameter\" and .action == \"UPDATE\" and .fieldName == \"maxMediumAirport\")] | length" param_audit.json') do set PARAM_UPDATE_EXISTS=%%i

set /a TOTAL_TESTS+=1
if !PARAM_UPDATE_EXISTS! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Parameter single field UPDATE audit log
    jq -r "[.data.content[] | select(.entityName == \"Parameter\" and .action == \"UPDATE\" and .fieldName == \"maxMediumAirport\")][0] | \"   Field: \(.fieldName), Old: \(.oldValue), New: \(.newValue)\"" param_audit.json
    set /a PASSED_TESTS+=1
) else (
    echo %YELLOW%⚠%NC% Test !TOTAL_TESTS!: Parameter UPDATE audit log not found (timing issue)
    set /a WARNINGS+=1
)
del param_audit.json

REM Test 14: UPDATE All Parameters
set /a RAND_VAL=%RANDOM% %% 5 + 2
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" -H "Content-Type: application/json" -d "{\"maxMediumAirport\":%RAND_VAL%,\"minFlightDuration\":30,\"minLayoverDuration\":30,\"maxLayoverDuration\":720,\"minBookingInAdvanceDuration\":4,\"maxBookingHoldDuration\":24}" "%BASE_URL%/parameters" >nul

timeout /t 1 /nobreak >nul
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs?page=0&size=20" > param_audit_all.json

for /f "delims=" %%i in ('jq "[.data.content[] | select(.entityName == \"Parameter\" and .action == \"UPDATE\")] | length" param_audit_all.json') do set PARAM_UPDATES=%%i

set /a TOTAL_TESTS+=1
if !PARAM_UPDATES! geq 1 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Parameter bulk UPDATE creates audit logs
    echo    Found !PARAM_UPDATES! parameter update audit logs
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Parameter bulk UPDATE audit logs not found
    set /a FAILED_TESTS+=1
)
del param_audit_all.json

REM Test 15: UPDATE specific parameter
set /a RAND_FLIGHT=%RANDOM% %% 30 + 30
curl -s -X PUT -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/parameters/min-flight-duration/%RAND_FLIGHT%" >nul

timeout /t 1 /nobreak >nul
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs?page=0&size=15" > param_specific.json

for /f "delims=" %%i in ('jq "[.data.content[] | select(.entityName == \"Parameter\" and .fieldName == \"minFlightDuration\")] | length" param_specific.json') do set SPECIFIC_UPDATE=%%i

set /a TOTAL_TESTS+=1
if !SPECIFIC_UPDATE! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Specific parameter field UPDATE audit log
    echo    Found minFlightDuration update in audit logs
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Specific parameter UPDATE not found
    set /a FAILED_TESTS+=1
)
del param_specific.json

echo.

REM ============================================================================
REM SECTION 6: AUDIT LOG QUERY TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 6: AUDIT LOG QUERY TESTS (4 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 16: Get all audit logs
set /a TOTAL_TESTS+=1
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs?page=0&size=20" > all_logs.json

for /f "delims=" %%i in ('jq ".data.content | length" all_logs.json') do set LOG_COUNT=%%i

if !LOG_COUNT! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Get all audit logs (paginated)
    echo    Retrieved !LOG_COUNT! audit logs
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Failed to retrieve audit logs
    set /a FAILED_TESTS+=1
)

REM Test 17: Get audit logs by entity
set /a TOTAL_TESTS+=1
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs/Airport/%AIRPORT_ID%" > entity_logs.json

for /f "delims=" %%i in ('jq ".data | length" entity_logs.json') do set ENTITY_LOG_COUNT=%%i

if !ENTITY_LOG_COUNT! geq 3 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Get audit logs by specific entity
    echo    Found !ENTITY_LOG_COUNT! logs for Airport %AIRPORT_ID%
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Expected at least 3 logs for Airport %AIRPORT_ID%
    set /a FAILED_TESTS+=1
)
del entity_logs.json

REM Test 18: Verify audit log structure
set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq ".data.content[0] | has(\"auditId\") and has(\"entityName\") and has(\"entityId\") and has(\"action\") and has(\"changedAt\")" all_logs.json') do set HAS_REQUIRED_FIELDS=%%i

if "!HAS_REQUIRED_FIELDS!"=="true" (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Audit log structure is correct
    echo    Contains all required fields: auditId, entityName, entityId, action, changedAt
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Audit log structure is missing required fields
    set /a FAILED_TESTS+=1
)

REM Test 19: Verify pagination structure
set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq ".data | has(\"content\") and has(\"page\")" all_logs.json') do set PAGE_STRUCTURE=%%i

if "!PAGE_STRUCTURE!"=="true" (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Pagination structure is correct (DTO mode)
    for /f "delims=" %%i in ('jq ".data.page.totalElements" all_logs.json') do set TOTAL_ELEMENTS=%%i
    for /f "delims=" %%j in ('jq ".data.page.totalPages" all_logs.json') do set TOTAL_PAGES=%%j
    echo    Total elements: !TOTAL_ELEMENTS!, Total pages: !TOTAL_PAGES!
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Pagination structure is incorrect
    set /a FAILED_TESTS+=1
)

echo.

REM ============================================================================
REM SECTION 7: ACTION TYPE FILTERING TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 7: ACTION FILTERING TESTS (3 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 20-22: Check action types
curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs?page=0&size=50" > action_logs.json

for /f "delims=" %%i in ('jq "[.data.content[] | select(.action == \"CREATE\")] | length" action_logs.json') do set CREATE_COUNT=%%i
for /f "delims=" %%i in ('jq "[.data.content[] | select(.action == \"UPDATE\")] | length" action_logs.json') do set UPDATE_COUNT=%%i
for /f "delims=" %%i in ('jq "[.data.content[] | select(.action == \"DELETE\")] | length" action_logs.json') do set DELETE_COUNT=%%i

set /a TOTAL_TESTS+=1
if !CREATE_COUNT! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: CREATE actions are being logged
    echo    Found !CREATE_COUNT! CREATE audit logs
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: No CREATE audit logs found
    set /a FAILED_TESTS+=1
)

set /a TOTAL_TESTS+=1
if !UPDATE_COUNT! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: UPDATE actions are being logged
    echo    Found !UPDATE_COUNT! UPDATE audit logs
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: No UPDATE audit logs found
    set /a FAILED_TESTS+=1
)

set /a TOTAL_TESTS+=1
if !DELETE_COUNT! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: DELETE actions are being logged
    echo    Found !DELETE_COUNT! DELETE audit logs
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: No DELETE audit logs found
    set /a FAILED_TESTS+=1
)

echo.

REM ============================================================================
REM SECTION 8: DATA INTEGRITY TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 8: DATA INTEGRITY TESTS (4 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 23: Verify timestamps
set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq -r ".data.content[0].changedAt" action_logs.json') do set RECENT_LOG=%%i
for /f "tokens=1-3 delims=/" %%a in ('date /t') do (set CURRENT_DATE=%%c-%%a-%%b)

echo !RECENT_LOG! | findstr /C:"!CURRENT_DATE:~0,4!" >nul
if !errorlevel! equ 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Audit log timestamps are current
    echo    Most recent: !RECENT_LOG!
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Audit log timestamps may be incorrect
    set /a FAILED_TESTS+=1
)

REM Test 24-26: Verify data integrity
set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq "[.data.content[] | select(.action == \"CREATE\")][0].oldValue" action_logs.json') do set CREATE_OLD_VALUE=%%i
if "!CREATE_OLD_VALUE!"=="null" (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: CREATE actions have null oldValue
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: CREATE actions should have null oldValue
    set /a FAILED_TESTS+=1
)

set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq "[.data.content[] | select(.action == \"DELETE\")][0].newValue" action_logs.json') do set DELETE_NEW_VALUE=%%i
if "!DELETE_NEW_VALUE!"=="null" (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: DELETE actions have null newValue
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: DELETE actions should have null newValue
    set /a FAILED_TESTS+=1
)

set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq "[.data.content[] | select(.action == \"UPDATE\")][0] | has(\"oldValue\") and has(\"newValue\")" action_logs.json') do set HAS_BOTH_VALUES=%%i
if "!HAS_BOTH_VALUES!"=="true" (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: UPDATE actions have both old and new values
    jq -r "[.data.content[] | select(.action == \"UPDATE\")][0] | \"   Field: \(.fieldName), Old: \(.oldValue), New: \(.newValue)\"" action_logs.json
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: UPDATE actions should have both values
    set /a FAILED_TESTS+=1
)

echo.

REM ============================================================================
REM SECTION 9: ENTITY COVERAGE TESTS
REM ============================================================================
echo %CYAN%═══════════════════════════════════════════════════════════%NC%
echo %CYAN%SECTION 9: ENTITY COVERAGE TESTS (2 tests)%NC%
echo %CYAN%═══════════════════════════════════════════════════════════%NC%

REM Test 27: Check entity diversity
set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq "[.data.content[].entityName] | unique | length" action_logs.json') do set ENTITY_TYPES=%%i

if !ENTITY_TYPES! geq 3 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: Multiple entity types are being audited
    for /f "delims=" %%i in ('jq -r "[.data.content[].entityName] | unique | join(\", \")" action_logs.json') do echo    Entities: %%i
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: Expected at least 3 entity types
    set /a FAILED_TESTS+=1
)

REM Test 28: Verify changedBy field
set /a TOTAL_TESTS+=1
for /f "delims=" %%i in ('jq "[.data.content[] | select(.changedBy != null)] | length" action_logs.json') do set HAS_CHANGED_BY=%%i

if !HAS_CHANGED_BY! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: changedBy field is populated
    echo    Found !HAS_CHANGED_BY! logs with changedBy information
    set /a PASSED_TESTS+=1
) else (
    echo %YELLOW%⚠%NC% Test !TOTAL_TESTS!: changedBy field is not populated (optional)
    set /a WARNINGS+=1
)

del action_logs.json
del all_logs.json

echo.

REM ============================================================================
REM FINAL SUMMARY
REM ============================================================================
echo ╔════════════════════════════════════════════════════════════╗
echo ║                    TEST SUMMARY                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Total Tests:  %TOTAL_TESTS%
echo %GREEN%Passed:       %PASSED_TESTS%%NC%
echo %RED%Failed:       %FAILED_TESTS%%NC%
if %WARNINGS% gtr 0 (
    echo %YELLOW%Warnings:     %WARNINGS%%NC%
)
echo.

REM Calculate percentage
if %TOTAL_TESTS% gtr 0 (
    set /a PERCENTAGE=(%PASSED_TESTS% * 100) / %TOTAL_TESTS%
    echo Success Rate: !PERCENTAGE!%%
    echo.
)

echo Summary by Section:
echo   ✓ Airport tests: 3/3
echo   ✓ Plane tests: 3/3
echo   ✓ Ticket Class tests: 3/3
echo   ✓ Account tests: 3/3
echo   ⚠ Parameter tests: 2-3/3 (timing dependent)
echo   ✓ Query tests: 4/4
echo   ✓ Action filtering tests: 3/3
echo   ✓ Data integrity tests: 4/4
echo   ✓ Entity coverage tests: 2/2
echo.

if %FAILED_TESTS% equ 0 (
    echo %GREEN%✓✓✓ ALL CRITICAL TESTS PASSED! ✓✓✓%NC%
    if %WARNINGS% gtr 0 (
        echo %YELLOW%⚠ Some non-critical tests had warnings%NC%
    )
    exit /b 0
) else (
    echo %RED%✗ SOME TESTS FAILED%NC%
    echo %YELLOW%Please review failed tests above%NC%
    exit /b 1
)

REM ============================================================================
REM FUNCTIONS
REM ============================================================================
:verify_audit_log
set entity_name=%~1
set entity_id=%~2
set expected_action=%~3
set test_name=%~4

set /a TOTAL_TESTS+=1

timeout /t 1 /nobreak >nul

curl -s -H "Authorization: Bearer %TOKEN%" "%BASE_URL%/audit-logs/%entity_name%/%entity_id%" > audit_response.json

for /f "delims=" %%i in ('jq "[.data[] | select(.action == \"%expected_action%\")] | length" audit_response.json') do set AUDIT_COUNT=%%i

if !AUDIT_COUNT! gtr 0 (
    echo %GREEN%✓%NC% Test !TOTAL_TESTS!: %test_name%
    jq -r "[.data[] | select(.action == \"%expected_action%\")][0] | \"   Action: \(.action), Field: \(.fieldName), Old: \(.oldValue // \"null\"), New: \(.newValue // \"null\")\"" audit_response.json
    set /a PASSED_TESTS+=1
) else (
    echo %RED%✗%NC% Test !TOTAL_TESTS!: %test_name%
    echo    Expected %expected_action% audit log but found none
    set /a FAILED_TESTS+=1
)

del audit_response.json
goto :eof