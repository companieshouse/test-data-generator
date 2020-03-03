UNVERSIONED = unversioned

artifact_name       := test-data-generator
version             := ${UNVERSIONED}
docker_registry     := ${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com
docker_repository   := ${artifact_name}
exposed_port        := 10000

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

.PHONY: package # TODO: remove zip based packaging when fully replaced by docker build/tag/push below
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

# Docker build, tag, push and run targets replacing zip based packaging
.PHONY: docker-build
docker-build:
	docker build -t $(docker_repository):$(version) .

.PHONY: docker-run
docker-run:
	docker run -i -t -p $(exposed_port):$(exposed_port) --env-file=local_env $(docker_registry)/$(docker_repository):$(version)
