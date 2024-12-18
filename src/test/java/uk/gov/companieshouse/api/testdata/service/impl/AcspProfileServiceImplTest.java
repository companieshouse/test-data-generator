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
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.Address;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.AmlDetail;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.Links;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.SensitiveData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
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
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        spec.setCompanyStatus("active");
        spec.setCompanyType("ltd");
        spec.setEmail("john.doe@example.com");

        AmlDetail amlDetail = new AmlDetail();
        amlDetail.setSupervisoryBody("financial-conduct-authority-fca");
        amlDetail.setMembershipDetails("Membership ID: FCA654321");
        spec.setAmlDetails(Collections.singletonList(amlDetail));
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
        assertEquals(0L, captured.getVersion());

        // Verify top-level fields set from spec
        assertEquals("Example ACSP Ltd", captured.getData().getName());
        assertEquals("active", captured.getData().getStatus());
        assertEquals("ltd", captured.getData().getType());
        assertEquals("scotland", captured.getData().getJurisdiction());

        // Check that notified_from, business_sector, and etag are set
        assertEquals(Instant.parse("2024-04-02T00:00:00.000Z"), captured.getData().getNotifiedFrom());
        assertEquals("financial-institutions", captured.getData().getBusinessSector());
        assertEquals("47e85fcf644420129b4388ef9c87496794620893", captured.getData().getEtag());

        // Check registered_office_address
        Address roa = captured.getData().getRegisteredOfficeAddress();
        assertNotNull(roa);
        assertEquals("Jane Smith", roa.getCareOf());
        assertEquals("456 Another Street", roa.getAddressLine1());
        assertEquals("Floor 2", roa.getAddressLine2());
        assertEquals("united-kingdom", roa.getCountry());
        assertEquals("Manchester", roa.getLocality());
        assertEquals("PO Box 123", roa.getPoBox());
        assertEquals("M1 2AB", roa.getPostalCode());
        assertEquals("Another Building", roa.getPremises());
        assertEquals("Greater Manchester", roa.getRegion());

        // Check service_address
        Address serviceAddress = captured.getData().getServiceAddress();
        assertNotNull(serviceAddress);
        assertEquals("Jane Smith", serviceAddress.getCareOf());

        // Check AML details
        assertNotNull(captured.getData().getAmlDetails());
        assertEquals(1, captured.getData().getAmlDetails().size());
        AmlDetail detail = captured.getData().getAmlDetails().get(0);
        assertEquals("financial-conduct-authority-fca", detail.getSupervisoryBody());
        assertEquals("Membership ID: FCA654321", detail.getMembershipDetails());

        // Check links
        Links links = captured.getData().getLinks();
        assertNotNull(links);
        assertEquals("/acsp/123456", links.getSelf());

        // Check sensitive data
        SensitiveData sensitiveData = captured.getSensitiveData();
        assertNotNull(sensitiveData);
        assertEquals("john.doe@example.com", sensitiveData.getEmail());
    }

    @Test
    void deleteWhenExists() {
        AcspProfile profile = new AcspProfile();
        when(repository.findById("123456")).thenReturn(Optional.of(profile));

        boolean deleted = acspProfileService.delete(123456L);
        assertTrue(deleted);
        verify(repository).delete(profile);
    }

    @Test
    void deleteWhenNotFound() {
        when(repository.findById("999999")).thenReturn(Optional.empty());

        boolean deleted = acspProfileService.delete(999999L);
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
