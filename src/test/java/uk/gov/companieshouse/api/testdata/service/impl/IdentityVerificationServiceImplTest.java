package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdentityVerificationServiceImplTest {

    private static final String EMAIL = "user@example.com";
    private static final String IDENTITY_ID = "identity-id-123";
    private static final String UVID_VALUE = "UVID-ABC";
    private static final String USER_ID = "user-999";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Mock
    private IdentityRepository identityRepository;

    @Mock
    private UvidRepository uvidRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IdentityVerificationServiceImpl service;

    private Identity identityMock;
    private Uvid uvid;
    private User user;

    @BeforeEach
    void setUp() {
        identityMock = mock(Identity.class);
        when(identityMock.getId()).thenReturn(IDENTITY_ID);
        when(identityMock.getUserId()).thenReturn(USER_ID);

        uvid = new Uvid();
        uvid.setValue(UVID_VALUE);
        uvid.setIdentityId(IDENTITY_ID);

        user = new User();
        user.setForename(FIRST_NAME);
        user.setSurname(LAST_NAME);
    }

    @Test
    void getIdentityVerification_whenIdentityNotFound_returnsNull() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNull(result);
        verify(identityRepository, times(1)).findByEmail(EMAIL);
        verifyNoInteractions(uvidRepository, userRepository);
    }

    @Test
    void getIdentityVerification_whenUvidNotFound_returnsNull() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.empty());

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNull(result);

        verify(identityRepository, times(1)).findByEmail(EMAIL);
        verify(uvidRepository, times(1)).findByIdentityId(IDENTITY_ID);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getIdentityVerification_whenAllFound_returnsFullData() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNotNull(result);
        assertEquals(IDENTITY_ID, result.getIdentityId());
        assertEquals(UVID_VALUE, result.getUvid());
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t"})
    void getIdentityVerification_whenEmailIsBlankOrNull_returnsNull(String invalidEmail) {

        IdentityVerificationResponse result = service.getIdentityVerificationData(invalidEmail);

        assertNull(result, "Service should return null for invalid email input");
        verifyNoInteractions(identityRepository, uvidRepository, userRepository);
    }

    @Test
    void getIdentityVerification_userIdIsNull_returnsEmptyNames() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));
        when(identityMock.getUserId()).thenReturn(null);

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNotNull(result);
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        verifyNoInteractions(userRepository);
    }

    @Test
    void getIdentityVerification_userIdIsBlank_returnsEmptyNames() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));
        when(identityMock.getUserId()).thenReturn("  ");

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNotNull(result);
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        verifyNoInteractions(userRepository);
    }

    @Test
    void getIdentityVerification_userNotFound_returnsEmptyNames() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNotNull(result);
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getIdentityVerification_userFound_withNullNames_returnsEmptyNames() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));

        user.setForename(null);
        user.setSurname(null);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNotNull(result);
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void getIdentityVerification_userFound_withNames_returnsNames() {
        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        IdentityVerificationResponse result = service.getIdentityVerificationData(EMAIL);

        assertNotNull(result);
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        verify(userRepository, times(1)).findById(USER_ID);
    }
}