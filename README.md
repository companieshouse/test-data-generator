Test data generator
==========
Service for generating and storing random test company data. This includes company profile, authentication code, filing history, officers and people of significant control.

Requirements
------------
In order to run the API locally you'll need the following installed on your machine:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com)


Getting started
---------------
1. Run `make`
2. Run `./start.sh`

## Environment Variables
The supported environmental variables have been categorised by use case and are as follows.

### Code Analysis Variables
Name                   | Description                                                                                                                               | Mandatory | Default | Example
---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | --------- | ------- | ------------------
CODE_ANALYSIS_HOST_URL | The host URL of the code analysis server. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                        | ✓         |         | http://HOST:PORT
CODE_ANALYSIS_LOGIN    | The analysis server account to use when analysing or publishing. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters) | ✓         |         | login
CODE_ANALYSIS_PASSWORD | The analysis server account password. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                            | ✓         |         | password

### Deployment Variables
Name                                   | Description                                                                                                                                                               | Mandatory | Default | Example
-------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------- | ------- | ----------------------------------------                                                                                                      
TEST_DATA_GENERATOR_PORT               | Configured port application runs on.                                                                                                                                      | ✓         |         | 4022             
MONGODB_URL                            | Mongo database URL.                                                                                                                                                       | ✓         |         | mongodb://localhost:27017
