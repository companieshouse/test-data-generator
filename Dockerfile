FROM centos:7

RUN yum update -y && \
    yum install -y \
    epel-release-7 \
    zip-3.0 \
    unzip-6.0 \
    java-1.8.0-openjdk \
    maven-3.0.5 \
    make-3.82 && \
    yum clean all

COPY test-data-generator.jar /root/test-data-generator.jar
COPY start-ecs /root/start-ecs

WORKDIR /root
CMD ["./start-ecs"]
