FROM openjdk:8-jdk-alpine

ARG root=.
ARG jar=target/artifactory-resource.jar

RUN pwd
RUN ls

ADD git-repo/assets/ /opt/resource/
ADD ${root}/assets/ /opt/resource/
ADD ${jar} /artifact/artifactory-resource.jar
RUN chmod +x /opt/resource/check /opt/resource/in /opt/resource/out