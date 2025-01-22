package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private AcspRepository repository;

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
    void createAcspProfileWithDefaultAmlDetails() throws DataException {
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