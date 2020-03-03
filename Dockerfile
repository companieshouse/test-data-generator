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

COPY test-data-generator.jar /root/test-data-generator.jar
COPY start-ecs /root/start-ecs

WORKDIR /root
CMD ["./start-ecs"]
