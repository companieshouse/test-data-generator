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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.User;
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

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(userSpec.getPassword(), savedUser.getPassword(), "Password should match the one set in UserSpec");
        assertEquals("randomUserId", savedUser.getId(), "User ID should match the generated ID");
        assertTrue(savedUser.getEmail().contains("test-data-generated"), "Email should contain 'test-data-generated'");
        assertTrue(savedUser.getForename().contains("Forename"), "Forename should contain 'Forename'");
        assertTrue(savedUser.getSurname().contains("Surname"), "Surname should contain 'Surname'");

        assertEquals("randomUserId", userData.getId(), "User ID should match the generated ID");
        assertTrue(userData.getEmail().contains("test-data-generated"), "Email should contain 'test-data-generated'");
        assertTrue(userData.getForename().contains("Forename"), "Forename should contain 'Forename'");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain 'Surname'");
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
            assertEquals(roleSpec.getId(), user.getRoles().get(0), "The assigned role should match the role ID");
            assertEquals("randomUserId", user.getId(), "User ID should match the generated ID");
            assertTrue(user.getEmail().contains("test-data-generated"), "Email should contain 'test-data-generated'");
            assertTrue(user.getForename().contains("Forename"), "Forename should contain 'Forename'");
            assertTrue(user.getSurname().contains("Surname"), "Surname should contain 'Surname'");
            return true;
        }));

        assertEquals("randomUserId", userData.getId(), "User ID should match the generated ID");
        assertTrue(userData.getEmail().contains("test-data-generated"), "Email should contain 'test-data-generated'");
        assertTrue(userData.getForename().contains("Forename"), "Forename should contain 'Forename'");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain 'Surname'");
    }


    @Test
    void testCreateUserWithEmptyRolesList() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        userSpec.setRoles(new ArrayList<>());
        when(randomService.getString(24)).thenReturn("randomUserId");
        UserData userData = userServiceImpl.create(userSpec);

        assertEquals("randomUserId", userData.getId(), "User ID should match the generated ID");
        assertTrue(userData.getEmail().contains("test-data-generated"), "Email should contain 'test-data-generated'");
        assertTrue(userData.getForename().contains("Forename"), "Forename should contain 'Forename'");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain 'Surname'");
    }

    @Test
    void testGetUserById() {
        String userId = "userId";
        List<String> roles = List.of("role1", "role2");
        User user = new User();
        user.setId(userId);
        user.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userServiceImpl.getUserById(userId);

        assertTrue(result.isPresent(), "User should be present");
        assertEquals(roles.size(), result.get().getRoles().size(), "Role list size should match");
        assertTrue(result.get().getRoles().containsAll(roles), "Role list should contain all roles");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserIdUserByNotFound() {
        String userId = "userId";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userServiceImpl.getUserById(userId);

        assertTrue(result.isEmpty(), "User should be empty when not found");
    }

    @Test
    void testDeleteUser() {
        User mockUser = new User();
        mockUser.setId("userId");

        when(userRepository.findById("userId")).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(any(User.class));

        boolean result = userServiceImpl.delete("userId");

        assertTrue(result, "User should be deleted successfully");
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUserUserRepositoryThrowsException() {
        String userId = "userId";
        User mockUser = new User();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doThrow(new RuntimeException("Database error")).when(userRepository).delete(mockUser);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userServiceImpl.delete(userId));

        assertEquals("Database error", exception.getMessage(), "Exception message should match");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUserNotFound() {
        String userId = "nonExistentUserId";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        boolean result = userServiceImpl.delete(userId);

        assertFalse(result, "Delete should return false when user does not exist");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).delete(any(User.class));
    }
}