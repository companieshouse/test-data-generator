package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

import java.util.List;

public interface UserService extends DataService<UserTestData,UserSpec> {
    /**
     * Retrieves the roles associated with a given user ID.
     *
     * @param userId the ID of the user whose roles are to be retrieved
     * @return a list of role IDs associated with the user
     */
    List<String> getRolesByUserId(String userId);
}
