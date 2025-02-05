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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceImplTest {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final String COMPANY_NUMBER = "12345678";
    private static final String ETAG = "ETAG";
    private static final String COMPANY_STATUS_DISSOLVED = "dissolved";
    private static final String COMPANY_TYPE_PLC = "plc";
    private static final String COMPANY_STATUS_ACTIVE = "active";
    private static final String COMPANY_TYPE_LTD = "ltd";
    private static final String COMPANY_STATUS_ADMINISTRATION = "administration";

    @Mock
    private RandomService randomService;
    @Mock
    private AddressService addressService;
    @Mock
    private CompanyProfileRepository repository;

    @InjectMocks
    private CompanyProfileServiceImpl companyProfileService;

    private CompanySpec spec;
    private CompanyProfile savedProfile;


    @BeforeEach
    void setUp() {
        spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        savedProfile = new CompanyProfile();
    }

    // Test that a company profile is created with default company type
    // with England and Wales jurisdiction
    @Test
    void createCompanyWithoutCompanyTypeAndWithEnglandWales() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus(COMPANY_STATUS_ADMINISTRATION);
        assertCreateCompanyProfile(spec.getCompanyStatus(),
                spec.getJurisdiction().toString(), COMPANY_TYPE_LTD, false);
    }

    // Test that a company profile is created with default company status with SCOTLAND jurisdiction
    @Test
    void createCompanyWithoutCompanyStatusAndWithScotland() {
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        spec.setCompanyType(COMPANY_TYPE_LTD);
        assertCreateCompanyProfile(COMPANY_STATUS_ACTIVE,
                spec.getJurisdiction().toString(), spec.getCompanyType(), false);
    }

    // Test that a company profile is deleted
    @Test
    void delete() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.of(savedProfile));

        assertTrue(this.companyProfileService.delete(COMPANY_NUMBER));
        verify(repository).delete(savedProfile);
    }

    // Test that a company profile is not deleted when it does not exist
    @Test
    void deleteNoCompanyProfile() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.empty());

        assertFalse(this.companyProfileService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

    // Test that a company profile is created with dissolved company status
    @Test
    void createDissolvedCompany() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus(COMPANY_STATUS_DISSOLVED);
        assertCreateCompanyProfile(spec.getCompanyStatus(),
                spec.getJurisdiction().toString(), COMPANY_TYPE_LTD, true);
    }

    // Test that a company profile is created with plc company type
    @Test
    void createPlcCompany() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(COMPANY_TYPE_PLC);
        assertCreateCompanyProfile(COMPANY_STATUS_ACTIVE,
                spec.getJurisdiction().toString(), spec.getCompanyType(), false);
    }


    private void assertCreateCompanyProfile(String companyStatus, String jurisdiction,
                                            String companyType, Boolean hasInsolvencyHistory) {
        Address mockRegiseteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegiseteredAddress);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor
                = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_NUMBER, profile.getId());
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertEquals("COMPANY " + COMPANY_NUMBER + " LIMITED", profile.getCompanyName());
        assertEquals(companyStatus, profile.getCompanyStatus());
        assertEquals(jurisdiction, profile.getJurisdiction());
        assertEquals(companyType, profile.getType());
        assertEquals(mockRegiseteredAddress, profile.getRegisteredOfficeAddress());
        assertEquals(false, profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());
        assertOnConfirmationStatement(profile.getConfirmationStatement());
        assertEquals(false, profile.getRegisteredOfficeIsInDispute());
        assertEquals(hasInsolvencyHistory, profile.getHasInsolvencyHistory());
        assertEquals(false, profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
        assertOnAccounts(profile.getAccounts());
        assertOnDateOfCreation(profile.getDateOfCreation());
        assertEquals("/company/" + COMPANY_NUMBER, profile.getLinks().getSelf());
        assertEquals("/company/" + COMPANY_NUMBER + "/filing-history",
                profile.getLinks().getFilingHistory());
        assertEquals("/company/" + COMPANY_NUMBER + "/officers", profile.getLinks().getOfficers());
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statement",
                profile.getLinks().getPersonsWithSignificantControlStatement());
    }

    private void assertOnConfirmationStatement(CompanyProfile.ConfirmationStatement
                                                       confirmationStatement) {
        assertNotNull(confirmationStatement.getNextMadeUpTo());
        assertEquals(false, confirmationStatement.getOverdue());
        assertNotNull(confirmationStatement.getNextDue());
    }

    private void assertOnAccounts(CompanyProfile.Accounts accounts) {
        assertNotNull(accounts);
        assertNotNull(accounts.getNextDue());
        assertNotNull(accounts.getPeriodStart());
        assertNotNull(accounts.getPeriodEnd());
        assertNotNull(accounts.getNextAccountsDueOn());
        assertEquals(false, accounts.getNextAccountsOverdue());
        assertNotNull(accounts.getNextMadeUpTo());
        assertNotNull(accounts.getAccountingReferenceDateDay());
        assertNotNull(accounts.getAccountingReferenceDateMonth());
    }

    private void assertOnDateOfCreation(Instant dateOfCreation) {
        assertNotNull(dateOfCreation);

        Instant now = Instant.now();
        assertTrue(now.isAfter(dateOfCreation));

        LocalDateTime t1 = LocalDateTime.ofInstant(dateOfCreation, ZONE_ID_UTC);
        LocalDateTime t2 = LocalDateTime.ofInstant(now, ZONE_ID_UTC);

        long days = Duration.between(t1, t2).toDays();
        assertTrue(days == 365 || days == 366); // cater for leap years
    }

    @Test
    void createCompanyWithSubType() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(COMPANY_TYPE_LTD);
        spec.setSubType("community-interest-company");

        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor
                = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals("community-interest-company", profile.getSubtype());
        assertTrue(profile.getIsCommunityInterestCompany());
    }

    @Test
    void createCompanyWithoutSubType() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(COMPANY_TYPE_LTD);
        spec.setSubType(null);

        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor =
                ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertNull(profile.getSubtype());
        assertNull(profile.getIsCommunityInterestCompany());
    }

    @Test
    void createCompanyWithNonCicSubType() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(COMPANY_TYPE_LTD);
        spec.setSubType("private-fund-limited-partnership");

        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor =
                ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals("private-fund-limited-partnership", profile.getSubtype());
        assertFalse(profile.getIsCommunityInterestCompany());
    }

    @Test
    void createCompanyWithSuperSecurePscsTrue() {
        spec.setHasSuperSecurePscs(true);
        Address mockRegisteredAddress = new Address("", "", "", "", "", "");

        when(randomService.getEtag()).thenReturn("ETAG");
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(true, profile.getHasSuperSecurePscs());
    }

    @Test
    void createCompanyWithSuperSecurePscsFalse() {
        spec.setHasSuperSecurePscs(false);
        Address mockRegisteredAddress = new Address("", "", "", "", "", "");

        when(randomService.getEtag()).thenReturn("ETAG");
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(false, profile.getHasSuperSecurePscs());
    }

    @Test
    void createCompanyWithSuperSecurePscsNull() {
        spec.setHasSuperSecurePscs(null);
        Address mockRegisteredAddress = new Address("", "", "", "", "", "");

        when(randomService.getEtag()).thenReturn("ETAG");
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(null, profile.getHasSuperSecurePscs());
    }
}
