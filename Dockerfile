FROM openjdk:8-jdk-alpine

ADD assets/ /opt/resource/
ADD target/artifactory-resource-1.0.0-SNAPSHOT.jar /artifact/artifactory-resource.jar
RUN chmod +x /opt/resource/check /opt/resource/in /opt/resource/out