package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Mock
    private RandomService randomService;
    @Mock
    private AddressService addressService;
    @Mock
    private CompanyProfileRepository repository;

    @InjectMocks
    private CompanyProfileServiceImpl companyProfileService;

    @Test
    void createEnglandWales() {
        final Address mockServiceAddress = new Address(
                "","","","","",""
        );
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);

        CompanyProfile savedProfile = new CompanyProfile();
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockServiceAddress);
        CompanyProfile returnedProfile = this.companyProfileService.create(spec);

        assertEquals(savedProfile, returnedProfile);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_NUMBER, profile.getId());
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertEquals("COMPANY " + COMPANY_NUMBER + " LIMITED", profile.getCompanyName());
        assertEquals("active", profile.getCompanyStatus());
        assertEquals("england-wales", profile.getJurisdiction());
        assertEquals("ltd", profile.getType());

        assertEquals(mockServiceAddress, profile.getRegisteredOfficeAddress());

        assertEquals("/company/"+COMPANY_NUMBER, profile.getLinks().getSelf());
        assertEquals("/company/"+COMPANY_NUMBER+ "/filing-history", profile.getLinks().getFilingHistory());
        assertEquals("/company/"+COMPANY_NUMBER+ "/officers", profile.getLinks().getOfficers());
        assertEquals("/company/"+COMPANY_NUMBER+ "/persons-with-significant-control-statement",
                profile.getLinks().getPersonsWithSignificantControlStatement());

        assertOnAccounts(profile.getAccounts());

        assertOnDateofCreation(profile.getDateOfCreation());

        assertEquals(false, profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());

        assertOnConfirmationStatement(profile.getConfirmationStatement());

        assertEquals(false, profile.getRegisteredOfficeIsInDispute());
        assertEquals(false, profile.getHasInsolvencyHistory());
        assertEquals(false, profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
    }

    private void assertOnConfirmationStatement(CompanyProfile.ConfirmationStatement confirmationStatement) {
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

    private void assertOnDateofCreation(Instant dateOfCreation) {
        assertNotNull(dateOfCreation);

        Instant now = Instant.now();
        assertTrue(now.isAfter(dateOfCreation));

        LocalDateTime t1 = LocalDateTime.ofInstant(dateOfCreation, ZONE_ID_UTC);
        LocalDateTime t2 = LocalDateTime.ofInstant(now, ZONE_ID_UTC);

        long days = Duration.between(t1, t2).toDays();
        assertTrue(days == 365 || days == 366); // cater for leap years
    }

    @Test
    void createScotland() {
        final Address mockServiceAddress = new Address(
                "","","","","",""
        );
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setJurisdiction(Jurisdiction.SCOTLAND);

        CompanyProfile savedProfile = new CompanyProfile();
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockServiceAddress);
        CompanyProfile returnedProfile = this.companyProfileService.create(spec);

        assertEquals(savedProfile, returnedProfile);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_NUMBER, profile.getId());
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertEquals("COMPANY " + COMPANY_NUMBER + " LIMITED", profile.getCompanyName());
        assertEquals("active", profile.getCompanyStatus());
        assertEquals("scotland", profile.getJurisdiction());
        assertEquals("ltd", profile.getType());

        assertEquals(mockServiceAddress, profile.getRegisteredOfficeAddress());

        assertEquals("/company/"+COMPANY_NUMBER, profile.getLinks().getSelf());
        assertEquals("/company/"+COMPANY_NUMBER+ "/filing-history", profile.getLinks().getFilingHistory());
        assertEquals("/company/"+COMPANY_NUMBER+ "/officers", profile.getLinks().getOfficers());
        assertEquals("/company/"+COMPANY_NUMBER+ "/persons-with-significant-control-statement",
                profile.getLinks().getPersonsWithSignificantControlStatement());

        assertOnAccounts(profile.getAccounts());

        assertOnDateofCreation(profile.getDateOfCreation());

        assertEquals(false, profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());

        assertOnConfirmationStatement(profile.getConfirmationStatement());

        assertEquals(false, profile.getRegisteredOfficeIsInDispute());
        assertEquals(false, profile.getHasInsolvencyHistory());
        assertEquals(false, profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
    }


    @Test
    void delete() {
        CompanyProfile companyProfile = new CompanyProfile();
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.of(companyProfile));

        assertTrue(this.companyProfileService.delete(COMPANY_NUMBER));
        verify(repository).delete(companyProfile);
    }

    @Test
    void deleteNoCompanyProfile() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.empty());

        assertFalse(this.companyProfileService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

}
