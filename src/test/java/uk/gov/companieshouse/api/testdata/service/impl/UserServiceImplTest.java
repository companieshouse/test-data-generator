package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void testCreateUserWithoutRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        when(randomService.getString(anyInt())).thenReturn("randomUserId");
        UserTestData userTestData = userServiceImpl.create(userSpec);
        verify(userRepository).save(argThat(user -> {
            assertEquals(userSpec.getPassword(), user.getPassword(), "Password should match the one set in UsersSpec");
            return true;
        }));
        assertNotNull(userTestData.getUserId(), "User ID should not be null");
        assertNotNull(userTestData.getEmail(), "Email should not be null");
        assertTrue(userTestData.getForename().contains("Forename"), "Forename should contain Forename");
        assertTrue(userTestData.getSurname().contains("Surname"), "Surname should contain Surname");

        ArgumentCaptor<Roles> rolesCaptor = ArgumentCaptor.forClass(Roles.class);
        verify(roleRepository, times(0)).save(rolesCaptor.capture());
    }

    @Test
    void testCreateUserWithRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(Arrays.asList("permission1", "permission2"));
        userSpec.setRoles(List.of(roleSpec));

        when(randomService.getString(anyInt())).thenReturn("randomUserId");
        UserTestData userTestData = userServiceImpl.create(userSpec);
        ArgumentCaptor<Roles> rolesCaptor = ArgumentCaptor.forClass(Roles.class);
        verify(roleRepository).save(rolesCaptor.capture());

        Roles capturedRole = rolesCaptor.getValue();
        assertNotNull(capturedRole, "Captured role should not be null");
        assertTrue(capturedRole.getId().contains(roleSpec.getId()), "Role ID should match the one set in RolesSpec");
        assertEquals(roleSpec.getPermissions(), capturedRole.getPermissions(), "Permissions should match the ones set in RolesSpec");

        ArgumentCaptor<Users> usersCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(usersCaptor.capture());

        Users capturedUser = usersCaptor.getValue();
        assertNotNull(capturedUser.getRoles(), "User roles should not be null");
        assertEquals(1, capturedUser.getRoles().size(), "User should have one role assigned");
        assertEquals(capturedRole.getId(), capturedUser.getRoles().get(0), "The assigned role should match the captured role");

        assertNotNull(userTestData.getUserId(), "User ID should not be null");
        assertNotNull(userTestData.getEmail(), "Email should not be null");
        assertTrue(userTestData.getForename().contains("Forename"), "Forename should contain Forename");
        assertTrue(userTestData.getSurname().contains("Surname"), "Surname should contain Surname");
    }
    @Test
    void testUserExists() {
        when(userRepository.findById("userId")).thenReturn(Optional.of(new Users()));
        boolean userExists = userServiceImpl.userExists("userId");

        assertTrue(userExists, "User should exist");
        verify(userRepository, times(1)).findById("userId");
    }

    @Test
    void testUserDoesNotExist() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());

        boolean userExists = userServiceImpl.userExists("userId");

        assertFalse(userExists, "User should not exist");
        verify(userRepository, times(1)).findById("userId");
    }

    @Test
    void testDeleteUser() throws DataException {
        Users mockUser = new Users();
        mockUser.setId("userId");

        when(userRepository.findById("userId")).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(any(Users.class));

        assertTrue(userServiceImpl.userExists("userId"), "User should exist before deletion");

        userServiceImpl.delete("userId");

        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById("userId")).thenReturn(Optional.empty());

        assertFalse(userServiceImpl.userExists("userId"), "User should not exist before deletion");

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.delete("userId"));

        assertEquals("User id userId not found", exception.getMessage());
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

        userServiceImpl.delete(userId);

        verify(roleRepository, times(1)).delete(mockRole1);
        verify(roleRepository, times(1)).delete(mockRole2);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testCreateUserWithNullRoleId() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId(null); // Role ID is null
        roleSpec.setPermissions(Arrays.asList("permission1", "permission2"));
        userSpec.setRoles(List.of(roleSpec));

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.create(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }

    @Test
    void testCreateUserWithNullRolePermissions() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(null); // Role permissions are null
        userSpec.setRoles(List.of(roleSpec));

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.create(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }

    @Test
    void testDeleteUserRoleRepositoryThrowsException() {
        String userId = "userId";
        Users mockUser = new Users();
        mockUser.setId(userId);
        mockUser.setRoles(List.of("role1"));

        Roles mockRole = new Roles();
        mockRole.setId("role1");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById("role1")).thenReturn(Optional.of(mockRole));
        doThrow(new RuntimeException("Database error")).when(roleRepository).delete(mockRole);

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.delete(userId));
        assertEquals("Failed to delete user", exception.getMessage());
    }

    @Test
    void testDeleteUserUserRepositoryThrowsException() {
        String userId = "userId";
        Users mockUser = new Users();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Database error")).when(userRepository).delete(mockUser);

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.delete(userId));
        assertEquals("Failed to delete user", exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptyRolesList() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        userSpec.setRoles(new ArrayList<>()); // Empty roles list
        when(randomService.getString(anyInt())).thenReturn("randomUserId");
        UserTestData userTestData = userServiceImpl.create(userSpec);

        assertNotNull(userTestData.getUserId(), "User ID should not be null");
        assertNotNull(userTestData.getEmail(), "Email should not be null");
        assertTrue(userTestData.getForename().contains("Forename"), "Forename should contain Forename");
        assertTrue(userTestData.getSurname().contains("Surname"), "Surname should contain Surname");

        verify(roleRepository, times(0)).save(any(Roles.class));
    }

    @Test
    void testCreateUserWithEmptyPassword() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword(""); // Empty password

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.create(userSpec));

        assertEquals("Password is required to create a user", exception.getMessage());
    }
}
