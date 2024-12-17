package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTestDataServiceImplTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserTestDataServiceImpl userTestDataServiceImpl;

    @Test
    public void testCreateUserTestData() throws DataException {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        UserTestData mockUserTestData = new UserTestData("userId", "email@example.com", "Forename", "Surname");

        when(userService.createUser(any(UsersSpec.class))).thenReturn(mockUserTestData);

        UserTestData userTestData = userTestDataServiceImpl.createUserTestData(usersSpec);

        assertNotNull(userTestData);
        assertEquals("userId", userTestData.getUserId());
        assertEquals("email@example.com", userTestData.getEmail());
        assertEquals("Forename", userTestData.getForeName());
        assertEquals("Surname", userTestData.getSurName());
    }

    @Test
    public void testCreateUserTestDataWithOutPassword() {
        UsersSpec usersSpec = new UsersSpec();
        assertThrows(IllegalArgumentException.class, () -> userTestDataServiceImpl.createUserTestData(usersSpec));
    }

    @Test
    public void testDeleteUserTestData() throws DataException {
        doNothing().when(userService).deleteUser("userId");

        userTestDataServiceImpl.deleteUserTestData("userId");

        verify(userService, times(1)).deleteUser("userId");
    }

    @Test
    public void testDeleteUserTestDataThrowsException() throws DataException {
        doThrow(new DataException("User not found")).when(userService).deleteUser("userId");
        assertThrows(DataException.class, () -> userTestDataServiceImpl.deleteUserTestData("userId"));
    }

    @Test
    public void testUserExists() throws NoDataFoundException {
        when(userService.userExits("userId")).thenReturn(true);

        boolean userExists = userTestDataServiceImpl.userExists("userId");

        assertTrue(userExists);
        verify(userService, times(1)).userExits("userId");
    }

    @Test
    public void testUserDoesNotExist() throws NoDataFoundException {
        when(userService.userExits("userId")).thenReturn(false);

        boolean userExists = userTestDataServiceImpl.userExists("userId");

        assertFalse(userExists);
        verify(userService, times(1)).userExits("userId");
    }
}

