#!/bin/sh

mkdir -p m2 && ln -s "$(pwd)/m2" ~/.m2

pushd artifactory-resource
	./mvnw install -Prun-local-artifactory
popd
cp artifactory-resource/target/artifactory-resource-*.jar generated-artifact
