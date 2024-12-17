package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

public interface UsersTestDataService {
    UserTestData createUserTestData(UsersSpec usersSpec) throws DataException;
    void deleteUserTestData(String userId) throws DataException;
    boolean userExists(String userId) throws NoDataFoundException;
}
