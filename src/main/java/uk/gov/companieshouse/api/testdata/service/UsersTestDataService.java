package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

public interface UsersTestDataService {
    UserTestData createUserTestData(UsersSpec usersSpec);
    void deleteUserTestData(String userId);
}
