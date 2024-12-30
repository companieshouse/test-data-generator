package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
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

        verify(userRepository).save(argThat(user -> {
            assertEquals(userSpec.getPassword(), user.getPassword(), "Password should match the one set in UserSpec");
            assertEquals(1, user.getRoles().size(), "User should have one role assigned");
            assertEquals(roleSpec.getId(), user.getRoles().getFirst(), "The assigned role should match the role ID");
            return true;
        }));

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
    }

    @Test
    void testGetRolesByUserId() {
        String userId = "userId";
        List<String> roles = List.of("role1", "role2");
        Users user = new Users();
        user.setId(userId);
        user.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<String> result = userServiceImpl.getRolesByUserId(userId);

        assertNotNull(result, "Roles list should not be null");
        assertEquals(roles.size(), result.size(), "Roles list size should match");
        assertTrue(result.containsAll(roles), "Roles list should contain all roles");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetRolesByUserIdUserNotFound() {
        String userId = "userId";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        List<String> result = userServiceImpl.getRolesByUserId(userId);

        assertNotNull(result, "Roles list should not be null");
        assertTrue(result.isEmpty(), "Roles list should be empty when user is not found");
        verify(userRepository, times(1)).findById(userId);
    }
}
