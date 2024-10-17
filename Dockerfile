FROM centos:7

# Could not resolve host: mirrorlist.centos.org - Centos 7 has reached EOL 1 July 2024, repoint to archive vault
# Temporary fix while generator is on java 8
RUN sed -i 's/^mirrorlist/#mirrorlist/g' /etc/yum.repos.d/CentOS-*
RUN sed -i 's|^#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-*

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

RUN chmod 555 /usr/local/bin/start-ecs

CMD ["start-ecs"]
