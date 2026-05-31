#!/bin/bash

# Configuration
BASE_URL=${1:-"http://localhost:8081"}
EXIT_CODE=0

echo "=========================================================="
echo " Starting Integration Pipeline Tests for TUB Backend API"
echo " Target API: $BASE_URL"
echo "=========================================================="

# Function to test endpoints
test_endpoint() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local auth_token=$4
    local data=$5
    local test_name=$6

    echo -n "Running Test: $test_name... "

    local curl_opts=("-s" "-X" "$method" "$BASE_URL$endpoint" "-H" "Content-Type: application/json" "-w" "|||%{http_code}")
    
    if [ ! -z "$auth_token" ]; then
        curl_opts+=("-H" "Authorization: Bearer $auth_token")
    fi

    if [ ! -z "$data" ]; then
        curl_opts+=("-d" "$data")
    fi

    local response=$(curl "${curl_opts[@]}")
    local body="${response%|||*}"
    local status="${response##*|||}"

    if [ "$status" = "$expected_status" ]; then
        echo "✅ PASS ($status)"
        # Return the body if needed
        echo "$body" > /tmp/curl_response.json
    else
        echo "❌ FAIL ($status, expected $expected_status)"
        # echo "Response Body: $body"
        EXIT_CODE=1
    fi
}

# --- TEST SUITE ---

# 1. Test Authentication
echo -e "\n--- AUTHENTICATION ---"
test_endpoint "POST" "/api/auth/login" "200" "" '{"email":"admin@tub.pt","password":"1234"}' "Login Admin"
TOKEN=$(cat /tmp/curl_response.json | grep -o '"token":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
    echo "❌ CRITICAL: Could not obtain JWT Token. Aborting subsequent tests."
    exit 1
fi

# 2. Test Dashboard KPIs
echo -e "\n--- DASHBOARD ---"
test_endpoint "GET" "/api/dashboard/kpis" "200" "$TOKEN" "" "Get KPIs"
test_endpoint "GET" "/api/alertas" "200" "$TOKEN" "" "Get Operational Alerts"

# 3. Test Users Management
echo -e "\n--- UTILIZADORES ---"
test_endpoint "GET" "/api/utilizadores" "200" "$TOKEN" "" "List Users"
TIMESTAMP=$(date +%s)
test_endpoint "POST" "/api/utilizadores/guardar" "200" "$TOKEN" "{\"nome\":\"Utilizador Pipeline\",\"email\":\"pipeline_$TIMESTAMP@tub.pt\",\"password\":\"password123\",\"cargo\":\"OPERADOR\",\"ativo\":true}" "Create Test User"

# 4. Test IoT / Fleet
echo -e "\n--- IOT / FROTA ---"
test_endpoint "GET" "/api/frota/posicoes" "200" "$TOKEN" "" "List Fleet Tracking Data"
test_endpoint "GET" "/api/bilhetica/analise?linha=vazia" "200" "$TOKEN" "" "Get Ticketing Analysis"

# 5. Test DMS Panels
echo -e "\n--- PAINÉIS DMS ---"
test_endpoint "GET" "/api/paineis" "200" "$TOKEN" "" "Get Panels Status"

echo -e "\n=========================================================="
if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ ALL PIPELINE TESTS PASSED!"
else
    echo "❌ PIPELINE TESTS FAILED."
fi

rm -f /tmp/curl_response.json
exit $EXIT_CODE
