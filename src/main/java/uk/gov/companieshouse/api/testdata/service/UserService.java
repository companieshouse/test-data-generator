package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;

public interface UserService extends DataService<UserResponse, UserRequest> {
    /**
     * Retrieves the roles associated with a given user ID.
     *
     * @param userId the ID of the user whose roles are to be retrieved
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> getUserById(String userId);

    /**
     * Retrieves the roles associated with a given user email.
     *
     * @param email the email of the user whose roles are to be retrieved
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Deleted the User associated with a given user email.
     *
     * @param email the email of the user whose roles are to be retrieved
     * @return true if the user was deleted, false otherwise
     */
    boolean deleteByEmail(String email);
}
