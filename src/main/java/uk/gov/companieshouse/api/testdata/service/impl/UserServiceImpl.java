package uk.gov.companieshouse.api.testdata.service.impl;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public boolean userExits(String userId) {
        return false;
    }

    @Override
    public void creteUser(Users user) throws DataException {

    }

    @Override
    public void deleteUser(String userId) {

    }
}
