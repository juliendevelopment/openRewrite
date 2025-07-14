#!/bin/bash

# This script finds all pom.xml files that DO NOT have '<packaging>pom</packaging>',
# starting from a directory provided as an argument. For each of them, it runs a command


# --- Input Validation ---
if [ -z "$1" ]; then
  echo "Error: No search directory provided." >&2
  echo "Usage: $0 <path/to/your/directory>" >&2
  exit 1
fi

SEARCH_DIR="$1"

if [ ! -d "$SEARCH_DIR" ]; then
  echo "Error: Directory '$SEARCH_DIR' does not exist." >&2
  exit 1
fi
# --- End of Validation ---


echo "Searching in '$SEARCH_DIR' for Maven projects that are not <packaging>pom</packaging>..."
echo ""

find "$SEARCH_DIR" -name "pom.xml" -print0 | while IFS= read -r -d '' pom_file; do


  if grep -q -v '<packaging>pom</packaging>' "$pom_file"; then

    project_dir=$(dirname "$pom_file")

    echo "--------------------------------------------------"
    echo "Found a valid project in: $project_dir"

    (
      cd "$project_dir" || exit
      pwd

#      put your command Here
    )
    echo ""
  fi
done

echo "--------------------------------------------------"
echo "excussion  complete."