# Summary: Get more information on usage

run() {
  echo "Usage: parkinglot.sh <command> [options|arguments...]"
  echo ""
  echo "Available commands:"

  for cmd in ./commands/*sh; do
    cmd_name=$(basename "$cmd" .sh)
    summary=$(get_command_summary "$cmd")
    printf "  %-15s %s\n" "$cmd_name" "$summary"
  done

  echo ""
  echo "Run 'parkinglot.sh <command> --help' for more information on a specific command"
}

get_command_summary() {
  local cmd_file="$1"
  local summary=$(grep '^# Summary' "$cmd_file" | head -n1 | cut -d':' -f2- | xargs)
  echo "${summary:-No description available}"
}