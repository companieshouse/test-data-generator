artifact_name       := test-data-generator
version             := "unversioned"
docker_registry     := 169942020521.dkr.ecr.eu-west-2.amazonaws.com
docker_image        := test-data-generator-temp
docker_tag          := test01

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./routes.yaml $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

# Docker

.PHONY: docker-build
docker-build: build # ADD THIS FOR BUILD DEPENDENCY LATER ######################################
	docker build -t $(docker_image):$(docker_tag) .

.PHONY: docker-tag
docker-tag:
	docker tag $(docker_image):$(docker_tag) $(docker_registry)/$(docker_image):$(docker_tag)

.PHONY: docker-push
docker-push:
	docker push $(docker_registry)/$(docker_image):$(docker_tag)
