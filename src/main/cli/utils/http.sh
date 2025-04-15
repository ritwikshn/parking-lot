# Utility for http related stuff

SERVER_BASE_URL="http://localhost:8080/api"

_http_request() {
  local method="$1"
  local endpoint="$2"
  local data="$3"

  local url="$SERVER_BASE_URL/$endpoint"
  echo "$url"
  if [[ -n "$data" ]]; then
    response=$(curl --location "$url" \
                    -X "$method" -s -w "\n%{response_code}" \
                    -H "Content-Type: application/json" \
                    -d "$data")
  else
    response=$(curl --location "$url" \
                    -X "$method" -s -w "\n%{response_code}" \
                    -H "Accept: application/json")
  fi

  response_body=$(echo "$response" | sed '$d')
  response_status=$(echo "$response" | tail -n1)

  echo "$response_body" | jq -R '. as $line | try (fromjson) catch $line'

  if [[ ! "$response_status" =~ ^2 ]]; then
    echo "Error: http status $response_status" >&2
    return 1
  fi
}
post() {
  _http_request "POST" "$1" "$2"
}

get() {
  _http_request "GET" "$1"
}

put() {
  _http_request "PUT" "$1" "$2"
}

delete() {
  _http_request "DELETE" "$1"
}
