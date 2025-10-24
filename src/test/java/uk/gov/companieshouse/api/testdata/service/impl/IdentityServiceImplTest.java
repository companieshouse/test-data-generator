package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.UvidData;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class IdentityServiceImplTest {

    @Mock
    private IdentityRepository identityRepository;

    @Mock
    private UvidRepository uvidRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RandomService randomService;

    @Spy
    @InjectMocks
    private IdentityServiceImpl identityServiceImpl;

    @Test
    void testCreateIdentity() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");
        identitySpec.setVerificationSource("source");

        final var createdDate = Instant.now();
        doReturn(createdDate).when(identityServiceImpl).getCurrentDateTime();

        IdentityData createdIdentity = identityServiceImpl.create(identitySpec);

        assertNotNull(createdIdentity.getId(), "ID should be generated");

        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);
        verify(identityRepository).save(identityCaptor.capture());
        Identity savedIdentity = identityCaptor.getValue();

        assertEquals(identitySpec.getEmail(), savedIdentity.getEmail(), "Email should match");
        assertEquals(identitySpec.getUserId(), savedIdentity.getUserId(), "User ID should match");
        assertEquals(identitySpec.getVerificationSource(), savedIdentity.getVerificationSource(),
                "Verification source should match");
        assertNotNull(savedIdentity.getId(), "ID should be set");
        assertEquals("VALID", savedIdentity.getStatus(), "Status should be VALID");
        assertEquals(createdDate, savedIdentity.getCreated(),
                "Created date should be set to the current date");
        verify(identityServiceImpl).getCurrentDateTime();
        assertFalse(savedIdentity.getSecureIndicator(),
                "Secure indicator should be false");
    }

    @Test
    void testCreateIdentityRepositoryException() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");
        identitySpec.setVerificationSource("source");

        doThrow(new RuntimeException("Database error"))
                .when(identityRepository).save(any(Identity.class));

        Exception exception = assertThrows(RuntimeException.class, ()
                -> identityServiceImpl.create(identitySpec));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testCreateIdentityWithUvid_Success() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");
        identitySpec.setVerificationSource("source");

        User mockUser = new User();
        mockUser.setId("testUserId");

        final var createdDate = Instant.now();
        doReturn(createdDate).when(identityServiceImpl).getCurrentDateTime();
        when(userRepository.findById("testUserId")).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId("testUserId")).thenReturn(Optional.empty());
        when(identityRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        when(randomService.getNumber(3)).thenReturn(0L, 1L, 2L); // For "ABC"
        when(randomService.getNumber(1)).thenReturn(5L); // For "5"
        when(randomService.getNumber(2)).thenReturn(3L, 4L); // For "DE"

        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);
        when(identityRepository.save(identityCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        when(uvidRepository.save(any(Uvid.class))).thenAnswer(invocation -> {
            Uvid uvid = invocation.getArgument(0);
            uvid.setIdentityUvid("ABC5DE22223");
            uvid.setType("PERMANENT");
            uvid.setCreated(createdDate);
            uvid.setId(new org.bson.types.ObjectId());
            return uvid;
        });

        UvidData result = identityServiceImpl.createIdentityWithUvid(identitySpec);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("ABC5DE22223", result.getUvid());

        Identity savedIdentity = identityCaptor.getValue();

        ArgumentCaptor<Uvid> uvidCaptor = ArgumentCaptor.forClass(Uvid.class);
        verify(uvidRepository).save(uvidCaptor.capture());
        Uvid savedUvid = uvidCaptor.getValue();

        assertEquals("test@test.com", savedIdentity.getEmail());
        assertEquals("testUserId", savedIdentity.getUserId());
        assertEquals("ABC5DE22223", savedUvid.getIdentityUvid());
        assertEquals("PERMANENT", savedUvid.getType());
        assertEquals(savedIdentity.getId(), savedUvid.getIdentityId());
    }

    @Test
    void testCreateIdentityWithUvid_UserNotFound() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("nonExistentUserId");
        identitySpec.setEmail("test@test.com");

        when(userRepository.findById("nonExistentUserId")).thenReturn(Optional.empty());

        DataException exception = assertThrows(DataException.class, ()
                -> identityServiceImpl.createIdentityWithUvid(identitySpec));
        assertEquals("User not found with ID: nonExistentUserId", exception.getMessage());
    }

    @Test
    void testCreateIdentityWithUvid_IdentityAlreadyExistsWithUvid() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");

        User mockUser = new User();
        mockUser.setId("testUserId");

        Identity existingIdentity = new Identity();
        existingIdentity.setId("existingIdentityId");

        Uvid existingUvid = new Uvid();
        existingUvid.setIdentityUvid("EXISTING123");

        when(userRepository.findById("testUserId")).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId("testUserId")).thenReturn(Optional.of(existingIdentity));
        when(uvidRepository.findByIdentityId("existingIdentityId")).thenReturn(existingUvid);

        DataException exception = assertThrows(DataException.class, ()
                -> identityServiceImpl.createIdentityWithUvid(identitySpec));
        assertEquals("User already has both identity and UVID", exception.getMessage());
    }

    @Test
    void testCreateIdentityWithUvid_IdentityAlreadyExistsWithoutUvid() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");

        User mockUser = new User();
        mockUser.setId("testUserId");

        Identity existingIdentity = new Identity();
        existingIdentity.setId("existingIdentityId");

        when(userRepository.findById("testUserId")).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId("testUserId")).thenReturn(Optional.of(existingIdentity));
        when(uvidRepository.findByIdentityId("existingIdentityId")).thenReturn(null);

        DataException exception = assertThrows(DataException.class, ()
                -> identityServiceImpl.createIdentityWithUvid(identitySpec));
        assertEquals("User already has an identity but no UVID exists", exception.getMessage());
    }

    @Test
    void testCreateIdentityWithUvid_EmailAlreadyUsed() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("existing@test.com");

        User mockUser = new User();
        mockUser.setId("testUserId");

        Identity existingIdentityWithSameEmail = new Identity();
        existingIdentityWithSameEmail.setId("differentIdentityId");

        when(userRepository.findById("testUserId")).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId("testUserId")).thenReturn(Optional.empty());
        when(identityRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(existingIdentityWithSameEmail));

        DataException exception = assertThrows(DataException.class, ()
                -> identityServiceImpl.createIdentityWithUvid(identitySpec));
        assertEquals("Email is already associated with another identity", exception.getMessage());
    }

    @Test
    void testCreateIdentityWithUvid_GeneralException() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");

        User mockUser = new User();
        mockUser.setId("testUserId");

        when(userRepository.findById("testUserId")).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId("testUserId")).thenThrow(new RuntimeException("Unexpected error"));

        DataException exception = assertThrows(DataException.class, ()
                -> identityServiceImpl.createIdentityWithUvid(identitySpec));
        assertEquals("Failed to create identity and UVID", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void testDeleteIdentity_WithoutUvid() {
        Identity mockIdentity = new Identity();
        mockIdentity.setId("identityId");

        when(identityRepository.findById("identityId")).thenReturn(Optional.of(mockIdentity));
        when(uvidRepository.findByIdentityId("identityId")).thenReturn(null);
        doNothing().when(identityRepository).delete(mockIdentity);

        boolean result = identityServiceImpl.delete("identityId");

        assertTrue(result, "Identity should be deleted successfully (no UVID to delete)");
        verify(uvidRepository, never()).delete(any(Uvid.class));
        verify(identityRepository, times(1)).delete(mockIdentity);
    }

    @Test
    void testDeleteIdentityNotFound() {
        String identityId = "nonExistentIdentityId";

        when(identityRepository.findById(identityId)).thenReturn(Optional.empty());

        boolean result = identityServiceImpl.delete(identityId);

        assertFalse(result, "Delete should return false when identity does not exist");
        verify(identityRepository, times(1)).findById(identityId);
        verify(identityRepository, never()).delete(any(Identity.class));
        verify(uvidRepository, never()).delete(any(Uvid.class));
    }

    @Test
    void testDeleteIdentityRepositoryException() {
        String identityId = "identityId";
        Identity mockIdentity = new Identity();
        mockIdentity.setId(identityId);

        when(identityRepository.findById(identityId)).thenReturn(Optional.of(mockIdentity));
        when(uvidRepository.findByIdentityId(identityId)).thenReturn(null);
        doThrow(new RuntimeException("Database error")).when(identityRepository)
                .delete(any(Identity.class));

        Exception exception = assertThrows(RuntimeException.class, ()
                -> identityServiceImpl.delete(identityId));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testGetCurrentDateTime() {
        Instant currentTime = identityServiceImpl.getCurrentDateTime();
        assertNotNull(currentTime, "Current time should not be null");
        assertTrue(currentTime.isAfter(Instant.now().minusSeconds(60)),
                "Current time should be recent");
    }

    /**
     * Provides test cases for validation failures in createIdentityWithUvid.
     *
     * @return A Stream of Arguments, each containing: [userId, email, expectedMessage]
     */
    private static Stream<Arguments> createIdentityWithUvid_ValidationFailureCases() {
        return Stream.of(
                Arguments.of(null, "test@test.com", "User ID is required"),
                Arguments.of("", "test@test.com", "User ID is required"),
                Arguments.of("testUserId", null, "Email is required"),
                Arguments.of("testUserId", "", "Email is required")
        );
    }

    @ParameterizedTest(name = "[{index}] userId=''{0}'', email=''{1}'' => ''{2}''")
    @MethodSource("createIdentityWithUvid_ValidationFailureCases")
    void testCreateIdentityWithUvid_ValidationFailures(String userId, String email, String expectedMessage) throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId(userId);
        identitySpec.setEmail(email);

        DataException exception = assertThrows(DataException.class, ()
               -> identityServiceImpl.createIdentityWithUvid(identitySpec));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreateIdentityWithUvid_UvidSaveException() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("testUserId");
        identitySpec.setEmail("test@test.com");
        identitySpec.setVerificationSource("source");

        User mockUser = new User();
        mockUser.setId("testUserId");

        when(userRepository.findById("testUserId")).thenReturn(Optional.of(mockUser));
        when(identityRepository.findByUserId("testUserId")).thenReturn(Optional.empty());
        when(identityRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        when(uvidRepository.save(any(Uvid.class)))
                .thenThrow(new RuntimeException("UVID database error"));

        DataException exception = assertThrows(DataException.class, ()
                -> identityServiceImpl.createIdentityWithUvid(identitySpec));

        assertEquals("Failed to create identity and UVID", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("UVID database error", exception.getCause().getMessage());

        verify(identityRepository, times(1)).save(any(Identity.class));
        verify(uvidRepository, times(1)).save(any(Uvid.class));
    }

    @Test
    void testDeleteIdentity_WithUvid() {
        Identity mockIdentity = new Identity();
        mockIdentity.setId("identityId");

        Uvid mockUvid = new Uvid();
        mockUvid.setIdentityId("identityId");

        when(identityRepository.findById("identityId")).thenReturn(Optional.of(mockIdentity));
        when(uvidRepository.findByIdentityId("identityId")).thenReturn(mockUvid);
        doNothing().when(uvidRepository).delete(mockUvid);
        doNothing().when(identityRepository).delete(mockIdentity);

        boolean result = identityServiceImpl.delete("identityId");

        assertTrue(result, "Identity and UVID should be deleted successfully");
        verify(uvidRepository, times(1)).delete(mockUvid);
        verify(identityRepository, times(1)).delete(mockIdentity);
    }

    @Test
    void testDeleteIdentity_UvidDeleteException() {
        Identity mockIdentity = new Identity();
        mockIdentity.setId("identityId");

        Uvid mockUvid = new Uvid();
        mockUvid.setIdentityId("identityId");

        when(identityRepository.findById("identityId")).thenReturn(Optional.of(mockIdentity));
        when(uvidRepository.findByIdentityId("identityId")).thenReturn(mockUvid);

        doThrow(new RuntimeException("UVID delete error"))
                .when(uvidRepository).delete(mockUvid);

        Exception exception = assertThrows(RuntimeException.class, ()
                -> identityServiceImpl.delete("identityId"));

        assertEquals("UVID delete error", exception.getMessage());

        verify(uvidRepository, times(1)).delete(mockUvid);
        verify(identityRepository, never()).delete(any(Identity.class));
    }
}