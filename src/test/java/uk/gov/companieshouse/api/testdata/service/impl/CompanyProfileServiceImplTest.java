package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceImplTest {

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
    void createEnglandWales() throws DataException {
        final Address mockServiceAddress = new Address(
                "","","","",""
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
        assertEquals("Company " + COMPANY_NUMBER + " LIMITED", profile.getCompanyName());
        assertEquals("active", profile.getCompanyStatus());
        assertEquals("england-wales", profile.getJurisdiction());
        assertEquals("ltd", profile.getType());

        assertEquals(mockServiceAddress, profile.getRegisteredOfficeAddress());

        assertEquals("/company/"+COMPANY_NUMBER, profile.getLinks().getSelf());
        assertEquals("/company/"+COMPANY_NUMBER+ "/filing-history", profile.getLinks().getFilingHistory());
        assertEquals("/company/"+COMPANY_NUMBER+ "/officers", profile.getLinks().getOfficers());
        assertEquals("/company/"+COMPANY_NUMBER+ "/persons-with-significant-control-statement",
                profile.getLinks().getPersonsWithSignificantControlStatement());

        CompanyProfile.Accounts accounts = profile.getAccounts();
        assertNotNull(accounts);
        assertNotNull(accounts.getNextDue());
        assertNotNull(accounts.getPeriodStart());
        assertNotNull(accounts.getPeriodEnd());
        assertNotNull(accounts.getNextAccountsDueOn());
        assertEquals(false, accounts.getNextAccountsOverdue());
        assertNotNull(accounts.getNextMadeUpTo());
        assertNotNull(accounts.getAccountingReferenceDateDay());
        assertNotNull(accounts.getAccountingReferenceDateMonth());

        assertNotNull(profile.getDateOfCreation());
        assertEquals(false, profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());

        CompanyProfile.ConfirmationStatement confirmationStatement = profile.getConfirmationStatement();
        assertNotNull(confirmationStatement.getNextMadeUpTo());
        assertEquals(false, confirmationStatement.getOverdue());
        assertNotNull(confirmationStatement.getNextDue());

        assertEquals(false, profile.getRegisteredOfficeIsInDispute());
        assertEquals(false, profile.getHasInsolvencyHistory());
        assertEquals(false, profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
    }

    @Test
    void createScotland() throws DataException {
        final Address mockServiceAddress = new Address(
                "","","","",""
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
        assertEquals("Company " + COMPANY_NUMBER + " LIMITED", profile.getCompanyName());
        assertEquals("active", profile.getCompanyStatus());
        assertEquals("scotland", profile.getJurisdiction());
        assertEquals("ltd", profile.getType());

        assertEquals(mockServiceAddress, profile.getRegisteredOfficeAddress());

        assertEquals("/company/"+COMPANY_NUMBER, profile.getLinks().getSelf());
        assertEquals("/company/"+COMPANY_NUMBER+ "/filing-history", profile.getLinks().getFilingHistory());
        assertEquals("/company/"+COMPANY_NUMBER+ "/officers", profile.getLinks().getOfficers());
        assertEquals("/company/"+COMPANY_NUMBER+ "/persons-with-significant-control-statement",
                profile.getLinks().getPersonsWithSignificantControlStatement());

        CompanyProfile.Accounts accounts = profile.getAccounts();
        assertNotNull(accounts);
        assertNotNull(accounts.getNextDue());
        assertNotNull(accounts.getPeriodStart());
        assertNotNull(accounts.getPeriodEnd());
        assertNotNull(accounts.getNextAccountsDueOn());
        assertEquals(false, accounts.getNextAccountsOverdue());
        assertNotNull(accounts.getNextMadeUpTo());
        assertNotNull(accounts.getAccountingReferenceDateDay());
        assertNotNull(accounts.getAccountingReferenceDateMonth());

        assertNotNull(profile.getDateOfCreation());
        assertEquals(false, profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());

        CompanyProfile.ConfirmationStatement confirmationStatement = profile.getConfirmationStatement();
        assertNotNull(confirmationStatement.getNextMadeUpTo());
        assertEquals(false, confirmationStatement.getOverdue());
        assertNotNull(confirmationStatement.getNextDue());

        assertEquals(false, profile.getRegisteredOfficeIsInDispute());
        assertEquals(false, profile.getHasInsolvencyHistory());
        assertEquals(false, profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
    }

    @Test
    void createMongoException() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.companyProfileService.create(spec)
        );
        assertEquals("Failed to save company profile", exception.getMessage());
    }

    @Test
    void delete() throws Exception {
        CompanyProfile companyProfile = new CompanyProfile();
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(companyProfile);

        this.companyProfileService.delete(COMPANY_NUMBER);

        verify(repository).delete(companyProfile);
    }
    
    @Test
    void deleteNoCompanyProfile() {
        CompanyProfile companyProfile = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(companyProfile);
        
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () ->
            this.companyProfileService.delete(COMPANY_NUMBER)
        );
        assertEquals("company profile data not found", exception.getMessage());
    }

    @Test
    void deleteMongoException() {
        CompanyProfile companyProfile = new CompanyProfile();
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(companyProfile);
        doThrow(MongoException.class).when(repository).delete(companyProfile);
        DataException exception = assertThrows(DataException.class, () ->
            this.companyProfileService.delete(COMPANY_NUMBER)
        );
        assertEquals("Failed to delete company profile", exception.getMessage());
    }

}