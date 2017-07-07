FROM openjdk:8-jdk-alpine

ADD assets/ /opt/resource/
ADD generated-artifact/* /artifact/
RUN chmod +x /opt/resource/check /opt/resource/in /opt/resource/out