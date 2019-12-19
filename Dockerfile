FROM centos:7.6.1810

RUN yum update -y && \
    yum install -y \
    epel-release-7-11 \
    zip-3.0-11.el7 \
    unzip-6.0-19.el7 \
    java-1.8.0-openjdk-1.8.0.191.b12-1.el7_6 \
    maven-3.0.5-17.el7 \
    make-3.82-23.el7 && \
    yum clean all

COPY test-data-generator.jar /root/test-data-generator.jar
COPY start-ecs /root/start-ecs

WORKDIR /root
CMD ["./start-ecs"]
