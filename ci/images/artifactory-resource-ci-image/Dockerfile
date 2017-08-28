FROM openjdk:8u141-jdk

RUN apt-get update && \
    apt-get -y install net-tools && \
    apt-get -y install libxml2-utils && \
    apt-get install -y git

ADD https://raw.githubusercontent.com/philwebb/concourse-java-scripts/414d526afc358824fba368b11299ff2555829c36/concourse-java.sh /opt/