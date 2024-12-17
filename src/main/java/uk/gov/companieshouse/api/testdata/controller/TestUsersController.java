package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.service.UsersTestDataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestUsersController {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UsersTestDataService userTestDataService;

    @PostMapping("/users")
    public ResponseEntity<UserTestData> createUser(@Valid @RequestBody(required = false) UsersSpec request) throws DataException {
        Optional<UsersSpec> optionalRequest = Optional.ofNullable(request);
        UsersSpec spec = optionalRequest.orElse(new UsersSpec());

        UserTestData createdUser = userTestDataService.createUserTestData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put("user email", createdUser.getEmail());
        data.put("user id", createdUser.getUserId());
        LOG.info("New user created", data);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("userId") String userId) throws DataException, NoDataFoundException {
        if(!userTestDataService.userExists(userId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("userId", "User not found "+ userId);
            LOG.info(userId+ " User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        userTestDataService.deleteUserTestData(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("user id", userId);
        LOG.info("User deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}