package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.UserService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String USER_ID = "userId";

    @Mock
    private UserService userService;

    @Mock
    private CompanyAuthAllowListService companyAuthAllowListService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setPassword("password");
        UserResponse user = new UserResponse("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(request)).thenReturn(user);
        ResponseEntity<UserResponse> response = userController.createUser(request);

        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createUserException() throws Exception {
        UserRequest request = new UserRequest();
        request.setPassword("password");
        Throwable exception = new DataException("Error message");

        when(userService.create(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                userController.createUser(request));
        assertEquals(exception, thrown);
    }

    @Test
    void createUserWithoutPasswordThrowsDataException() {
        UserRequest request = new UserRequest();
        DataException thrown = assertThrows(DataException.class, () ->
                userController.createUser(request));
        assertEquals("Password is required to create a user", thrown.getMessage());
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("test@example.com");
        when(userService.getUserById(USER_ID)).thenReturn(Optional.of(user));
        when(userService.delete(USER_ID)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId("test@example.com")).thenReturn("auth-id");

        ResponseEntity<Map<String, Object>> response = userController.deleteUser(USER_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).delete(USER_ID);
        verify(companyAuthAllowListService).delete("auth-id");
    }

    @Test
    void deleteUserException() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail("test@example.com");
        RuntimeException exception = new RuntimeException("Error message");
        when(userService.getUserById(USER_ID)).thenReturn(Optional.of(user));
        when(userService.delete(USER_ID)).thenThrow(exception);

        RuntimeException thrown =
                assertThrows(RuntimeException.class, () -> userController.deleteUser(USER_ID));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteUserNotFound() {
        when(userService.getUserById(USER_ID)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = userController.deleteUser(USER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(USER_ID, Objects.requireNonNull(response.getBody()).get("user id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
    }

    @Test
    void deleteUserByEmailSuccess() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(userService.deleteByEmail(email)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = userController.deleteUserByEmail(email);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteByEmail(email);
    }

    @Test
    void deleteUserByEmailNotFound() {
        String email = "test@example.com";
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = userController.deleteUserByEmail(email);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(email, body.get("email"));
        assertEquals(HttpStatus.NOT_FOUND, body.get("status"));
    }

    @Test
    void deleteUserByEmailException() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        RuntimeException exception = new RuntimeException("Error deleting user");
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(userService.deleteByEmail(email)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> userController.deleteUserByEmail(email));
        assertEquals(exception, thrown);
    }
}
