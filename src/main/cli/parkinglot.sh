#!/bin/bash

COMMAND="$1"
shift

COMMAND_SCRIPT="./commands/$COMMAND.sh"

if [[ -f "$COMMAND_SCRIPT" ]]; then
  source "$COMMAND_SCRIPT"
  run "$@"
else
  echo "Unknown command: $COMMAND"
  echo "Run 'parkinglot.sh help' for available commands"
fi

