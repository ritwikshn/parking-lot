# Summary: Issue a new parking ticket for a vehicle

run() {
  if [[ "$1" == "--help" ]]; then
    echo "$(print_usage)"
    return
  fi

  local vehicle_type="$1"
  local vehicle_license_number="$2"

  if [[ -z "$vehicle_type" || -z "$vehicle_license_number" ]]; then
    echo "Vehicle type and vehicle license number are required"
    echo ""
    echo "$(print_usage)"
    return
  fi

  source "./utils/http.sh"
  post "parkingLot/entranceGate/assignAndGetParkingTicket" \
        "{ \"vehicleType\" : \"$vehicle_type\",
        \"vehicleLicenseNumber\" : \"$vehicle_license_number\" }"
}

print_usage() {
  echo "Usage: get_ticket <vehicle_type> <vehicle_license_number>"
  echo "Issue a new parking ticket for a vehicle"
}