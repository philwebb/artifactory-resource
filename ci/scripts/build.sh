#!/bin/bash
set -e

source $(dirname $0)/common.sh

setup_symlinks
cleanup_maven_repo "io.spring.concourse.artifactoryresource"

pushd git-repo > /dev/null
version=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="properties"]/*[local-name()="revision"]/text()' pom.xml)
run_maven clean install -Prun-local-artifactory -DskipTests
popd > /dev/null

cp git-repo/target/artifactory-resource.jar built-artifact/
echo $version > built-artifact/version
