package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.api.testdata.service.UsersTestDataService;

@Service
public class UserTestDataServiceImpl implements UsersTestDataService {

    @Autowired
    private UserService userService;

    @Override
    public UserTestData createUserTestData(UsersSpec usersSpec) {
        UserTestData usersTestData = null;
        if(usersSpec.getRoles() == null) {
            throw new IllegalArgumentException("RolesSpec can not be null");
        }
        try {
           usersTestData = this.userService.creteUser(usersSpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user test data", e);
        }
        return new UserTestData(usersTestData.getUserId(),usersTestData.getEmail(),usersTestData.getForeName(),usersTestData.getSurName());
    }

    @Override
    public void deleteUserTestData(String userId) {
        if(userId == null) {
            throw new IllegalArgumentException("User Id can not be null");
        }
        this.userService.deleteUser(userId);
    }
}
