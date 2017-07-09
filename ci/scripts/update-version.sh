#!/bin/sh

mkdir -p m2 && ln -s "$(pwd)/m2" ~/.m2

cd artifactory-resource
	./mvnw versions:set -DnewVersion=$RELEASE_VERSION -DgenerateBackupPoms=false
	./mvnw install -DskipTests
cd ..
cp artifactory-resource/target/artifactory-resource-*.jar generated-artifact
cp artifactory-resource/. artifactory-resource-updated