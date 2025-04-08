package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;

import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;

public interface UserService extends DataService<UserData, UserSpec> {
    /**
     * Retrieves the roles associated with a given user ID.
     *
     * @param userId the ID of the user whose roles are to be retrieved
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> getUserById(String userId);

    void updateUserWithOneLogin(String userId);
}
