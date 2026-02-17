package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityVerificationSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserRoles;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Instant DATE_NOW = Instant.now();
    private static final String TEST_USER_ID = "randomised-user-id";
    private static final String TEST_IDENTITY_ID = "4e93b3e8-4652-4484-af39-708f80a011ff";
    private static final String TEST_UVID = "XT12322223";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminPermissionsRepository adminPermissionsRepository;

    @Mock
    private IdentityRepository identityRepository;

    @Mock
    private UvidRepository uvidRepository;

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

        when(randomService.getString(23)).thenReturn(TEST_USER_ID);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertCommonUserAssertions(userSpec, savedUser, userData, TEST_USER_ID);
        assertNull(savedUser.getRoles(), "User should have no roles assigned");
    }

    @Test
    void testCreateUserWithoutEmailsAndRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        when(randomService.getString(23)).thenReturn(TEST_USER_ID);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertCommonUserAssertions(userSpec, savedUser, userData, TEST_USER_ID);
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

        when(randomService.getString(23)).thenReturn(TEST_USER_ID);
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
        assertEquals(TEST_USER_ID, userData.getId());
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
    void createUser_withAllFieldsAndRoles() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("test@domain.com");
        spec.setPassword("pass");
        spec.setIsAdmin(true);
        spec.setRoles(List.of("CHS_ADMIN_SUPERVISOR"));

        when(randomService.getString(23)).thenReturn("RANDOMID");
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        AdminPermissions adminPermissions = new AdminPermissions();
        adminPermissions.setEntraGroupId("entra-group-id");
        when(adminPermissionsRepository.findByGroupName("chs admin supervisor")).thenReturn(adminPermissions);

        UserData result = userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals("randomid", saved.getId());
        assertEquals("test@domain.com", saved.getEmail());
        assertEquals("Forename-randomid", saved.getForename());
        assertEquals("Surname-randomid", saved.getSurname());
        assertEquals("GB_en", saved.getLocale());
        assertEquals("pass", saved.getPassword());
        assertTrue(saved.getDirectLoginPrivilege());
        assertEquals(DATE_NOW, saved.getCreated());
        assertTrue(saved.getAdminUser());
        assertTrue(saved.getTestData());
        assertEquals(List.of("entra-group-id"), saved.getRoles());

        assertEquals(saved.getId(), result.getId());
        assertEquals(saved.getEmail(), result.getEmail());
        assertEquals(saved.getForename(), result.getForename());
        assertEquals(saved.getSurname(), result.getSurname());
    }

    @Test
    void createUser_withoutEmail_assignsGeneratedEmail() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setPassword("pass");
        when(randomService.getString(23)).thenReturn("RANDOMID");

        userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals("test-data-generatedrandomid@chtesttdg.mailosaur.net", saved.getEmail());
        assertEquals("randomid", saved.getId());
        assertEquals("pass", saved.getPassword());
    }

    @Test
    void createUser_withNullRoles_doesNotSetRoles() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("test@domain.com");
        spec.setPassword("pass");
        spec.setRoles(null);
        when(randomService.getString(23)).thenReturn("RANDOMID");

        userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertNull(saved.getRoles());
    }

    @Test
    void createUser_withEmptyRoles_doesNotSetRoles() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("test@domain.com");
        spec.setPassword("pass");
        spec.setRoles(List.of());
        when(randomService.getString(23)).thenReturn("RANDOMID");

        userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertNull(saved.getRoles());
    }

    @Test
    void createUser_withIsAdminFalse_setsAdminUserFalse() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("test@domain.com");
        spec.setPassword("pass");
        spec.setIsAdmin(false);
        when(randomService.getString(23)).thenReturn("RANDOMID");

        userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertFalse(saved.getAdminUser());
    }

    @Test
    void createUser_withIsAdminNull_setsAdminUserFalse() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("test@domain.com");
        spec.setPassword("pass");
        spec.setIsAdmin(null);
        when(randomService.getString(23)).thenReturn("RANDOMID");

        userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertFalse(saved.getAdminUser());
    }

    @Test
    void testProcessRolesWithValidRolesAndEntraGroupIds() throws DataException {
        List<String> roleNames = List.of("CHS_ADMIN_SUPERVISOR", "CHS_ADMIN_SUPPORT_MEMBER");

        AdminPermissions supervisorPerm = new AdminPermissions();
        supervisorPerm.setEntraGroupId("entra-group-id-1");
        supervisorPerm.setGroupName("chs admin supervisor");

        AdminPermissions supportPerm = new AdminPermissions();
        supportPerm.setEntraGroupId("entra-group-id-2");
        supportPerm.setGroupName("chs admin support member");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(supervisorPerm);
        when(adminPermissionsRepository.findByGroupName("chs admin support member"))
                .thenReturn(supportPerm);

        List<String> result = userServiceImpl.processRoles(roleNames);

        assertEquals(2, result.size());
        assertTrue(result.contains("entra-group-id-1"));
        assertTrue(result.contains("entra-group-id-2"));

        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin supervisor");
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin support member");
    }

    @Test
    void testProcessRolesWithEmptyRolesList() throws DataException {
        List<String> roleNames = new ArrayList<>();
        List<String> result = userServiceImpl.processRoles(roleNames);

        assertTrue(result.isEmpty());
        verify(adminPermissionsRepository, never()).findByGroupName(anyString());
    }

    @Test
    void testProcessRolesWithNullAdminPermissionsThrowsException() {
        List<String> roleNames = List.of("CHS_ADMIN_SUPERVISOR");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(null);

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.processRoles(roleNames));

        assertTrue(ex.getMessage().contains("No admin permissions found for groupName: chs admin supervisor"));
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin supervisor");
    }

    @Test
    void testProcessRolesWithNullEntraGroupIdThrowsException() {
        List<String> roleNames = List.of("CHS_ADMIN_SUPERVISOR");

        AdminPermissions adminPermissions = new AdminPermissions();
        adminPermissions.setEntraGroupId(null);
        adminPermissions.setGroupName("chs admin supervisor");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(adminPermissions);

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.processRoles(roleNames));

        assertTrue(ex.getMessage().contains("No entra_group_id found for group: chs admin supervisor"));
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin supervisor");
    }

    @Test
    void testProcessRolesWithEmptyEntraGroupIdThrowsException() {
        List<String> roleNames = List.of("CHS_ADMIN_SUPERVISOR");

        AdminPermissions adminPermissions = new AdminPermissions();
        adminPermissions.setEntraGroupId("");
        adminPermissions.setGroupName("chs admin supervisor");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(adminPermissions);

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.processRoles(roleNames));

        assertTrue(ex.getMessage().contains("No entra_group_id found for group: chs admin supervisor"));
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin supervisor");
    }

    @Test
    void testProcessRolesWithMultipleRolesWhereOneFails() {
        List<String> roleNames = List.of("CHS_ADMIN_SUPERVISOR", "CHS_ADMIN_SUPPORT_MEMBER");

        AdminPermissions supervisorPerm = new AdminPermissions();
        supervisorPerm.setEntraGroupId("entra-group-id-1");
        supervisorPerm.setGroupName("chs admin supervisor");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(supervisorPerm);
        when(adminPermissionsRepository.findByGroupName("chs admin support member"))
                .thenReturn(null);

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.processRoles(roleNames));

        assertTrue(ex.getMessage().contains("No admin permissions found for groupName: chs admin support member"));
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin supervisor");
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin support member");
    }

    @Test
    void testGetUserRoleWithValidRoleName() throws DataException {
        String validRoleName = "CHS_ADMIN_SUPERVISOR";
        UserRoles result = userServiceImpl.getUserRole(validRoleName);

        assertEquals(UserRoles.CHS_ADMIN_SUPERVISOR, result);
        assertEquals("chs admin supervisor", result.getGroupName());
    }

    @Test
    void testGetUserRoleWithInvalidRoleNameThrowsException() {
        String invalidRoleName = "INVALID_ROLE_NAME";
        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.getUserRole(invalidRoleName));

        assertTrue(ex.getMessage().contains("Invalid role name: INVALID_ROLE_NAME"));
    }

    @Test
    void testGetUserRoleWithNullRoleNameThrowsException() {
        String nullRoleName = null;

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.getUserRole(nullRoleName));

        assertTrue(ex.getMessage().contains("Invalid role name: null"));
    }

    @Test
    void testGetUserRoleWithEmptyRoleNameThrowsException() {
        String emptyRoleName = "";

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.getUserRole(emptyRoleName));

        assertTrue(ex.getMessage().contains("Invalid role name: "));
    }

    @Test
    void testGetUserRoleWithCaseSensitiveRoleName() {
        String lowercaseRoleName = "chs_admin_supervisor";

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.getUserRole(lowercaseRoleName));

        assertTrue(ex.getMessage().contains("Invalid role name: chs_admin_supervisor"));
    }

    @Test
    void testCreateUserWithMultipleRolesAndEntraGroupIds() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("multirole@hello.com");
        userSpec.setPassword("password");
        userSpec.setRoles(List.of("CHS_ADMIN_SUPERVISOR", "CHS_ADMIN_SUPPORT_MEMBER", "CHS_ADMIN_BADOS_USER"));

        String generatedUserId = "multiroleid";
        when(randomService.getString(23)).thenReturn(generatedUserId);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        AdminPermissions supervisorPerm = new AdminPermissions();
        supervisorPerm.setEntraGroupId("b7c48d82-444b-414f-870b-4da96c2d075e");
        AdminPermissions supportPerm = new AdminPermissions();
        supportPerm.setEntraGroupId("1cf86422-f0e6-4463-9aac-37c210cdc7f6");
        AdminPermissions badosPerm = new AdminPermissions();
        badosPerm.setEntraGroupId("6b19906c-b9a6-409c-8017-d35b9222856b");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(supervisorPerm);
        when(adminPermissionsRepository.findByGroupName("chs admin support member"))
                .thenReturn(supportPerm);
        when(adminPermissionsRepository.findByGroupName("chs admin bados user"))
                .thenReturn(badosPerm);

        UserData userData = userServiceImpl.create(userSpec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        List<String> expectedEntraGroupIds = List.of(
                "b7c48d82-444b-414f-870b-4da96c2d075e",
                "1cf86422-f0e6-4463-9aac-37c210cdc7f6",
                "6b19906c-b9a6-409c-8017-d35b9222856b"
        );

        assertEquals(expectedEntraGroupIds.size(), savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().containsAll(expectedEntraGroupIds));
        assertEquals(generatedUserId, userData.getId());

        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin supervisor");
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin support member");
        verify(adminPermissionsRepository, times(1)).findByGroupName("chs admin bados user");
    }

    @Test
    void testCreateUserWithRoleThatHasNullEntraGroupIdThrowsException() {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("nullentra@hello.com");
        userSpec.setPassword("password");
        userSpec.setRoles(List.of("CHS_ADMIN_SUPERVISOR"));

        String generatedUserId = "nullentraid";
        when(randomService.getString(23)).thenReturn(generatedUserId);

        AdminPermissions adminPermissions = new AdminPermissions();
        adminPermissions.setEntraGroupId(null);
        adminPermissions.setGroupName("chs admin supervisor");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(adminPermissions);

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.create(userSpec));

        assertTrue(ex.getMessage().contains("No entra_group_id found for group: chs admin supervisor"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUserWithRoleThatHasEmptyEntraGroupIdThrowsException() {
        UserSpec userSpec = new UserSpec();
        userSpec.setEmail("emptyentra@hello.com");
        userSpec.setPassword("password");
        userSpec.setRoles(List.of("CHS_ADMIN_SUPERVISOR"));

        String generatedUserId = "emptyentraid";
        when(randomService.getString(23)).thenReturn(generatedUserId);

        AdminPermissions adminPermissions = new AdminPermissions();
        adminPermissions.setEntraGroupId("");
        adminPermissions.setGroupName("chs admin supervisor");

        when(adminPermissionsRepository.findByGroupName("chs admin supervisor"))
                .thenReturn(adminPermissions);

        DataException ex = assertThrows(DataException.class,
                () -> userServiceImpl.create(userSpec));

        assertTrue(ex.getMessage().contains("No entra_group_id found for group: chs admin supervisor"));
        verify(userRepository, never()).save(any());
    }

    private void assertCommonUserAssertions(
            UserSpec userSpec, User savedUser, UserData userData, String generatedUserId) {
        assertEquals(
                userSpec.getPassword(),
                savedUser.getPassword(),
                "Password should match the one set in UserSpec");
        assertEquals(generatedUserId, savedUser.getId(), "User ID should match the generated ID");
        String expectedEmail = userSpec.getEmail() != null ?
                userSpec.getEmail() :
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
        when(userRepository.findById("nonExistentUserId")).thenReturn(Optional.empty());

        boolean result = userServiceImpl.delete("nonExistentUserId");

        assertFalse(result, "Delete should return false when user does not exist");
        verify(userRepository, never()).delete(any(User.class));
        verify(identityRepository, never()).findByUserId(anyString());
        verify(uvidRepository, never()).deleteByIdentityId(anyString());
    }

    @Test
    void testDeleteUser_DeletesUserIdentityAndUvid() {
        User mockUser = new User();
        mockUser.setId(TEST_USER_ID);

        Identity mockIdentity = new Identity();
        mockIdentity.setId(TEST_IDENTITY_ID);
        mockIdentity.setUserId(TEST_USER_ID);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(mockIdentity));

        boolean result = userServiceImpl.delete(TEST_USER_ID);

        assertTrue(result, "Delete should return true");

        verify(uvidRepository, times(1)).deleteByIdentityId(TEST_IDENTITY_ID);
        verify(identityRepository, times(1)).delete(mockIdentity);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUser_WhenIdentityNotFound_DeletesOnlyUser() {
        User mockUser = new User();
        mockUser.setId(TEST_USER_ID);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        boolean result = userServiceImpl.delete(TEST_USER_ID);

        assertTrue(result, "Delete should return true");
        verify(uvidRepository, never()).deleteByIdentityId(anyString());
        verify(identityRepository, never()).delete(any(Identity.class));
        verify(userRepository, times(1)).delete(mockUser);
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

    @Test
    void create_WithIdentityVerification_CreatesIdentityAndUvid() throws Exception {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource("TEST_SOURCE");
        spec.setIdentityVerification(List.of(ivSpec));

        when(randomService.getString(23)).thenReturn(TEST_USER_ID);

        when(identityRepository.save(any(Identity.class))).thenAnswer(invocation -> {
            Identity saved = invocation.getArgument(0);
            saved.setId(TEST_IDENTITY_ID);
            return saved;
        });

        when(randomService.getString(10)).thenReturn(TEST_UVID);

        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(spec);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        assertEquals(TEST_USER_ID, userCaptor.getValue().getId());

        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);
        verify(identityRepository, times(1)).save(identityCaptor.capture());
        Identity savedIdentity = identityCaptor.getValue();
        assertEquals(TEST_IDENTITY_ID, savedIdentity.getId());
        assertEquals(TEST_USER_ID, savedIdentity.getUserId());
        assertEquals("user@example.com", savedIdentity.getEmail());
        assertEquals("TEST_SOURCE", savedIdentity.getVerificationSource());
        assertEquals("VALID", savedIdentity.getStatus());

        ArgumentCaptor<Uvid> uvidCaptor = ArgumentCaptor.forClass(Uvid.class);
        verify(uvidRepository, times(1)).save(uvidCaptor.capture());
        Uvid savedUvid = uvidCaptor.getValue();
        assertEquals(TEST_UVID, savedUvid.getValue());
        assertEquals(TEST_IDENTITY_ID, savedUvid.getIdentityId());
        assertEquals("PERMANENT", savedUvid.getType());
    }

    @Test
    void create_WithoutIdentityVerification_DoesNotCreateIdentityOrUvid() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        spec.setIdentityVerification(null);

        when(randomService.getString(23)).thenReturn("RANDOMID");
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(spec);

        verify(userRepository, times(1)).save(any(User.class));
        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void create_WithEmptyIdentityVerification_DoesNotCreateIdentityOrUvid() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        spec.setIdentityVerification(new ArrayList<>());

        when(randomService.getString(23)).thenReturn("RANDOMID");
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(spec);

        verify(userRepository, times(1)).save(any(User.class));
        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void create_WithNullIvSpecItem_SkipsAndDoesNotCreate() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        spec.setIdentityVerification(java.util.Arrays.asList((IdentityVerificationSpec) null));

        when(randomService.getString(23)).thenReturn("RANDOMID");
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(spec);

        verify(userRepository, times(1)).save(any(User.class));
        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void create_WithNullVerificationSource_SkipsAndDoesNotCreate() throws DataException {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource(null);
        spec.setIdentityVerification(List.of(ivSpec));

        when(randomService.getString(23)).thenReturn("RANDOMID");
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(spec);

        verify(userRepository, times(1)).save(any(User.class));
        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void testGenerateUvid_returnsCorrectPattern_noReflection() throws Exception {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource("TEST_SOURCE");
        spec.setIdentityVerification(List.of(ivSpec));

        when(randomService.getString(23)).thenReturn(TEST_USER_ID);
        when(randomService.getString(10)).thenReturn(TEST_UVID);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        when(identityRepository.save(any(Identity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userServiceImpl.create(spec);

        ArgumentCaptor<Uvid> uvidCaptor = ArgumentCaptor.forClass(Uvid.class);
        verify(uvidRepository, times(1)).save(uvidCaptor.capture());
        Uvid savedUvid = uvidCaptor.getValue();

        assertNotNull(savedUvid.getValue());
        Pattern uvidPattern = Pattern.compile("^X[A-Z]\\d{3}22223$");
        assertTrue(uvidPattern.matcher(savedUvid.getValue()).matches(),
                "UVID " + savedUvid.getValue() + " does not match pattern ^X[A-Z]\\d{3}22223$");
    }

    @Test
    void testGenerateIdentityId_returnsValidUUID_noReflection() throws Exception {
        UserSpec spec = new UserSpec();
        spec.setEmail("user@example.com");
        spec.setPassword("password");
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource("TEST_SOURCE");
        spec.setIdentityVerification(List.of(ivSpec));

        when(randomService.getString(23)).thenReturn(TEST_USER_ID);
        when(randomService.getString(10)).thenReturn(TEST_UVID);
        when(userServiceImpl.getDateNow()).thenReturn(DATE_NOW);

        userServiceImpl.create(spec);

        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);
        verify(identityRepository, times(1)).save(identityCaptor.capture());
        Identity savedIdentity = identityCaptor.getValue();

        assertNotNull(savedIdentity.getId());
        try {
            UUID.fromString(savedIdentity.getId());
        } catch (IllegalArgumentException e) {
            throw new AssertionError("Generated ID " + savedIdentity.getId() + " is not a valid UUID");
        }
    }

    @Test
    void processIdentityVerifications_withNullList_doesNothing() {
        User user = new User();
        UserSpec spec = new UserSpec();
        spec.setIdentityVerification(null);

        userServiceImpl.processIdentityVerifications(user, spec);

        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void processIdentityVerifications_withEmptyList_doesNothing() {
        User user = new User();
        UserSpec spec = new UserSpec();
        spec.setIdentityVerification(List.of());

        userServiceImpl.processIdentityVerifications(user, spec);

        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void processIdentityVerifications_withNullItemInList_skipsItem() {
        User user = new User();
        UserSpec spec = new UserSpec();
        spec.setIdentityVerification(Arrays.asList((IdentityVerificationSpec) null));

        userServiceImpl.processIdentityVerifications(user, spec);

        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void processIdentityVerifications_withNullVerificationSource_skipsItem() {
        User user = new User();
        UserSpec spec = new UserSpec();
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource(null);
        spec.setIdentityVerification(List.of(ivSpec));

        userServiceImpl.processIdentityVerifications(user, spec);

        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void processIdentityVerifications_withEmptyVerificationSource_skipsItem() {
        User user = new User();
        UserSpec spec = new UserSpec();
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource("");
        spec.setIdentityVerification(List.of(ivSpec));

        userServiceImpl.processIdentityVerifications(user, spec);

        verify(identityRepository, never()).save(any(Identity.class));
        verify(uvidRepository, never()).save(any(Uvid.class));
    }

    @Test
    void processIdentityVerifications_withValidSpec_createsIdentityAndUvid() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail("test@example.com");

        UserSpec spec = new UserSpec();
        IdentityVerificationSpec ivSpec = new IdentityVerificationSpec();
        ivSpec.setVerificationSource("VALID_SOURCE");
        spec.setIdentityVerification(List.of(ivSpec));

        doReturn(DATE_NOW).when(userServiceImpl).getDateNow();
        when(randomService.getString(10)).thenReturn("RANDOMUVID");

        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);
        when(identityRepository.save(identityCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        userServiceImpl.processIdentityVerifications(user, spec);

        Identity savedIdentity = identityCaptor.getValue();
        assertNotNull(savedIdentity.getId());
        assertEquals(DATE_NOW, savedIdentity.getCreated());
        assertEquals("VALID", savedIdentity.getStatus());
        assertEquals(TEST_USER_ID, savedIdentity.getUserId());
        assertEquals("VALID_SOURCE", savedIdentity.getVerificationSource());
        assertEquals("test@example.com", savedIdentity.getEmail());
        assertFalse(savedIdentity.getSecureIndicator());

        ArgumentCaptor<Uvid> uvidCaptor = ArgumentCaptor.forClass(Uvid.class);
        verify(uvidRepository, times(1)).save(uvidCaptor.capture());
        Uvid savedUvid = uvidCaptor.getValue();
        assertEquals("RANDOMUVID", savedUvid.getValue());
        assertEquals("PERMANENT", savedUvid.getType());
        assertEquals(savedIdentity.getId(), savedUvid.getIdentityId());
        assertEquals(DATE_NOW, savedUvid.getCreated());
    }

    @Test
    void processIdentityVerifications_withMixedList_processesValidItemsOnly() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail("test@example.com");

        IdentityVerificationSpec validSpec1 = new IdentityVerificationSpec();
        validSpec1.setVerificationSource("VALID_1");

        IdentityVerificationSpec nullSourceSpec = new IdentityVerificationSpec();
        nullSourceSpec.setVerificationSource(null);

        IdentityVerificationSpec emptySourceSpec = new IdentityVerificationSpec();
        emptySourceSpec.setVerificationSource("");

        IdentityVerificationSpec validSpec2 = new IdentityVerificationSpec();
        validSpec2.setVerificationSource("VALID_2");

        UserSpec spec = new UserSpec();
        spec.setIdentityVerification(Arrays.asList(
                validSpec1,
                nullSourceSpec,
                null,
                emptySourceSpec,
                validSpec2
        ));

        doReturn(DATE_NOW).when(userServiceImpl).getDateNow();
        when(randomService.getString(10)).thenReturn("UVID-1", "UVID-2");
        when(identityRepository.save(any(Identity.class))).thenAnswer(inv -> inv.getArgument(0));

        userServiceImpl.processIdentityVerifications(user, spec);

        verify(identityRepository, times(2)).save(any(Identity.class));
        verify(uvidRepository, times(2)).save(any(Uvid.class));
    }
}
