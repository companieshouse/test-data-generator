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
  - `jurisdiction`: The jurisdiction of the company (e.g., `england_wales`, `scotland`, `northern_ireland`, `united-kingdom`). Defaults to `england_wales`.
  - `company_status`: The status of the company (e.g., `active`, `dissolved`, `administration`). Defaults to `active`.
  - `type`: The type of the company (e.g., `ltd`, `plc`). Defaults to `ltd`.
  - `has_uk_establishment`: Boolean value to determine if the oversea company has a UK establishment. Defaults to false, `true` value will create an oversea company with a UK establishment. Used alongside `oversea-company` company type.
  - `sub_type`: The subtype of the company (e.g., `community-interest-company`, `private-fund-limited-partnership`). Defaults to no subtype.
  - `has_super_secure_pscs`: Boolean value to determine if the company has super secure PSCs. Defaults to false, `true` value will create a Psc entry of `super-secure-person-with-significant-control` or `super-secure-beneficial-owner` depending on CompanyType.
  - `registers` : The registers of the company (e.g., `directors`, `persons-with-significant-control`, ``). Defaults to no registers.
  - `number_of_appointments`: Used alongside `officer_roles` to determine the number of appointments to create. Defaults to 1.
  - `officer_roles`: This takes a list of officer roles (`director`, `secretary`). Defaults to director when no role is passed.
  - `accounts_due_status`: Set the accounts and confirmation statement due dates of the company by providing accounts_due_status (e.g., `overdue`, `due-soon`). Defaults to current date. 
  - `company_status_detail`: The status detail of the company (e.g., `active-proposal-to-strike-off`, `converted-to-plc`). Defaults to no value, field not present in the database.
  - `company_name`: The name of the company. Defaults to a randomly generated name. Creates a company with the name provided and appended with company number.
  - `is_company_number_padding`: Boolean value to determine if the company number should be padded with zeros. Defaults to false, `true` value will create a company number with leading zeros.
  - `filing_history`: {
    - `type`: The type of the submission (e.g., `GAZ1(A)`, `DS01`). Defaults to `NEWINC`.
    - `category`: The category of the filing (e.g., `incorporation`, `dissolution`, `gazette`). Defaults to `incorporation`.
    - `description`: The description of the filing (e.g., `incorporation-company`, `gazette-notice-voluntary`). Defaults to `incorporation-company`.
    - `original_description`: The original description of the filing (e.g., `First gazette notice for voluntary strike-off`). Defaults to `Certificate of incorporation general company details & statements of; officers, capital & shareholdings, guarantee, compliance memorandum of association`.
    - `number_of_psc`: The number of PSCs to create. Defaults to 0. Can be used to create multiple PSCs.
    - `psc_type`: Used alongside the `number_of_psc`. The types of PSCs to create (e.g., `individual`, `corporate`, `legal-person`, `individual-bo`, `corporate-bo`).
    }
  - `withdrawn_psc_statements`: Integer value to determine the number of withdrawn PSC statements to create. Defaults to 0.
  - `active_psc_statements`: Integer value to determine the number of active PSC statements to create. Defaults to 1 or `the number_of_psc` passed in the request.
  - `registered_office_is_in_dispute`: Boolean value to determine if the registered office is in dispute. Defaults to false.

  - A usage example for creating `registered-overseas-entity` looks like this: `{"registered-overseas-entity}`, this will create an overseas entity with hardcoded values
  - A usage example for creating `oversea-company` looks like this: `{"overseas-company}`, this will create an overseas entity with hardcoded values
  - A usage example for creating `oversea-company` with `has_uk_establishment` looks like this: `{"overseas-company", "has_uk_establishment": true}`, this will create an oversea company with hardcoded values and a UK establishment
  - A usage example looks like this: `{"jurisdiction":"scotland", "company_status":"administration", "type":"plc", "sub_type":"community-interest-company", "has_super_secure_pscs":true, "registers":["register_type": "directors", "register_moved_to": "public-register"], "accounts_due_status":"overdue", "company_status_detail":"active-proposal-to-strike-off", "filing_history": {"type": "GAZ1(A)", "category": "gazette", "description": "gazette-notice-voluntary", "original_description": "First gazette notice for voluntary strike-off"}, "number_of_appointments": 2, "officer_roles": ["director"]}`
  - A usage example for creating a company with psc: `{ "number_of_psc": 2, "psc_type": ["legal", "individual"] }`
  - A usage example for creating a company with withdrawn psc statements: `{ "withdrawn_psc_statements": 3 }`
  - A usage example for creating a company with active psc statements: `{ "active_psc_statements": 5 }`
  - A usage example for creating a company with registered office in dispute: `{ "registered_office_is_in_dispute": true }`
  - A usage example for creating a company with padded company number: `{ "is_company_number_padding": true }`
  - A usage example for creating a company with company name: `{ "company_name": "Test Company Ltd" }`

- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/company/{companyNumber}` will delete the test company. There is a required parameter that is Authcode which needs to be included in the request body to be allowed to delete the test company. A usage example looks like this: `{"auth_code":"222222"}`
- Health Check: Sending a GET request on the endpoint `{Base URL}/test-data/healthcheck` will return a status code and an empty response body.

#### Creating test users
- POST: Sending a POST request to create users with the associated roles `{Base URL}/test-data/user` will generate a new test user. The request body must include `UserSpec` parameter to customise the generated user.
    - `email`: The email id of the user. This is an optional field which defaults to randomly generated string + a test email domain.
    - `password`: The password of the user. This is mandatory.
    - `roles`: The roles of the user along with `permissions`. Roles is optional. If we provide the roles, we need to provide the `id` of the role and the `permissions` associated with the role. permissions are mandatory if we provide role id and vice versa.
    - `is_company_auth_allow_list`: This is optional. If we provide this, we need to provide the value as `true` or `false`.
    
    A usage example looks like this: `{ "password": "password", "roles": [ { "id": "roleId1", "permissions": [ "permission1", "permission2" ] }, { "id": "roleId2", "permissions": [ "permission3", "permission4" ] }, { "id": "roleId3", "permissions": [ "permission5", "permission6" ] } ], "is_company_auth_allow_list": true }`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/user/{userId}` will delete the test user. `userid` is required to delete the user.

#### Validating user Identity
- POST: Sending a POST request to validate the user identity `{Base URL}/test-data/identity` will validate the user identity. The request body must include `IdentitySpec` parameter to validate the user identity.
    - `email`: The email id of the user. This is mandatory.
    - `user_id`: The user id of the user. This is mandatory.
    - `verification_source`: The verification source of the user. This is mandatory.'
    
    A usage example looks like this: `{ "email": "test@test.com", "user_id": "userid", "verification_source": "TEST" }`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/identity/{identityId}` will delete the test user identity. `identityId` is required to delete the user identity.

#### Creating Acsp Members and Acsp Profiles
- POST: Sending a POST request to create Acsp Members and Acsp Profiles `{Base URL}/test-data/acsp-members` will generate a new Acsp Member and Acsp Profile. The request body must include mandatory `userId` and optional `AcspMembersSpec` and `AcspProfile` parameter to customise the generated Acsp Member and Acsp Profile.
    - `user_id`: The User ID of user from the user db. This is mandatory.
    - `user_role`: This is the role of the ACSP Member. This is optional
    - `status`: Status of the Acsp Member. This is optional
    - `acsp_profile`:
      - `type`: Company type of the AcspProfile. This is optional with a defaults to `limited-company`.
      - `status`: Status of the Acsp Profile. This is optional with a defaults to`active`.
      - `acsp_number`: ACSP number of the ACSP Profile. This is optional.
      - `business_sector`: Business sector of the ACSP Profile. This is optional.
      - `aml_details`:
          - `supervisory_body`: Supervisory body of the Acsp Profile. This is optional.
          - `membership_details`: Membership details of the Acsp Profile. This is optional.
      - `email`: The email of the AcspProfile. This is optional.

  A usage example looks like this: `{"user_id": "rsf3pdwywvse5yz55mfodfx8","user_role": "test","status": "test","acsp_profile": {"type": "test","status": "test", "acsp_number": "TestACSP", "aml_details": [ {"supervisory_body": "association-of-chartered-certified-accountants-acca","membership_details": "test"} ] } }`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/acsp-members/{acspMemberId}` will delete the test `Acsp Member` and associated `Acsp Profile`. `acspMemberId` is required to delete the Acsp Member.

#### Deleting Appeals
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/appeals` will delete the appeals by providing 
  - `company_number`: The company number of the company. This is mandatory.
  - `penalty_reference`: The penalty reference of the appeal. This is mandatory.
  
  A usage example looks like this: `{"company_number": "123456", "penalty_reference": "A0000001"}`

#### Adding Certificates and Basket
- POST: Sending a POST request to `{Base URL}/test-data/certificates` will order certificates for a company and add the basket details.
  - `company_name`: The name of the company.
  - `company_number`: The number of the company matching with company name.
  - `description_identifier`: The identifier description of the certificate.
  - `description_values`: 
    - `company_number`: The description value which has number of the company matching with company name.
    - `certificate`: The description value of the certificate.
  - `item_options`: {
    - `certificate_type`: The certificates types for the item.
    - `delivery_timescale`: The delivery time scale (e.g. `standard`, `express`).
    - `inclue_email_copy`: The boolean value for sending email copy. Default value is false.
    - `company_type`: The type of the company (e.g., `ltd`, `plc`).
    - `company_status`: The status of the company (e.g., `active`, `dissolved`, `administration`).
    }
  - `basket`: {
    - `forename`: The forename for basket delivery details.
    - `surname`: The surname for basket delivery details.
    - `enrolled`: To make the multi-item basket available to all users, this should be set to true. Default value is true.
      }
  - `kind`: The kind of the certificate.
  - `quantity`: The number of the certificate.
  - `postal_delivery`: The boolean value for certificate postal delivery. Default value is false.
  - `user_id`: The user id who logged in to order a certificate.
  
  - An usage example looks like this: `{"company_name" : "ACME Company", "company_number" : "KA000034", "description_identifier" : "certificate", "description_company_number" : "KA000034", "description_certificate" : "certificate for company KA000034", "item_options" : { "certificate_type" : "incorporation-with-all-name-changes", "delivery_timescale" : "standard", "include_email_copy" : true, "company_type" : "ltd", "company_status" : "active" }, "kind" : "item#certificate", "quantity" : 1, "postal_delivery": true, "user_id" : "RYCWjabPzgLvwBdlLmuhPsSpfkZ", "basket": { "forename": "John", "surname": "Doe", "enrolled": true } }`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/certificates/{id}` will delete the test certificate.

#### Retrieving, Updating and Deleting Account Penalties

- GET: Sending a GET request to retrieve the Account Penalties `{Base URL}/test-data/penalties`. The request body must include mandatory `companyCode` and `customer_code`.
  - `company_code`: The Company Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.
  - `customer_code`: The Customer Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.

  A usage example looks like this: `{"company_code": "LP", "customer_code": "12345678"}`
- GET: Sending a GET request to retrieve the Account Penalties for a specific Penalty `{Base URL}/test-data/penalties/{penaltyRef}` will get an Account Penalties entry with only the requested penalty being returned in the data object. The request body must include mandatory `companyCode` and `customer_code`.
  - `company_code`: The Company Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.
  - `customer_code`: The Customer Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.

  A usage example looks like this: `{"company_code": "LP", "customer_code": "12345678"}`
- PUT: Sending a PUT request to update Account Penalties for a specific Penalty `{Base URL}/test-data/penalties/{penaltyRef}` will update an Account Penalties entry. The request body must include mandatory `companyCode` and `customer_code`, and optional `createdAt`, `closedAt`, `isPaid`, `amount` and `outstandingAMount` parameters.
  - `company_code`: The Company Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.
  - `customer_code`: The Customer Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.
  - `created_at`: The Created At date of the Penalty being updated in the Account Penalties data in the account_penalties db collection. This cannot be set to null. This is optional.
  - `closed_at`: The Closed At date of the Penalty being updated in the Account Penalties data in the account_penalties db collection. This is optional.
  - `is_paid`: The Is Paid flag of the Penalty being updated in the Account Penalties data in the account_penalties db collection. This is optional.
  - `amount`: The Amount of the Penalty being updated in the Account Penalties data in the account_penalties db collection. This is optional.
  - `outstanding_amount`: The Amount of the Penalty being updated in the Account Penalties data in the account_penalties db collection. This is optional.

  A usage example looks like this: `{"company_code": "LP", "customer_code": "12345678", "created_at": "2026-06-07T14:04:23.512Z", "closed_at": "2026-06-07T14:04:23.512Z", "is_paid": true, "amount": 50, "outstanding_amount": 0}`
- DELETE: Sending a DELETE request on the endpoint `{Base URL}/test-data/penalties` will delete the `Account Penalties`. `companyCode` and `companyCode` are required.
  - `company_code`: The Company Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.
  - `customer_code`: The Customer Code of the Account Penalties entry in the account_penalties db collection. This is mandatory.

  A usage example looks like this: `{"company_code": "LP", "customer_code": "12345678"}`


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
