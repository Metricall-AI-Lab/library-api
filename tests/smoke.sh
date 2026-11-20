#!/bin/sh
# Smoke tests against a running instance. BASE defaults to the app inside its own container.
set -eu
BASE="${BASE:-http://localhost:8080}"
pass=0; fail=0
check() { # name expected actual
  if [ "$2" = "$3" ]; then pass=$((pass+1)); echo "ok   $1"; else fail=$((fail+1)); echo "FAIL $1: expected $2, got $3"; fi
}

check "health answers 200" 200 "$(curl -s -o /dev/null -w '%{http_code}' "$BASE/health")"
check "books list is seeded" 4 "$(curl -s "$BASE/books" | grep -o '"id":' | wc -l | tr -d ' ')"
created=$(curl -s -X POST -H 'content-type: application/json' -d '{"title":"Smoke Test","author":"CI"}' "$BASE/books")
id=$(echo "$created" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
check "a book can be created" "Smoke Test" "$(echo "$created" | sed -n 's/.*"title":"\([^"]*\)".*/\1/p')"
check "a missing book is 404" 404 "$(curl -s -o /dev/null -w '%{http_code}' "$BASE/books/999999")"
check "a book can be lent" 200 "$(curl -s -o /dev/null -w '%{http_code}' -X POST -H 'content-type: application/json' -d '{"to":"maya"}' "$BASE/books/$id/lend")"
check "a book can be returned" 200 "$(curl -s -o /dev/null -w '%{http_code}' -X POST "$BASE/books/$id/return")"

echo "tests $((pass+fail)) · pass $pass · fail $fail"
[ "$fail" -eq 0 ]
