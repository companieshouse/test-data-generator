package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

public interface UserService extends DataService<UserTestData,UserSpec> {

    /**
     * Checks if a user exists by their user ID.
     *
     * @param userId the ID of the user to check
     * @return true if the user exists, false otherwise
     */
    boolean userExists(String userId);
}
