package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.Links;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspProfileServiceImplTest {

    @Mock
    private RandomService randomService;

    @Mock
    private AddressService addressService;

    @Mock
    private AcspProfileRepository repository;

    @InjectMocks
    private AcspProfileServiceImpl acspProfileService;

    @Captor
    private ArgumentCaptor<AcspProfile> acspProfileCaptor;

    private AcspSpec spec;

    @BeforeEach
    void setUp() {
        spec = new AcspSpec();
        spec.setAcspNumber(123456L);
        spec.setCompanyStatus("active");
        spec.setCompanyType("ltd");
    }

    @Test
    void createAcspProfile() {
        AcspProfile savedProfile = new AcspProfile();
        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfile returnedProfile = acspProfileService.create(spec);

        assertEquals(savedProfile, returnedProfile);

        verify(repository).save(acspProfileCaptor.capture());
        AcspProfile captured = acspProfileCaptor.getValue();

        // Verify ID and version
        assertEquals("123456", captured.getId());

        // Verify top-level fields set from spec
        assertEquals("Example ACSP Ltd", captured.getData().getName());
        assertEquals("active", captured.getData().getStatus());
        assertEquals("ltd", captured.getData().getType());

        // Check links
        Links links = captured.getData().getLinks();
        assertNotNull(links);
        assertEquals("/acsp/123456", links.getSelf());

    }

    @Test
    void deleteWhenExists() {
        AcspProfile profile = new AcspProfile();
        when(repository.findById("123456")).thenReturn(Optional.of(profile));

        boolean deleted = acspProfileService.delete(String.valueOf(123456L));
        assertTrue(deleted);
        verify(repository).delete(profile);
    }

    @Test
    void deleteWhenNotFound() {
        when(repository.findById("999999")).thenReturn(Optional.empty());

        boolean deleted = acspProfileService.delete(String.valueOf(999999L));
        assertFalse(deleted);
        verify(repository, never()).delete(any());
    }

    @Test
    void acspProfileExistsTrue() {
        when(repository.findById("123456")).thenReturn(Optional.of(new AcspProfile()));
        assertTrue(acspProfileService.acspProfileExists(123456L));
    }

    @Test
    void acspProfileExistsFalse() {
        when(repository.findById("999999")).thenReturn(Optional.empty());
        assertFalse(acspProfileService.acspProfileExists(999999L));
    }
}
