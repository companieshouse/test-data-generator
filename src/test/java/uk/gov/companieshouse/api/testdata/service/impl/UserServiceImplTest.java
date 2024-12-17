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
}
