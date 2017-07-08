#!/bin/sh

mkdir -p m2 && ln -s "$(pwd)/m2" ~/.m2

cd artifactory-resource
./mvnw install -Prun-local-artifactory

mkdir generated-artifact
cp target/artifactory-resource-*.jar generated-artifact/
