#!/bin/sh

mkdir -p m2 && ln -s "$(pwd)/m2" ~/.m2

version=$(cat version/version)
cd artifactory-resource
#update pom on detached head
sed -ie "s|<revision>.*</revision>|<revision>$version</revision>|" pom.xml

#build and test
./mvnw install -Prun-local-artifactory

git add pom.xml
git commit -m"Release version $version"

#tag
git tag v$version

major=$(echo $version | cut -d'.' -f 1)
minor=$(echo $version | cut -d'.' -f 2)
patch=$(echo $version | cut -d'.' -f 3)
#bump
snapshot_version=$major.$minor.$((patch+1))-SNAPSHOT
git reset --hard HEAD^1
sed -ie "s|<revision>.*</revision>|<revision>$snapshot_version</revision>|" pom.xml
git add pom.xml
git commit --message "v$snapshot_version Development"

cp artifactory-resource/target/artifactory-resource-*.jar generated-artifact
cp -r artifactory-resource artifactory-resource-updated