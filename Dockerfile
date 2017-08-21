FROM openjdk:8-jdk-alpine

ARG checkout=.
ARG jar=target/artifactory-resource.jar

ADD ${checkout}/assets/ /opt/resource/
ADD ${jar} /artifact/artifactory-resource.jar
RUN chmod +x /opt/resource/check /opt/resource/in /opt/resource/out