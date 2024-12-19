package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;

public interface UsersTestDataService {

    /**
     * Creates a new user test data based on the provided user specifications.
     *
     * @param usersSpec the specifications of the user to create
     * @return the created user's test data
     * @throws DataException if there is an error during user creation
     */
    UserTestData createUserTestData(UsersSpec usersSpec) throws DataException;

    /**
     * Deletes a user test data by their user ID.
     *
     * @param userId the ID of the user to delete
     * @throws DataException if there is an error during user deletion
     */
    void deleteUserTestData(String userId) throws DataException;

    /**
     * Checks if a user exists by their user ID.
     *
     * @param userId the ID of the user to check
     * @return true if the user exists, false otherwise
     * @throws NoDataFoundException if no data is found for the user
     */
    boolean userExists(String userId) throws NoDataFoundException;
}
