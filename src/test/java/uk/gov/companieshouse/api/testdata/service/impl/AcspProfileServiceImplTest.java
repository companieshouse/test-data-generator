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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AmlSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspProfileServiceImplTest {

    @Mock
    private AcspProfileRepository repository;

    @Mock
    private AddressService addressService;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private AcspProfileServiceImpl service;

    @Captor
    private ArgumentCaptor<AcspProfile> profileCaptor;

    private AcspProfile acspProfile;
    private AcspProfileSpec acspProfileSpec;

    @BeforeEach
    void setUp() {
        acspProfile = new AcspProfile();
        acspProfile.setAcspNumber("randomId");

        acspProfileSpec = new AcspProfileSpec();
    }

    private AmlSpec getAmlSpec(String supervisoryBody, String membershipDetails) {
        AmlSpec amlSpec = new AmlSpec();
        amlSpec.setSupervisoryBody(supervisoryBody);
        amlSpec.setMembershipDetails(membershipDetails);
        return amlSpec;
    }

    private void assertCommonProfileDetails(AcspProfile captured, String type, String forename, String surname) {
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals("active", captured.getStatus());
        assertEquals(type, captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());

        assertNotNull(captured.getSoleTraderDetails());
        assertEquals(forename, captured.getSoleTraderDetails().getForename());
        assertEquals(surname, captured.getSoleTraderDetails().getSurname());
        assertEquals("British", captured.getSoleTraderDetails().getNationality());
        assertEquals("England", captured.getSoleTraderDetails().getUsualResidentialCountry());
    }

    @Test
    void createAcspProfile() throws DataException {
        acspProfileSpec.setStatus("active");
        acspProfileSpec.setType("ltd");
        acspProfileSpec.setBusinessSector("financial-institutions");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(any(AcspProfile.class));
        verify(repository).save(profileCaptor.capture());

        AcspProfile captured = profileCaptor.getValue();
        assertNotNull(captured);
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(acspProfileSpec.getStatus(), captured.getStatus());
        assertEquals(acspProfileSpec.getType(), captured.getType());
        assertEquals(acspProfileSpec.getBusinessSector(), captured.getBusinessSector());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertNull(captured.getAmlDetails());
        assertNull(captured.getEmail());
    }

    @Test
    void createAcspProfileWithDefaultValues() throws DataException {
        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals("active", captured.getStatus());
        assertEquals("limited-company", captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());
    }

    @Test
    void createAcspProfileWithAmlDetails() throws DataException {
        acspProfileSpec.setStatus("active");
        acspProfileSpec.setType("ltd");

        AmlSpec amlSpec1 = getAmlSpec("association-of-chartered-certified-accountants-acca", "Membership Id: 127678");
        AmlSpec amlSpec2 = getAmlSpec("association-of-accounting-technicians-aat", "Membership Id: 765678");
        AmlSpec amlSpec3 = getAmlSpec("association-of-international-accountants-aia", "Membership Id: 656767");
        acspProfileSpec.setEmail("testdatagenerator@companieshouse.gov.uk");

        acspProfileSpec.setAmlDetails(List.of(amlSpec1, amlSpec2, amlSpec3));

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(acspProfileSpec.getStatus(), captured.getStatus());
        assertEquals(acspProfileSpec.getType(), captured.getType());
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
        assertEquals(acspProfileSpec.getEmail(), captured.getEmail());
    }

    @Test
    void createAcspProfileWithEmptyAmlDetails() throws DataException {
        acspProfileSpec.setStatus("active");
        acspProfileSpec.setType("ltd");
        acspProfileSpec.setAmlDetails(Collections.emptyList());
        acspProfileSpec.setEmail("");
        acspProfileSpec.setBusinessSector("");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getAmlDetails());
        assertTrue(captured.getAmlDetails().isEmpty());
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(acspProfileSpec.getStatus(), captured.getStatus());
        assertEquals(acspProfileSpec.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(acspProfileSpec.getEmail(),captured.getEmail());
        assertEquals(acspProfileSpec.getBusinessSector(),captured.getBusinessSector());
    }

    @Test
    void createAcspProfileWithProvidedAcspNumber() throws DataException {
        acspProfileSpec.setAcspNumber("TestACSP");

        AcspProfile savedProfileWithAcsp = new AcspProfile();
        savedProfileWithAcsp.setAcspNumber(acspProfileSpec.getAcspNumber());
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(randomService.getString(8)).thenReturn("randomId");
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals("TestACSP", result.getAcspNumber());
        verify(repository).save(any(AcspProfile.class));
    }

    @Test
    void createAcspProfileAsSoleTrader() throws DataException {
        acspProfileSpec.setType("sole-trader");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(addressService
                .getCountryOfResidence(Jurisdiction.ENGLAND)).thenReturn("England");
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertCommonProfileDetails(captured, "sole-trader", "Forename randomId",
                "Surname randomId");
    }

    @Test
    void createAcspProfileAsSoleTraderWithDefaultValues() throws DataException {
        acspProfileSpec.setType("sole-trader");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(addressService
                .getCountryOfResidence(Jurisdiction.ENGLAND)).thenReturn("England");
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertCommonProfileDetails(captured, "sole-trader", "Forename randomId",
                "Surname randomId");
    }
     @Test
    void createAcspProfileWithName() throws DataException {
        acspProfileSpec.setName("Business Test");
         acspProfileSpec.setStatus("active");
         acspProfileSpec.setType("ltd");
         acspProfileSpec.setBusinessSector("financial-institutions");
         acspProfileSpec.setAmlDetails(Collections.emptyList());
         AcspProfile savedProfileWithAcsp = new AcspProfile();
        savedProfileWithAcsp.setName(acspProfileSpec.getName());
        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(Jurisdiction.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileData result = service.create(acspProfileSpec);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getAmlDetails());
        assertEquals(acspProfileSpec.getStatus(), captured.getStatus());
        assertEquals(acspProfileSpec.getType(), captured.getType());
        assertEquals(acspProfileSpec.getName(),captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(acspProfileSpec.getBusinessSector(),captured.getBusinessSector());
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

    @Test
    void getAcspProfileFound() {
        AcspProfile savedProfile = new AcspProfile();
        savedProfile.setAcspNumber("AP123456");
        savedProfile.setId("AP123456");

        when(repository.findById("AP123456")).thenReturn(Optional.of(savedProfile));

        Optional<AcspProfile> result = service.getAcspProfile("AP123456");

        assertTrue(result.isPresent());
        assertEquals("AP123456", result.get().getAcspNumber());
        assertEquals("AP123456", result.get().getId());

        verify(repository).findById("AP123456");
    }

    @Test
    void getAcspProfileNotFound() {
        when(repository.findById("AP123456")).thenReturn(Optional.empty());

        Optional<AcspProfile> result = service.getAcspProfile("AP123456");

        assertFalse(result.isPresent());
        verify(repository).findById("AP123456");
    }

    @Test
    void getAcspProfileRepositoryThrowsException() {
        when(repository.findById("AP123456")).thenThrow(new RuntimeException("DB error"));

        Optional<AcspProfile> result = service.getAcspProfile("AP123456");

        assertFalse(result.isPresent());
        verify(repository).findById("AP123456");
    }
}