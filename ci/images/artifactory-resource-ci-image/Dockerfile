FROM openjdk:8u141-jdk

RUN apt-get update && \
    apt-get -y install net-tools && \
    apt-get -y install libxml2-utils && \
    apt-get install -y git

ADD https://raw.githubusercontent.com/philwebb/concourse-java-scripts/c62b882385b426e0d2d1d54b6825bcc5551b3dcc/concourse-java.sh /opt/