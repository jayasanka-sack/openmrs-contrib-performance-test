#!/bin/bash
set -e

# Delete everything except the target directory
shopt -s extglob
rm -rf !("target")

# Identify the directory starting with test-simulation- inside target/gatling/
report=$(find target/gatling -maxdepth 1 -type d -name "testsimulation-*" | head -n 1)

echo $report
# Check if the report directory exists
if [ -d "$report" ]; then
  # Copy the directory to the root
  cp -r "$report"/* .

  # Delete the target directory
  rm -rf target

  # Add all changes to git
  git add --all

  timestamp=$(date +"%Y-%m-%d %H:%M:%S")
  git commit -m "Update report: $timestamp"

  git log
else
  echo "No directory found starting with 'testsimulation-' in target/gatling/"
fi
