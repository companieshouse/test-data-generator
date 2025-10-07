package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserRoles;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Instant DATE_NOW = Instant.now();

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminPermissionsRepository adminPermissionsRepository;

    @Mock
    private RandomService randomService;

    @Spy
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateUserWithoutRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("hello@hello.com");
        userSpec.setPassword("password");
        var generatedUserId = "randomised";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertCommonUserAssertions(userSpec, savedUser, userData, generatedUserId);
        assertNull(savedUser.getRoles(), "User should have no roles assigned");
    }

    @Test
    void testCreateUserWithoutEmailsAndRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        var generatedUserId = "randomised";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertCommonUserAssertions(userSpec, savedUser, userData, generatedUserId);
        assertNull(savedUser.getRoles(), "User should have no roles assigned");
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("invalid-email");
        userSpec.setPassword("password");

        Set<ConstraintViolation<UserSpec>> violations = validator.validate(userSpec);
        assertFalse(violations.isEmpty());
        assertEquals("email is not a valid email address",
                violations.iterator().next().getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUserWithValidRolesAndAdminPermissions() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("valid@hello.com");
        userSpec.setPassword("password");
        userSpec.setRoles(List.of("CHS_ADMIN_SUPERVISOR", "CHS_ADMIN_SUPPORT_MEMBER"));

        String generatedUserId = "validid";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        AdminPermissions supervisorPerm = new AdminPermissions();
        supervisorPerm.setEntraGroupId("entra-group-id-1");
        AdminPermissions supportPerm = new AdminPermissions();
        supportPerm.setEntraGroupId("entra-group-id-2");

        when(adminPermissionsRepository.findByGroupName(UserRoles.CHS_ADMIN_SUPERVISOR.getGroupName()))
                .thenReturn(supervisorPerm);
        when(adminPermissionsRepository.findByGroupName(UserRoles.CHS_ADMIN_SUPPORT_MEMBER.getGroupName()))
                .thenReturn(supportPerm);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        List<String> expectedEntraGroupIds = List.of("entra-group-id-1", "entra-group-id-2");

        assertEquals(expectedEntraGroupIds.size(), savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().containsAll(expectedEntraGroupIds));
        assertEquals(generatedUserId, userData.getId());
    }

    @Test
    void testCreateUserWithInvalidRoleNameThrowsException() {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("invalidrole@hello.com");
        userSpec.setPassword("password");
        userSpec.setRoles(List.of("NOT_A_ROLE"));

        String generatedUserId = "invalidid";
        when(randomService.getString(23)).thenReturn(generatedUserId);

        DataException ex = assertThrows(DataException.class, () -> userServiceImpl.create(userSpec));
        assertTrue(ex.getMessage().contains("Invalid role name: NOT_A_ROLE"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUserWithEmptyRolesList() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        userSpec.setRoles(new ArrayList<>());

        String generatedUserId = "randomised";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertCommonUserAssertions(userSpec, savedUser, userData, generatedUserId);
        assertNull(savedUser.getRoles(), "User should have no roles assigned");
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
        assertTrue(result.get().getRoles().containsAll(roles),
                "Role list should contain all roles");
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

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> userServiceImpl.delete(userId));

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

    private void assertCommonUserAssertions(
            UserSpec userSpec, User savedUser, UserData userData, String generatedUserId) {
        assertEquals(
                userSpec.getPassword(),
                savedUser.getPassword(),
                "Password should match the one set in UserSpec");
        assertEquals(generatedUserId, savedUser.getId(), "User ID should match the generated ID");
        String expectedEmail = userSpec.getEmail() != null ? userSpec.getEmail() :
                "test-data-generated" + generatedUserId + "@chtesttdg.mailosaur.net";
        assertEquals(expectedEmail, savedUser.getEmail(), "Email should match");

        assertEquals(
                "Forename-" + generatedUserId,
                savedUser.getForename(),
                "Forename should match the generated forename");
        assertEquals(
                "Surname-" + generatedUserId,
                savedUser.getSurname(),
                "Surname should match the generated surname");
        assertEquals("GB_en", savedUser.getLocale(), "Locale should be 'GB_en'");
        assertEquals(
                true, savedUser.getDirectLoginPrivilege(), "Direct login privilege should be true");
        assertEquals(DATE_NOW, savedUser.getCreated(),
                "Created date should be set to today's date");

        assertEquals(generatedUserId, userData.getId(), "User ID should match the generated ID");
        assertTrue(
                userData.getEmail().contains(userSpec.getEmail() != null ? userSpec.getEmail()
                        : "test-data-generated"),
                "Email in UserData should contain the correct prefix");
        assertTrue(userData.getForename().contains("Forename"),
                "Forename should contain 'Forename'");
        assertTrue(userData.getSurname().contains("Surname"), "Surname should contain 'Surname'");
        assertTrue(savedUser.getTestData(), "test data as true by default");
    }

    @Test
    void testUpdateUserWithOneLogin() {
        String userId = "userId";
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userServiceImpl.updateUserWithOneLogin(userId);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals(userId, updatedUser.getOneLoginUserId(), "OneLoginUserId should be updated");
    }

    @Test
    void testUpdateUserWithOneLoginUserNotFound() {
        String userId = "nonExistentUserId";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userServiceImpl.updateUserWithOneLogin(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserWithIsAdminTrue() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("admin@hello.com");
        userSpec.setPassword("password");
        userSpec.setIsAdmin(true);

        String generatedUserId = "adminid";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertTrue(savedUser.getAdminUser(), "User should be marked as admin when isAdmin is true");
    }

    @Test
    void testCreateUserWithIsAdminFalse() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("user@hello.com");
        userSpec.setPassword("password");
        userSpec.setIsAdmin(false);

        String generatedUserId = "userid";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertFalse(savedUser.getAdminUser(), "User should not be marked as admin when isAdmin is false");
    }

    @Test
    void testCreateUserWithIsAdminNull() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("nulladmin@hello.com");
        userSpec.setPassword("password");
        userSpec.setIsAdmin(null);

        String generatedUserId = "nulladminid";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertFalse(savedUser.getAdminUser(), "User should not be marked as admin when isAdmin is null");
    }
}
