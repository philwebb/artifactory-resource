#!/bin/bash
set -e

source $(dirname $0)/common.sh

setup_symlinks
cleanup_maven_repo

pushd git-repo > /dev/null
run_maven clean install -Prun-local-artifactory
popd > /dev/null

cp git-repo/target/artifactory-resource.jar built-artifact/
ls -l built-artifact
