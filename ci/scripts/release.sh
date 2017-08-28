#!/bin/bash
set -e

source $(dirname $0)/common.sh

pushd git-repo > /dev/null
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
git reset --hard HEAD^1
set_revision_to_pom "$nextVersion"
git add pom.xml
git commit -m"Next development version (v$releaseVersion)"
echo "default login $username password $password" > $HOME/.netrc
git push origin "v$releaseVersion"
popd > /dev/null

cp git-repo/target/artifactory-resource.jar built-artifact/
echo $releaseVersion > built-artifact/version
build
