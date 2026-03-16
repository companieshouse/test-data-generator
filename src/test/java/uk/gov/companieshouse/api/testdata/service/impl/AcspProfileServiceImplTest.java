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

import java.time.Instant;
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
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AmlRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
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
    private AcspProfileRequest acspProfileRequest;

    @BeforeEach
    void setUp() {
        acspProfile = new AcspProfile();
        acspProfile.setAcspNumber("randomId");

        acspProfileRequest = new AcspProfileRequest();
    }

    private AmlRequest getAmlSpec(String supervisoryBody, String membershipDetails) {
        AmlRequest amlRequest = new AmlRequest();
        amlRequest.setSupervisoryBody(supervisoryBody);
        amlRequest.setMembershipDetails(membershipDetails);
        return amlRequest;
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
        acspProfileRequest.setStatus("active");
        acspProfileRequest.setType("ltd");
        acspProfileRequest.setBusinessSector("financial-institutions");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(any(AcspProfile.class));
        verify(repository).save(profileCaptor.capture());

        AcspProfile captured = profileCaptor.getValue();
        assertNotNull(captured);
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(acspProfileRequest.getStatus(), captured.getStatus());
        assertEquals(acspProfileRequest.getType(), captured.getType());
        assertEquals(acspProfileRequest.getBusinessSector(), captured.getBusinessSector());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertNull(captured.getAmlDetails());
        assertNull(captured.getEmail());
    }

    @Test
    void createAcspProfileWithDefaultValues() throws DataException {
        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

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
        acspProfileRequest.setStatus("active");
        acspProfileRequest.setType("ltd");

        AmlRequest amlRequest1 = getAmlSpec("association-of-chartered-certified-accountants-acca", "Membership Id: 127678");
        AmlRequest amlRequest2 = getAmlSpec("association-of-accounting-technicians-aat", "Membership Id: 765678");
        AmlRequest amlRequest3 = getAmlSpec("association-of-international-accountants-aia", "Membership Id: 656767");
        acspProfileRequest.setEmail("testdatagenerator@companieshouse.gov.uk");

        acspProfileRequest.setAmlDetails(List.of(amlRequest1, amlRequest2, amlRequest3));

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(acspProfileRequest.getStatus(), captured.getStatus());
        assertEquals(acspProfileRequest.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(0L, captured.getVersion());

        assertNotNull(captured.getAmlDetails());
        assertEquals(3, captured.getAmlDetails().size());
        assertEquals(amlRequest1.getSupervisoryBody(), captured.getAmlDetails().get(0).getSupervisoryBody());
        assertEquals(amlRequest1.getMembershipDetails(), captured.getAmlDetails().get(0).getMembershipDetails());
        assertEquals(amlRequest2.getSupervisoryBody(), captured.getAmlDetails().get(1).getSupervisoryBody());
        assertEquals(amlRequest2.getMembershipDetails(), captured.getAmlDetails().get(1).getMembershipDetails());
        assertEquals(amlRequest3.getSupervisoryBody(), captured.getAmlDetails().get(2).getSupervisoryBody());
        assertEquals(amlRequest3.getMembershipDetails(), captured.getAmlDetails().get(2).getMembershipDetails());
        assertEquals(acspProfileRequest.getEmail(), captured.getEmail());
    }

    @Test
    void createAcspProfileWithEmptyAmlDetails() throws DataException {
        acspProfileRequest.setStatus("active");
        acspProfileRequest.setType("ltd");
        acspProfileRequest.setAmlDetails(Collections.emptyList());
        acspProfileRequest.setEmail("");
        acspProfileRequest.setBusinessSector("");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getAmlDetails());
        assertTrue(captured.getAmlDetails().isEmpty());
        assertEquals("randomId", captured.getId());
        assertEquals("randomId", captured.getAcspNumber());
        assertEquals(acspProfileRequest.getStatus(), captured.getStatus());
        assertEquals(acspProfileRequest.getType(), captured.getType());
        assertEquals("Test Data Generator randomId Company Ltd", captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(acspProfileRequest.getEmail(),captured.getEmail());
        assertEquals(acspProfileRequest.getBusinessSector(),captured.getBusinessSector());
    }

    @Test
    void createAcspProfileWithProvidedAcspNumber() throws DataException {
        acspProfileRequest.setAcspNumber("TestACSP");

        AcspProfile savedProfileWithAcsp = new AcspProfile();
        savedProfileWithAcsp.setAcspNumber(acspProfileRequest.getAcspNumber());
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(randomService.getString(8)).thenReturn("randomId");
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals("TestACSP", result.getAcspNumber());
        verify(repository).save(any(AcspProfile.class));
    }

    @Test
    void createAcspProfileAsSoleTrader() throws DataException {
        acspProfileRequest.setType("sole-trader");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(addressService
                .getCountryOfResidence(JurisdictionType.ENGLAND)).thenReturn("England");
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertCommonProfileDetails(captured, "sole-trader", "Forename randomId",
                "Surname randomId");
    }

    @Test
    void createAcspProfileAsSoleTraderWithDefaultValues() throws DataException {
        acspProfileRequest.setType("sole-trader");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(addressService
                .getCountryOfResidence(JurisdictionType.ENGLAND)).thenReturn("England");
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertCommonProfileDetails(captured, "sole-trader", "Forename randomId",
                "Surname randomId");
    }
     @Test
    void createAcspProfileWithName() throws DataException {
        acspProfileRequest.setName("Business Test");
         acspProfileRequest.setStatus("active");
         acspProfileRequest.setType("ltd");
         acspProfileRequest.setBusinessSector("financial-institutions");
         acspProfileRequest.setAmlDetails(Collections.emptyList());
         AcspProfile savedProfileWithAcsp = new AcspProfile();
        savedProfileWithAcsp.setName(acspProfileRequest.getName());
        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);
        assertEquals(acspProfile.getAcspNumber(), result.getAcspNumber());

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertNotNull(captured);
        assertNotNull(captured.getAmlDetails());
        assertEquals(acspProfileRequest.getStatus(), captured.getStatus());
        assertEquals(acspProfileRequest.getType(), captured.getType());
        assertEquals(acspProfileRequest.getName(),captured.getName());
        assertEquals("/authorised-corporate-service-providers/randomId", captured.getLinksSelf());
        assertEquals(acspProfileRequest.getBusinessSector(),captured.getBusinessSector());
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

    @Test
    void createAcspProfileSetsAuditDetails() throws DataException {
        acspProfileRequest.setStatus("active");
        acspProfileRequest.setType("ltd");

        when(randomService.getString(8)).thenReturn("randomId");
        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(new Address());
        when(repository.save(any(AcspProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AcspProfileResponse result = service.create(acspProfileRequest);

        assertNotNull(result);

        verify(repository).save(profileCaptor.capture());
        AcspProfile captured = profileCaptor.getValue();

        assertNotNull(captured.getCreated());
        assertNotNull(captured.getCreated().getAt());
        assertNotNull(captured.getCreated().getBy());
        assertNotNull(captured.getCreated().getType());

        assertNotNull(captured.getUpdated());
        assertNotNull(captured.getUpdated().getAt());
        assertNotNull(captured.getUpdated().getBy());
        assertNotNull(captured.getUpdated().getType());
    }

    @Test
    void acspProfileSetters_updateValues() {
        AcspProfile profile = new AcspProfile();

        String etagValue = "W/\"123456\"";
        profile.setEtag(etagValue);
        assertEquals(etagValue, profile.getEtag());

        Instant notifiedFromValue = Instant.now();
        profile.setNotifiedFrom(notifiedFromValue);
        assertEquals(notifiedFromValue, profile.getNotifiedFrom());

        String deltaAtValue = "2026-02-03T12:00:00Z";
        profile.setDeltaAt(deltaAtValue);
        assertEquals(deltaAtValue, profile.getDeltaAt());
    }

}