#!/bin/sh

mkdir -p m2 && ln -s "$(pwd)/m2" ~/.m2

cd artifactory-resource
	current=xmllint --xpath '/*[local-name()="project"]/*[local-name()="properties"]/*[local-name()="revision"]/text() pom.xml'
	./mvnw install -Prun-local-artifactory
cd ..
echo $current > snapshot-version/version
echo "Current version is $current"
cp artifactory-resource/target/artifactory-resource-*.jar generated-artifact
