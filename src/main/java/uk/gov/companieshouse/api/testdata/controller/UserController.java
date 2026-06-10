package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyAuthAllowListRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";
    private static final String USER_NOT_FOUND = "User not found";

    private final UserService userService;
    private final CompanyAuthAllowListService companyAuthAllowListService;

    public UserController(
            UserService userService,
            CompanyAuthAllowListService companyAuthAllowListService) {
        this.userService = userService;
        this.companyAuthAllowListService = companyAuthAllowListService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request)
            throws DataException {
        final String password = request.getPassword();
        if (password == null || password.isEmpty()) {
            throw new DataException("Password is required to create a user");
        }

        var createdUser = userService.create(request);
        if (Boolean.TRUE.equals(request.getIsCompanyAuthAllowList())) {
            var companyAuthAllowListSpec = new CompanyAuthAllowListRequest();
            companyAuthAllowListSpec.setEmailAddress(createdUser.getEmail());
            companyAuthAllowListService.create(companyAuthAllowListSpec);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user email", createdUser.getEmail());
        data.put("user id", createdUser.getId());
        LOG.info("New user created", data);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("userId") String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("user id", userId);

        User user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info(USER_NOT_FOUND, response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            var allowListId = companyAuthAllowListService.getAuthId(user.getEmail());
            if (allowListId != null) {
                companyAuthAllowListService.delete(allowListId);
            }
        }

        boolean deleteUser = userService.delete(userId);
        if (deleteUser) {
            LOG.info("User deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        response.put(STATUS, HttpStatus.NOT_FOUND);
        LOG.info(USER_NOT_FOUND, response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/user", params = "email")
    public ResponseEntity<Map<String, Object>> deleteUserByEmail(
            @RequestParam("email") String email) {

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);

        User user = userService.getUserByEmail(email).orElse(null);
        if (user == null) {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info(USER_NOT_FOUND, response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            var allowListId = companyAuthAllowListService.getAuthId(user.getEmail());
            if (allowListId != null) {
                companyAuthAllowListService.delete(allowListId);
            }
        }

        boolean deleted = userService.deleteByEmail(email);
        if (deleted) {
            LOG.info("User deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        response.put(STATUS, HttpStatus.NOT_FOUND);
        LOG.info(USER_NOT_FOUND, response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
