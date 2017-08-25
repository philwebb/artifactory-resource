FROM openjdk:8u141-jdk

RUN apt-get update && \
    apt-get -y install net-tools && \
    apt-get -y install libxml2-utils && \
    apt-get install -y git

ADD https://raw.githubusercontent.com/philwebb/concourse-java-scripts/e8332cce785961c717bd61f7e582df871d7c9a1d/concourse-java.sh /opt/