FROM centos:7

RUN yum update -y && \
    yum install -y \
    epel-release-7 \
    zip \
    unzip \
    java-1.8.0-openjdk \
    maven \
    make && \
    yum clean all

COPY test-data-generator.jar /opt/test-data-generator/
COPY start-ecs /usr/local/bin/

CMD ["start-ecs"]
