package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.api.testdata.service.UsersTestDataService;

@Service
public class UserTestDataServiceImpl implements UsersTestDataService {

    @Autowired
    private UserService userService;

    @Override
    public UserTestData createUserTestData(UsersSpec usersSpec) throws DataException {
        UserTestData usersTestData = null;
        if(usersSpec.getPassword() == null) {
            throw new IllegalArgumentException("Password can not be null");
        }
        try {
            usersTestData = this.userService.createUser(usersSpec);
        } catch (Exception e) {
            throw new DataException("Failed to create user test data", e);
        }
        return new UserTestData(usersTestData.getUserId(),usersTestData.getEmail(),usersTestData.getForeName(),usersTestData.getSurName());
    }

    @Override
    public void deleteUserTestData(String userId) throws DataException {
        this.userService.deleteUser(userId);
    }

    @Override
    public boolean userExists(String userId) {
        return this.userService.userExits(userId);
    }
}
