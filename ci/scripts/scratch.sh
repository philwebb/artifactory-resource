#!/bin/bash
set -e
# set -x
source $(dirname $0)/common.sh

version=$(get_relase_version "1.2.3.RELEASE")
echo $version

