package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspProfileServiceImplTest {

    @Mock
    private AcspProfileRepository repository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private AcspProfileServiceImpl service;

    @Test
    void createAcspProfile() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = new AcspProfile();
        savedProfile.setAcspNumber("randomId");
        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspNumber());

        verify(repository).save(any(AcspProfile.class));
    }

    @Test
    void createAcspProfileWithDefaultValues() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = new AcspProfile();
        savedProfile.setAcspNumber("randomId");
        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspNumber());

        ArgumentCaptor<AcspProfile> captor = ArgumentCaptor.forClass(AcspProfile.class);
        verify(repository).save(captor.capture());

        AcspProfile captured = captor.getValue();
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals("active", captured.getStatus()); // Default value
        assertEquals("ltd", captured.getType()); // Default value
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());
    }

    @Test
    void deleteAcspProfile() {
        AcspProfile acspProfile = new AcspProfile();
        when(repository.findById("profileId")).thenReturn(Optional.of(acspProfile));

        boolean result = service.delete("profileId");

        assertTrue(result);
        verify(repository).delete(acspProfile);
    }

    @Test
    void deleteAcspProfileNotFound() {
        when(repository.findById("profileId")).thenReturn(Optional.empty());

        boolean result = service.delete("profileId");

        assertFalse(result);
        verify(repository, never()).delete(any(AcspProfile.class));
    }
}