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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
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

    @Captor
    private ArgumentCaptor<AcspProfile> profileCaptor;

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
        assertEquals(savedProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(any(AcspProfile.class));
        verify(repository).save(profileCaptor.capture());

        AcspProfile captured = profileCaptor.getValue();
        assertNotNull(captured);
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(spec.getStatus(), captured.getStatus());
        assertEquals(spec.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());

        assertNull(captured.getAmlDetails());
        assertNull(captured.getEmail());
    }

    @Test
    void createAcspProfileWithDefaultValues() throws DataException {
        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = createSavedProfile();
        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileSpec spec = new AcspProfileSpec();
        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals(savedProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals("active", captured.getStatus()); // Default value
        assertEquals("limited-company", captured.getType()); //
        // Default value
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());
    }

    private AmlSpec getAmlSpec(String SupervisoryBody, String MembershipDetails)  {
        AmlSpec amlSpec = new AmlSpec();
        amlSpec.setSupervisoryBody(SupervisoryBody);
        amlSpec.setMembershipDetails(MembershipDetails);
        return amlSpec;
    }

    @Test
    void createAcspProfileWithAmlDetails() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");

        AmlSpec amlSpec1 = getAmlSpec("association-of-chartered-certified-accountants-acca", "Membership Id: 127678");
        AmlSpec amlSpec2 = getAmlSpec("association-of-accounting-technicians-aat", "Membership Id: 765678");
        AmlSpec amlSpec3 = getAmlSpec("association-of-international-accountants-aia", "Membership Id: 656767");
        spec.setEmail("testdatagenerator@companieshouse.gov.uk");

        spec.setAmlDetails(List.of(amlSpec1, amlSpec2, amlSpec3));

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = createSavedProfile();

        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals(savedProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(spec.getStatus(), captured.getStatus());
        assertEquals(spec.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());

        assertNotNull(captured.getAmlDetails());
        assertEquals(3, captured.getAmlDetails().size());
        assertEquals(amlSpec1.getSupervisoryBody(), captured.getAmlDetails().get(0).getSupervisoryBody());
        assertEquals(amlSpec1.getMembershipDetails(), captured.getAmlDetails().get(0).getMembershipDetails());
        assertEquals(amlSpec2.getSupervisoryBody(), captured.getAmlDetails().get(1).getSupervisoryBody());
        assertEquals(amlSpec2.getMembershipDetails(), captured.getAmlDetails().get(1).getMembershipDetails());
        assertEquals(amlSpec3.getSupervisoryBody(), captured.getAmlDetails().get(2).getSupervisoryBody());
        assertEquals(amlSpec3.getMembershipDetails(), captured.getAmlDetails().get(2).getMembershipDetails());
        assertEquals(spec.getEmail(), captured.getEmail());
    }

    @Test
    void createAcspProfileWithEmptyAmlDetails() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setStatus("active");
        spec.setType("ltd");
        spec.setAmlDetails(Collections.emptyList()); // Setting an empty list
        spec.setEmail("");

        when(randomService.getString(8)).thenReturn("randomId");
        AcspProfile savedProfile = createSavedProfile();

        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals(savedProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getAmlDetails());
        assertTrue(captured.getAmlDetails().isEmpty()); // Ensure the list is empty
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(spec.getStatus(), captured.getStatus());
        assertEquals(spec.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(spec.getEmail(),captured.getEmail());
    }

    @Test
    void createAcspProfileWithProvidedAcspNumber() throws DataException {
        AcspProfileSpec spec = new AcspProfileSpec();
        spec.setAcspNumber("TestACSP");

        AcspProfile savedProfile = new AcspProfile();
        savedProfile.setAcspNumber(spec.getAcspNumber());

        when(repository.save(any(AcspProfile.class))).thenReturn(savedProfile);

        AcspProfileData result = service.create(spec);

        assertNotNull(result);
        assertEquals("TestACSP", result.getAcspNumber());
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