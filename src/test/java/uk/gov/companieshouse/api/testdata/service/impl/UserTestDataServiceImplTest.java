package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTestDataServiceImplTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserTestDataServiceImpl userTestDataServiceImpl;

    @Test
    void testCreateUserTestData() throws DataException {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        UserTestData mockUserTestData = new UserTestData("userId", "email@example.com", "Forename", "Surname");

        when(userService.createUser(any(UsersSpec.class))).thenReturn(mockUserTestData);

        UserTestData userTestData = userTestDataServiceImpl.createUserTestData(usersSpec);

        assertNotNull(userTestData);
        assertEquals("userId", userTestData.getUserId());
        assertEquals("email@example.com", userTestData.getEmail());
        assertEquals("Forename", userTestData.getForename());
        assertEquals("Surname", userTestData.getSurname());
    }

    @Test
    void testCreateUserTestDataWithoutPassword() {
        UsersSpec usersSpec = new UsersSpec();
        assertThrows(IllegalArgumentException.class, () -> userTestDataServiceImpl.createUserTestData(usersSpec));
    }

    @Test
    void testDeleteUserTestData() throws DataException {
        doNothing().when(userService).deleteUser("userId");

        userTestDataServiceImpl.deleteUserTestData("userId");

        verify(userService, times(1)).deleteUser("userId");
    }

    @Test
    void testDeleteUserTestDataThrowsException() throws DataException {
        doThrow(new DataException("User not found")).when(userService).deleteUser("userId");
        assertThrows(DataException.class, () -> userTestDataServiceImpl.deleteUserTestData("userId"));
    }

    @Test
    void testUserExists() {
        when(userService.userExists("userId")).thenReturn(true);

        boolean userExists = userTestDataServiceImpl.userExists("userId");

        assertTrue(userExists);
        verify(userService, times(1)).userExists("userId");
    }

    @Test
    void testUserDoesNotExist() {
        when(userService.userExists("userId")).thenReturn(false);

        boolean userExists = userTestDataServiceImpl.userExists("userId");

        assertFalse(userExists);
        verify(userService, times(1)).userExists("userId");
    }

    @Test
    void testCreateUserTestDataThrowsException() throws DataException {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        when(userService.createUser(any(UsersSpec.class))).thenThrow(new RuntimeException("Service exception"));

        DataException exception = assertThrows(DataException.class, () -> userTestDataServiceImpl.createUserTestData(usersSpec));

        assertEquals("Failed to create user test data", exception.getMessage());
        verify(userService, times(1)).createUser(any(UsersSpec.class));
    }
}

