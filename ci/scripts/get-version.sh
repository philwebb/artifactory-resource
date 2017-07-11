#!/bin/sh

cd artifactory-resource
#parse pom for version number
current=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="properties"]/*[local-name()="revision"]/text()' pom.xml | sed 's/-SNAPSHOT.*//')
#parse tags to get last one
last=$(git tag --list v$current.${pattern}* | sed "s/v${current}.${pattern}//" | sort -nr | head -n1)
#add one
version=$((last+1))
cd ..
echo $current-RELEASE > version/version