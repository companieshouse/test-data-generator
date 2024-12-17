package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

public interface UserService {
    boolean userExits(String userId);
    UserTestData createUser(UsersSpec roles) throws DataException;
    void deleteUser(String userId) throws DataException;
}
