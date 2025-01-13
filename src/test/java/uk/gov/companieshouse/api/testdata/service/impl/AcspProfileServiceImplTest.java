package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspProfileServiceImplTest {

    private static final String ACSP_PREFIX = "PlaywrightACSP";
    private static final String LINK_STEM = "/authorised-corporate-service-providers/";

    @Mock
    private AcspRepository repository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private AcspProfileServiceImpl acspProfileService;

    @Captor
    private ArgumentCaptor<AcspProfile> acspProfileCaptor;

    @Test
    void testCreateSuccess() throws DataException {
        // Given
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setAcspNumber("ACSP_NUMBER");   // Not actually used for ID, since the code uses randomService
        spec.setCompanyType("ltd");
        spec.setCompanyStatus("active");

        long randomId = 12345678L;
        when(randomService.getNumber(8)).thenReturn(randomId);

        AcspProfileData result = acspProfileService.create(spec);

        verify(repository).save(acspProfileCaptor.capture());
        AcspProfile savedProfile = acspProfileCaptor.getValue();

        String expectedId = ACSP_PREFIX + randomId; // "PlaywrightACSP12345678"

        assertEquals(expectedId, savedProfile.getId(), "Profile ID should match the prefixed random ID");
        assertEquals(0L, savedProfile.getVersion(), "Version should be 0 by default");
        assertEquals(expectedId, savedProfile.getAcspNumber(), "acspNumber should match the ID");
        assertEquals("active", savedProfile.getStatus(), "Profile status should match 'companyStatus' from the spec");
        assertEquals("ltd", savedProfile.getType(), "Profile type should match 'companyType' from the spec");
        assertTrue(savedProfile.getName().contains(String.valueOf(randomId)), "Profile name should contain the random ID");
        assertTrue(savedProfile.getLinksSelf().contains(expectedId), "Profile linksSelf should contain the random ID");

        // The returned AcspProfileData should contain the prefixed ACSP number
        assertEquals(expectedId, result.getAcspNumber(), "Returned AcspProfileData should match the profile's acspNumber");
    }

    @Test
    void testDeleteFound() {

        String acspProfileId = "PlaywrightACSP99999999";
        AcspProfile profile = new AcspProfile();
        profile.setId(acspProfileId);

        when(repository.findById(acspProfileId)).thenReturn(Optional.of(profile));

        boolean deleted = acspProfileService.delete(acspProfileId);

        assertTrue(deleted, "Should return true if the profile exists");
        verify(repository).delete(profile);
    }

    @Test
    void testDeleteNotFound() {

        String acspProfileId = "PlaywrightACSPNoSuch";
        when(repository.findById(acspProfileId)).thenReturn(Optional.empty());

        boolean deleted = acspProfileService.delete(acspProfileId);

        assertFalse(deleted, "Should return false if the profile doesn't exist");
        verify(repository, never()).delete(any(AcspProfile.class));
    }

    @Test
    void testGetAcspProfileByIdFound() {

        String acspProfileId = "PlaywrightACSP1234";
        AcspProfile profile = new AcspProfile();
        profile.setId(acspProfileId);

        when(repository.findById(acspProfileId)).thenReturn(Optional.of(profile));

        Optional<AcspProfile> result = acspProfileService.getAcspProfileById(acspProfileId);

        assertTrue(result.isPresent(), "Should return an Optional with the AcspProfile if found");
        assertEquals(acspProfileId, result.get().getId(), "Profile ID should match");
        verify(repository).findById(acspProfileId);
    }

    @Test
    void testGetAcspProfileByIdNotFound() {

        String acspProfileId = "PlaywrightACSPNoSuch";
        when(repository.findById(acspProfileId)).thenReturn(Optional.empty());

        Optional<AcspProfile> result = acspProfileService.getAcspProfileById(acspProfileId);

        assertTrue(result.isEmpty(), "Should return an empty Optional if not found");
        verify(repository).findById(acspProfileId);
    }
}
