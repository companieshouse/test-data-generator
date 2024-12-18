package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RolesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void testCreateUserWithoutRoles() throws DataException {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        Users mockUser = new Users();
        mockUser.setId("generated-user-id");

        when(userRepository.save(any(Users.class))).thenReturn(mockUser);

        UserTestData userTestData = userServiceImpl.createUser(usersSpec);

        assertNotNull(userTestData.getUserId(), "User ID should not be null");
        assertNotNull(userTestData.getEmail(), "Email should not be null");
        assertTrue(userTestData.getForeName().contains("Forename"), "Forename should contain Forename");
        assertTrue(userTestData.getSurName().contains("Surname"), "Surname should contain Surname");
    }

    @Test
    void testCreateUserWithRoles() throws DataException {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        RolesSpec roleSpec = new RolesSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(Arrays.asList("permission1", "permission2"));
        usersSpec.setRoles(List.of(roleSpec));

        Roles mockRole = new Roles();
        mockRole.setId("role-id-playwright-role" + System.currentTimeMillis());
        mockRole.setPermissions(roleSpec.getPermissions());

        Users mockUser = new Users();
        mockUser.setId("generated-user-id");

        when(roleRepository.save(any(Roles.class))).thenReturn(mockRole);
        when(userRepository.save(any(Users.class))).thenReturn(mockUser);

        UserTestData userTestData = userServiceImpl.createUser(usersSpec);

        assertNotNull(userTestData.getUserId(), "User ID should not be null");
        assertNotNull(userTestData.getEmail(), "Email should not be null");
        assertTrue(userTestData.getForeName().contains("Forename"), "Forename should contain Forename");
        assertTrue(userTestData.getSurName().contains("Surname"), "Surname should contain Surname");

        // Verify that the role repository was called
        verify(roleRepository, times(1)).save(any(Roles.class));
    }

    @Test
    void testUserExists() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(new Users()));

        boolean userExists = userServiceImpl.userExits("userId");

        assertTrue(userExists, "User should exist");
        verify(userRepository, times(1)).findById("userId");
    }

    @Test
    void testUserDoesNotExist() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());

        boolean userExists = userServiceImpl.userExits("userId");

        assertFalse(userExists, "User should not exist");
        verify(userRepository, times(1)).findById("userId");
    }

    @Test
    void testDeleteUser() throws DataException {
        Users mockUser = new Users();
        mockUser.setId("userId");

        when(userRepository.findById("userId")).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(any(Users.class));

        assertTrue(userServiceImpl.userExits("userId"), "User should exist before deletion");

        userServiceImpl.deleteUser("userId");

        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById("userId")).thenReturn(Optional.empty());

        assertFalse(userServiceImpl.userExits("userId"), "User should not exist before deletion");

        assertThrows(DataException.class, () -> userServiceImpl.deleteUser("userId"));
    }

    @Test
    void testDeleteUserWithRoles() throws DataException {
        String userId = "userId";
        Users mockUser = new Users();
        mockUser.setId(userId);
        mockUser.setRoles(List.of("role1", "role2"));

        Roles mockRole1 = new Roles();
        mockRole1.setId("role1");
        Roles mockRole2 = new Roles();
        mockRole2.setId("role2");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById("role1")).thenReturn(Optional.of(mockRole1));
        when(roleRepository.findById("role2")).thenReturn(Optional.of(mockRole2));
        doNothing().when(roleRepository).delete(any(Roles.class));
        doNothing().when(userRepository).delete(any(Users.class));

        userServiceImpl.deleteUser(userId);

        verify(roleRepository, times(1)).delete(mockRole1);
        verify(roleRepository, times(1)).delete(mockRole2);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUserThrowsException() {
        String userId = "userId";
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database error"));

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.deleteUser(userId));

        assertEquals("Failed to delete user", exception.getMessage());
    }

    @Test
    void testGenerateRandomString() throws Exception {
        Method method = UserServiceImpl.class.getDeclaredMethod("generateRandomString", int.class);
        method.setAccessible(true);
        String randomString = (String) method.invoke(userServiceImpl, 24);
        assertNotNull(randomString);
        assertEquals(24, randomString.length());
    }
    @Test
    void testCreateUserWithNullRoleId() {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        RolesSpec roleSpec = new RolesSpec();
        roleSpec.setId(null); // Role ID is null
        roleSpec.setPermissions(Arrays.asList("permission1", "permission2"));
        usersSpec.setRoles(List.of(roleSpec));

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.createUser(usersSpec));

        assertEquals("Role does not exist", exception.getMessage());
    }

    @Test
    void testCreateUserWithNullRolePermissions() {
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        RolesSpec roleSpec = new RolesSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(null); // Role permissions are null
        usersSpec.setRoles(List.of(roleSpec));

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.createUser(usersSpec));

        assertEquals("Role does not exist", exception.getMessage());
    }
}
