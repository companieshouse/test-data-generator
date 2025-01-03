package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
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
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
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
        UserData userData = userServiceImpl.create(userSpec);
        verify(userRepository).save(argThat(user -> {
            assertEquals(userSpec.getPassword(), user.getPassword(), "Password should match the one set in UsersSpec");
            return true;
        }));
        assertNotNull(userData.getUserId(), "User ID should not be null");
        assertNotNull(userData.getEmail(), "Email should not be null");
        assertTrue(userData.getForename().contains("Forename"), "Forename should contain Forename");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain Surname");
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
        UserData userData = userServiceImpl.create(userSpec);

        verify(userRepository).save(argThat(user -> {
            assertEquals(userSpec.getPassword(), user.getPassword(), "Password should match the one set in UserSpec");
            assertEquals(1, user.getRoles().size(), "User should have one role assigned");
            assertEquals(roleSpec.getId(), user.getRoles().getFirst(), "The assigned role should match the role ID");
            return true;
        }));

        assertNotNull(userData.getUserId(), "User ID should not be null");
        assertNotNull(userData.getEmail(), "Email should not be null");
        assertTrue(userData.getForename().contains("Forename"), "Forename should contain Forename");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain Surname");


    }

    @Test
    void testDeleteUser() {
        Users mockUser = new Users();
        mockUser.setId("userId");

        when(userRepository.findById("userId")).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(any(Users.class));

        boolean result = userServiceImpl.delete("userId");

        assertTrue(result, "User should be deleted successfully");
        verify(userRepository, times(1)).delete(mockUser);
    }


    @Test
    void testDeleteUserUserRepositoryThrowsException() {
        String userId = "userId";
        Users mockUser = new Users();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Database error")).when(userRepository).delete(mockUser);

        boolean result = userServiceImpl.delete(userId);

        assertFalse(result, "Delete should return false when an exception occurs");
    }

    @Test
    void testCreateUserWithEmptyRolesList() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        userSpec.setRoles(new ArrayList<>()); // Empty roles list
        when(randomService.getString(anyInt())).thenReturn("randomUserId");
        UserData userData = userServiceImpl.create(userSpec);

        assertNotNull(userData.getUserId(), "User ID should not be null");
        assertNotNull(userData.getEmail(), "Email should not be null");
        assertTrue(userData.getForename().contains("Forename"), "Forename should contain Forename");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain Surname");
    }

    @Test
    void testGetUserById() {
        String userId = "userId";
        List<String> roles = List.of("role1", "role2");
        Users user = new Users();
        user.setId(userId);
        user.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Users result = userServiceImpl.getUserById(userId);

        assertNotNull(result, "Roles list should not be null");
        assertEquals(roles.size(), result.getRoles().size(), "Roles list size should match");
        assertTrue(result.getRoles().containsAll(roles), "Roles list should contain all roles");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserIdUserByNotFound() {
        String userId = "userId";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Users result = userServiceImpl.getUserById(userId);

        assertNull(result, "User should be null when not found");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        String userId = "nonExistentUserId";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userServiceImpl.delete(userId);

        assertFalse(result, "Delete should return false when user does not exist");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).delete(any(Users.class));
    }
}
