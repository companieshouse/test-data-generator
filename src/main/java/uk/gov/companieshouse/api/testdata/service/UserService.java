package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.RolesSpec;

public interface UserService {
    boolean userExits(String userId);
    void creteUser(RolesSpec roles) throws DataException;
    void deleteUser(String userId);
}
