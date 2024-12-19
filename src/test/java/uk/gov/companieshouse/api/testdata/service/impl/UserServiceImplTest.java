package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        UserTestData userTestData = userServiceImpl.create(usersSpec);
        verify(userRepository).save(argThat(user -> {
            assertEquals(usersSpec.getPassword(), user.getPassword(), "Password should match the one set in UsersSpec");
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
        UsersSpec usersSpec = new UsersSpec();
        usersSpec.setPassword("password");

        RolesSpec roleSpec = new RolesSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(Arrays.asList("permission1", "permission2"));
        usersSpec.setRoles(List.of(roleSpec));

        UserTestData userTestData = userServiceImpl.create(usersSpec);
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

        assertThrows(DataException.class, () -> userServiceImpl.delete("userId"));
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

        userServiceImpl.delete(userId);

        verify(roleRepository, times(1)).delete(mockRole1);
        verify(roleRepository, times(1)).delete(mockRole2);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUserThrowsException() {
        String userId = "userId";
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database error"));

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.delete(userId));

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

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.create(usersSpec));

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

        DataException exception = assertThrows(DataException.class, () -> userServiceImpl.create(usersSpec));

        assertEquals("Role does not exist", exception.getMessage());
    }
}
