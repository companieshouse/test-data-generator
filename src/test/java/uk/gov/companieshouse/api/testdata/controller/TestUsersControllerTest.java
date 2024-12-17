package uk.gov.companieshouse.api.testdata.controller;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUsersControllerTest {
    @Mock
    private UsersTestDataService usersTestDataService;

    @InjectMocks
    private TestUsersController testUsersController;

    @Test
    public void createUser() throws Exception {
        UsersSpec usersSpec = new UsersSpec();
        UserTestData userTestData = new UserTestData("test1234user", "test@test.com", "TestForeName", "TestSurName");

        when(usersTestDataService.createUserTestData(any(UsersSpec.class))).thenReturn(userTestData);

        ResponseEntity<UserTestData> response = testUsersController.createUser(usersSpec);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userTestData, response.getBody());
    }

    @Test
    public void createUserWithNullRequest() throws Exception {
        UserTestData userTestData = new UserTestData("test1234user", "test@test.com", "TestForeName", "TestSurName");

        when(usersTestDataService.createUserTestData(any(UsersSpec.class))).thenReturn(userTestData);

        ResponseEntity<UserTestData> response = testUsersController.createUser(null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userTestData, response.getBody());
    }

    @Test
    public void createUserThrowsDataException() throws Exception {
        UsersSpec usersSpec = new UsersSpec();
        when(usersTestDataService.createUserTestData(any(UsersSpec.class))).thenThrow(new DataException("Error"));

        assertThrows(DataException.class, () -> testUsersController.createUser(usersSpec));
    }

    @Test
    public void deleteUser() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = testUsersController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(usersTestDataService).deleteUserTestData(userId);
    }

    @Test
    public void deleteUserNotFound() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = testUsersController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found 12345", Objects.requireNonNull(response.getBody()).get("userId"));
    }

    @Test
    public void deleteUserThrowsDataException() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(true);
        doThrow(new DataException("Error")).when(usersTestDataService).deleteUserTestData(userId);

        assertThrows(DataException.class, () -> testUsersController.deleteUser(userId));
    }

    @Test
    public void deleteUserThrowsNoDataFoundException() throws Exception {
        String userId = "12345";

        when(usersTestDataService.userExists(userId)).thenReturn(true);
        doThrow(new DataException("Error")).when(usersTestDataService).deleteUserTestData(userId);

        assertThrows(DataException.class, () -> testUsersController.deleteUser(userId));
    }
}
