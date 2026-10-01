package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class IdentityServiceImplTest {

    @Mock
    private IdentityRepository identityRepository;

    @Mock
    private UvidRepository uvidRepository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private IdentityServiceImpl identityService;

    private static final String IDENTITY_ID = "identity-123";
    private static final String UVID_ID = "uvid-123";
    private static final String EMAIL = "test@test.com";
    private static final String ACSP_ID = "acsp-id";
    private static final String ACSP_USER_ID = "acsp-user-id";

    @Test
    void createIdentitySuccess() {
        IdentityVerificationRequest request = new IdentityVerificationRequest();

        request.setEmail(EMAIL);
        request.setAcspId(ACSP_ID);
        request.setAcspUserId(ACSP_USER_ID);

        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(randomService.getString(10)).thenReturn("abcdefghij");
        when(uvidRepository.save(any(Uvid.class)))
                .thenAnswer(invocation -> {
                    Uvid uvid = invocation.getArgument(0);
                    uvid.setId(UVID_ID);
                    return uvid;
                });

        IdentityVerificationResponse response = identityService.createIdentity(request);

        assertNotNull(response);
        assertNotNull(response.getIdentityId());
        assertEquals(UVID_ID, response.getUvid());
        assertEquals(ACSP_ID, response.getAcspId());
        assertEquals(ACSP_USER_ID, response.getAcspUserId());

        verify(identityRepository, times(1)).findByEmail(EMAIL);
        verify(identityRepository, times(1)).save(any(Identity.class));
        verify(uvidRepository, times(1)).save(any(Uvid.class));
    }

    @Test
    void createIdentityAlreadyExists() {
        IdentityVerificationRequest request = new IdentityVerificationRequest();
        request.setEmail(EMAIL);
        Identity existingIdentity = new Identity();

        when(identityRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingIdentity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> identityService.createIdentity(request));

        assertEquals("Identity already exists for email: " + EMAIL, exception.getMessage());

        verify(identityRepository, never()).save(any());
        verify(uvidRepository, never()).save(any());
    }

    @Test
    void getIdentitySuccess() {
        Identity identity = new Identity();
        identity.setId(IDENTITY_ID);
        identity.setAcspId(ACSP_ID);
        identity.setAcspUserId(ACSP_USER_ID);

        Uvid uvid = new Uvid();
        uvid.setId(UVID_ID);
        uvid.setIdentityId(IDENTITY_ID);

        when(identityRepository.findById(IDENTITY_ID)).thenReturn(Optional.of(identity));
        when(uvidRepository.findByIdentityId(IDENTITY_ID)).thenReturn(Optional.of(uvid));

        IdentityVerificationResponse response = identityService.getIdentity(IDENTITY_ID);

        assertNotNull(response);
        assertEquals(IDENTITY_ID, response.getIdentityId());
        assertEquals(UVID_ID, response.getUvid());
        assertEquals(ACSP_ID, response.getAcspId());
        assertEquals(ACSP_USER_ID, response.getAcspUserId());

        verify(identityRepository, times(1)).findById(IDENTITY_ID);
        verify(uvidRepository, times(1)).findByIdentityId(IDENTITY_ID);
    }

    @Test
    void getIdentityNotFound() {
        when(identityRepository.findById(IDENTITY_ID)).thenReturn(Optional.empty());

        IdentityVerificationResponse response = identityService.getIdentity(IDENTITY_ID);

        assertNull(response);

        verify(identityRepository, times(1)).findById(IDENTITY_ID);
    }

    @Test
    void deleteIdentitySuccess() {
        Identity identity = new Identity();
        identity.setId(IDENTITY_ID);

        when(identityRepository.findById(IDENTITY_ID)).thenReturn(Optional.of(identity));

        boolean deleted = identityService.deleteIdentity(IDENTITY_ID);

        assertTrue(deleted);

        verify(uvidRepository, times(1)).deleteByIdentityId(IDENTITY_ID);
        verify(identityRepository, times(1)).deleteById(IDENTITY_ID);
    }

    @Test
    void deleteIdentityNotFound() {
        when(identityRepository.findById(IDENTITY_ID)).thenReturn(Optional.empty());

        boolean deleted = identityService.deleteIdentity(IDENTITY_ID);

        assertFalse(deleted);

        verify(identityRepository, times(1)).findById(IDENTITY_ID);
        verify(identityRepository, never()).deleteById(any());
        verify(uvidRepository, never()).deleteByIdentityId(any());
    }
}