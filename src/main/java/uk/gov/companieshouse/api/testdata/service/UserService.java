package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

public interface UserService {

    /**
     * Checks if a user exists by their user ID.
     *
     * @param userId the ID of the user to check
     * @return true if the user exists, false otherwise
     */
    boolean userExists(String userId);

    /**
     * Creates a new user based on the provided user specifications.
     *
     * @param usersSpec the specifications of the user to create
     * @return the created user's test data
     * @throws DataException if there is an error during user creation
     */
    UserTestData create(UsersSpec usersSpec) throws DataException;

    /**
     * Deletes a user by their user ID.
     *
     * @param userId the ID of the user to delete
     * @throws DataException if there is an error during user deletion
     */
    void delete(String userId) throws DataException;
}
