FROM openjdk:8u141-jdk

RUN apt-get update && \
    apt-get -y install net-tools && \
    apt-get -y install libxml2-utils && \
    apt-get install -y git

ADD https://raw.githubusercontent.com/philwebb/concourse-java-scripts/79a456d7ec8d6c866ec199369493c55bc905d745/concourse-java.sh /opt/