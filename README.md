Test data generator
==========
Service for generating and storing random test company data. This includes company profile, authentication code, filing history, officers and people of significant control.

As new filings are exposed to external software vendors more test data options should be added to this service and the relevant public specs/docs updated.

Requirements
------------
In order to run the API locally you'll need the following installed on your machine:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com)


Getting started
---------------
1. Run `make`
2. Run `./start.sh`

Docker
------

There is a Docker service for this project called `test-data-generator` which can be used for development and test in the CHS Dev Env.

The docker compose file for this service is `docker-chs-development/services/test-utils/test-data-generator.docker-compose.yaml ` for correct operation under CHS Dev Env pay attention to the following clauses:

    environment:
      - TEST_DATA_GENERATOR_PORT=4022
      - MONGODB_URL=mongodb://mongo:27017
      - BARCODE_SERVICE_URL=http://barcode-generator:18101
      - HUMAN_LOG=1
      - API_URL=http://localhost:8080
    expose:
      - 4022
    ports:
      - "4022:4022"

### Usage
In order to use the generator, there are different possible endpoints that can be used.

#### Creating test companies
- POST: Sending a POST request to `{Base URL}/test-data/company` will generate a new test company and accompanying Authcode. The request body can include an optional `CompanySpec` parameter to customise the generated company.
  - `jurisdiction`: The jurisdiction of the company (e.g., `england_wales`, `scotland`, `northern_ireland`). Defaults to `england_wales`.
  - `company_status`: The status of the company (e.g., `active`, `dissolved`, `administration`). Defaults to `active`.
  - `type`: The type of the company (e.g., `ltd`, `plc`). Defaults to `ltd`.

  An usage example looks like this: `{"jurisdiction":"scotland", "company_status":"administration", "type":"plc"}`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/company/{companyNumber}` will delete the test company. There is a required parameter that is Authcode which needs to be included in the request body to be allowed to delete the test company. An usage example looks like this: `{"auth_code":"222222"}`
- Health Check: Sending a GET request on the endpoint `{Base URL}/test-data/healthcheck` will return a status code and an empty response body.

#### Creating test users
- POST: Sending a POST request to create users with the associated roles `{Base URL}/test-data/user` will generate a new test user. The request body must include `UserSpec` parameter to customise the generated user.
    - `password`: The password of the user. This is mandatory.
    - `roles`: The roles of the user along with `permissions`. Roles is optional and defaults to an empty list. If we provide the roles, we need to provide the `_id` of the role and the `permissions` associated with the role. permissions are mandatory if we provide role id and vice versa.
    
    An usage example looks like this: `{ "password": "password", "roles": [ { "_id": "roleId1", "permissions": [ "permission1", "permission2" ] }, { "_id": "roleId2", "permissions": [ "permission3", "permission4" ] }`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/user/{userId}` will delete the test user. `userid` is required to delete the user.

## Environment Variables
The supported environmental variables have been categorised by use case and are as follows.

### Code Analysis Variables
| Name                   | Description                                                                                                                               | Mandatory | Default | Example          |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|-----------|---------|------------------|
| CODE_ANALYSIS_HOST_URL | The host URL of the code analysis server. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                        | ✓         |         | http://HOST:PORT |
| CODE_ANALYSIS_LOGIN    | The analysis server account to use when analysing or publishing. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters) | ✓         |         | login            |
| CODE_ANALYSIS_PASSWORD | The analysis server account password. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                            | ✓         |         | password         |

### Deployment Variables
| Name                     | Description                          | Mandatory | Default | Example                   |
|--------------------------|--------------------------------------|-----------|---------|---------------------------|
| TEST_DATA_GENERATOR_PORT | Configured port application runs on. | ✓         |         | 4022                      |
| MONGODB_URL              | Mongo database URL.                  | ✓         |         | mongodb://localhost:27017 |
| BARCODE_SERVICE_URL      | URL of barcode service               | ✓         |         | http://localhost:9000     |
| API_URL                  | URL of (company) API service         | ✓         |         |                           |
