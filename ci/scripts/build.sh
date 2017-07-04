#!/bin/sh

mkdir m2
ln -s m2 ~/.m2

cd artifactory-resource
./mvnw install -DskipITs