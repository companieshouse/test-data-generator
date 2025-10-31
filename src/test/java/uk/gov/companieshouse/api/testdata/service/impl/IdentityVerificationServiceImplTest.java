package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityVerificationData;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityVerificationServiceImplTest {

    @Mock
    private IdentityRepository identityRepository;

    @Mock
    private UvidRepository uvidRepository;

    @InjectMocks
    private IdentityVerificationServiceImpl service;

    @Test
    void getIdentityVerification_whenIdentityNotFound_returnsNull() {
        String email = "missing@example.com";

        when(identityRepository.findByEmail(email)).thenReturn(Optional.empty());

        IdentityVerificationData result = service.getIdentityVerificationData(email);

        assertNull(result);
        verify(identityRepository, times(1)).findByEmail(email);
        verifyNoInteractions(uvidRepository);
    }

    @Test
    void getIdentityVerification_whenUvidNotFound_returnsNull() {
        String email = "user@example.com";
        String identityId = "identity-id-123";

        Identity identityMock = mock(Identity.class);
        when(identityMock.getId()).thenReturn(identityId);

        when(identityRepository.findByEmail(email)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(identityId)).thenReturn(Optional.empty());

        IdentityVerificationData result = service.getIdentityVerificationData(email);

        assertNull(result);
        verify(identityRepository, times(1)).findByEmail(email);
        verify(uvidRepository, times(1)).findByIdentityId(identityId);
    }

    @Test
    void getIdentityVerification_whenBothFound_returnsData() {
        String email = "user@example.com";
        String identityId = "identity-id-123";
        String uvidValue = "UVID-ABC";

        Identity identityMock = mock(Identity.class);
        when(identityMock.getId()).thenReturn(identityId);

        Uvid uvid = new Uvid();
        uvid.setValue(uvidValue);
        uvid.setIdentityId(identityId);

        when(identityRepository.findByEmail(email)).thenReturn(Optional.of(identityMock));
        when(uvidRepository.findByIdentityId(identityId)).thenReturn(Optional.of(uvid));

        IdentityVerificationData result = service.getIdentityVerificationData(email);

        assertNotNull(result);
        assertEquals(identityId, result.getIdentityId());
        assertEquals(uvidValue, result.getUvid());

        verify(identityRepository, times(1)).findByEmail(email);
        verify(uvidRepository, times(1)).findByIdentityId(identityId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t"})
    void getIdentityVerification_whenEmailIsBlankOrNull_returnsNull(String invalidEmail) {
        IdentityVerificationData result = service.getIdentityVerificationData(invalidEmail);

        assertNull(result, "Service should return null for invalid email input");
        verifyNoInteractions(identityRepository);
        verifyNoInteractions(uvidRepository);
    }
}