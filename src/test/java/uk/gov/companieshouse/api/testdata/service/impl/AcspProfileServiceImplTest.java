package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AmlDetails;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AmlSpec;
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

    private AcspProfile createSavedProfile() {
        AcspProfile profile = new AcspProfile();
        profile.setAcspNumber("randomId");
        return profile;
    }

    @Test
    void createAcspProfile() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = createSavedProfile();
        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspNumber());

        verify(repository).save(any(AcspProfile.class));
    }

    @Test
    void createAcspProfileWithDefaultValues() throws DataException {
        when(randomService.getString(8)).thenReturn("randomId");
            AcspProfile savedProfile = createSavedProfile();
        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileSpec spec = new AcspProfileSpec();
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
    void createAcspProfileWithAmlDetails() throws DataException {
        AcspProfileSpec spec = getAcspProfileSpec();

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = getAcspProfile(spec);

        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals(savedProfile.getAcspNumber(), result.getAcspNumber());

        ArgumentCaptor<AcspProfile> captor = ArgumentCaptor.forClass(AcspProfile.class);
        verify(repository).save(captor.capture());

        AcspProfile captured = captor.getValue();
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(spec.getStatus(), captured.getStatus());
        assertEquals(spec.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());

        assertNotNull(captured.getAmlDetails());
        assertEquals(3, captured.getAmlDetails().size());
        assertEquals("Supervisory Body 1",
                captured.getAmlDetails().get(0).getSupervisoryBody());
        assertEquals("Membership Details 1",
                captured.getAmlDetails().get(0).getMembershipDetails());
        assertEquals("Supervisory Body 2",
                captured.getAmlDetails().get(1).getSupervisoryBody());
        assertEquals("Membership Details 2",
                captured.getAmlDetails().get(1).getMembershipDetails());
        assertEquals("Supervisory Body 3",
                captured.getAmlDetails().get(2).getSupervisoryBody());
        assertEquals("Membership Details 3",
                captured.getAmlDetails().get(2).getMembershipDetails());
    }

    private AcspProfile getAcspProfile(AcspProfileSpec spec) {
        AcspProfile savedProfile = createSavedProfile();

        List<AmlDetails> amlDetailsList = new ArrayList<>();
        for (AmlSpec amlSpec : spec.getAmlDetails()) {
            AmlDetails amlDetails = new AmlDetails();
            amlDetails.setSupervisoryBody(amlSpec.getSupervisoryBody());
            amlDetails.setMembershipDetails(amlSpec.getMembershipDetails());
            amlDetailsList.add(amlDetails);
        }
        savedProfile.setAmlDetails(amlDetailsList);
        return savedProfile;
    }

    private AcspProfileSpec getAcspProfileSpec() {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");

        // Creating multiple AML details entries
        AmlSpec amlSpec1 = new AmlSpec();
        amlSpec1.setSupervisoryBody("Supervisory Body 1");
        amlSpec1.setMembershipDetails("Membership Details 1");

        AmlSpec amlSpec2 = new AmlSpec();
        amlSpec2.setSupervisoryBody("Supervisory Body 2");
        amlSpec2.setMembershipDetails("Membership Details 2");

        AmlSpec amlSpec3 = new AmlSpec();
        amlSpec3.setSupervisoryBody("Supervisory Body 3");
        amlSpec3.setMembershipDetails("Membership Details 3");

        spec.setAmlDetails(List.of(amlSpec1, amlSpec2, amlSpec3));
        return spec;
    }

    @Test
    void createAcspProfileWithNullAmlDetails() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");
        spec.setAmlDetails(null); // Setting AmlDetails as null

        when(randomService.getString(8)).thenReturn("randomId");
            AcspProfile savedProfile = createSavedProfile();

        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspNumber());

        ArgumentCaptor<AcspProfile> captor = ArgumentCaptor.forClass(AcspProfile.class);
        verify(repository).save(captor.capture());

        AcspProfile captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(spec.getStatus(), captured.getStatus()); // Default value
        assertEquals(spec.getType(), captured.getType()); // Default value
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertNull(captured.getAmlDetails()); // Ensure it remains null
    }

    @Test
    void createAcspProfileWithEmptyAmlDetails() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");
        spec.setAmlDetails(Collections.emptyList()); // Setting an empty list

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = createSavedProfile();
        savedProfile.setAmlDetails(new ArrayList<>()); // Ensure it's an empty list

        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspNumber());

        ArgumentCaptor<AcspProfile> captor = ArgumentCaptor.forClass(AcspProfile.class);
        verify(repository).save(captor.capture());

        AcspProfile captured = captor.getValue();
        assertNotNull(captured);
        assertNotNull(captured.getAmlDetails());
        assertTrue(captured.getAmlDetails().isEmpty()); // Ensure the list is empty
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