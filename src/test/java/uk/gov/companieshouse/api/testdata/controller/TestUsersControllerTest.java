package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.service.UsersTestDataService;

import java.util.Map;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class TestUsersControllerTest {
    @Mock
    private UsersTestDataService usersTestDataService;

    @InjectMocks
    private TestUsersController testUsersController;

    @Test
    void createUser() throws Exception {
        UsersSpec usersSpec = new UsersSpec();
        UserTestData userTestData = new UserTestData("test1234user", "test@test.com", "TestForename", "TestSurname");

        when(usersTestDataService.createUserTestData(any(UsersSpec.class))).thenReturn(userTestData);

        ResponseEntity<UserTestData> response = testUsersController.createUser(usersSpec);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userTestData, response.getBody());
    }

    @Test
    void createUserWithNullRequest() throws Exception {
        UserTestData userTestData = new UserTestData("test1234user", "test@test.com", "TestForename", "TestSurname");

        when(usersTestDataService.createUserTestData(any(UsersSpec.class))).thenReturn(userTestData);

        ResponseEntity<UserTestData> response = testUsersController.createUser(null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userTestData, response.getBody());
    }

    @Test
    void createUserThrowsDataException() throws Exception {
        UsersSpec usersSpec = new UsersSpec();
        when(usersTestDataService.createUserTestData(any(UsersSpec.class))).thenThrow(new DataException("Error"));

        assertThrows(DataException.class, () -> testUsersController.createUser(usersSpec));
    }

    @Test
    void deleteUser() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = testUsersController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(usersTestDataService).deleteUserTestData(userId);
    }

    @Test
    void deleteUserNotFound() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = testUsersController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found 12345", Objects.requireNonNull(response.getBody()).get("userId"));
    }

    @Test
    void deleteUserThrowsDataException() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(true);
        doThrow(new DataException("Error")).when(usersTestDataService).deleteUserTestData(userId);

        assertThrows(DataException.class, () -> testUsersController.deleteUser(userId));
    }

    @Test
    void deleteUserThrowsNoDataFoundException() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(true);
        doThrow(new DataException("Error")).when(usersTestDataService).deleteUserTestData(userId);

        assertThrows(DataException.class, () -> testUsersController.deleteUser(userId));
    }
}
