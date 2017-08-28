#!/bin/bash
set -e

source $(dirname $0)/common.sh

git clone git-repo release-git-repo

pushd release-git-repo > /dev/null
git branch
snapshotVersion=$( get_revision_from_pom )
releaseVersion=$( strip_snapshot_suffix "$snapshotVersion" )
nextVersion=$( bump_version_number "$snapshotVersion" )
echo "Releasing $releaseVersion (next SNAPSHOT will be $nextVersion)"
set_revision_to_pom "$releaseVersion"
git config user.name "Spring Buildmaster"
git config user.email "buildmaster@springframework.org"
git add pom.xml
git commit -m"Release v$releaseVersion"
git tag -a "v$releaseVersion" -m"Release version v$releaseVersion"
build
echo "Setting next development version (v$nextVersion)"
git reset --hard HEAD^
set_revision_to_pom "$nextVersion"
git add pom.xml
git commit -m"Next development version (v$releaseVersion)"
git show HEAD
popd > /dev/null

cp release-git-repo/target/artifactory-resource.jar built-artifact/
echo $releaseVersion > built-artifact/version
