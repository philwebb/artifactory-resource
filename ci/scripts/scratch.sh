#!/bin/bash
set -e
source $(dirname $0)/common.sh

version=$(get_revision_from_pom)
echo $version

