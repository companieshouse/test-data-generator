package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;

public interface UserService {
    boolean userExits(String userId);
    void creteUser(Users user) throws DataException;
    void deleteUser(String userId);
}
