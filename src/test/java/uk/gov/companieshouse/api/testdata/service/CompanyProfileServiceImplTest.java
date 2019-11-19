package uk.gov.companieshouse.api.testdata.service;

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

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyProfileServiceImpl;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ETAG = "ETAG";

    @Mock
    private RandomService randomService;
    @Mock
    private CompanyProfileRepository repository;

    @InjectMocks
    private CompanyProfileServiceImpl companyProfileService;

    @Test
    void createNoException() throws DataException {
        CompanyProfile savedProfile = new CompanyProfile();
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        CompanyProfile returnedProfile = this.companyProfileService.create(COMPANY_NUMBER);

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

        assertEquals("Crown Way", profile.getRegisteredOfficeAddress().getAddressLine1());
        assertEquals("United Kingdom", profile.getRegisteredOfficeAddress().getCountry());
        assertEquals("Cardiff", profile.getRegisteredOfficeAddress().getLocality());
        assertEquals("CF14 3UZ", profile.getRegisteredOfficeAddress().getPostalCode());

        assertEquals("/company/"+COMPANY_NUMBER, profile.getLinks().getSelf());
        assertEquals("/company/"+COMPANY_NUMBER+ "/filing-history", profile.getLinks().getFilingHistory());
        assertEquals("/company/"+COMPANY_NUMBER+ "/officers", profile.getLinks().getOfficers());
        assertEquals("/company/"+COMPANY_NUMBER+ "/persons-with-significant-control-statement", profile.getLinks().getPersonsWithSignificantControlStatement());

        assertNotNull(profile.getAccountsNextDue());
        assertNotNull(profile.getPeriodStart());
        assertNotNull(profile.getPeriodEnd());
        assertNotNull(profile.getNextAccountsDueOn());
        assertEquals(false, profile.getNextAccountsOverdue());
        assertNotNull(profile.getAccountsNextMadeUpTo());
        assertNotNull(profile.getAccountingReferenceDateDay());
        assertNotNull(profile.getAccountingReferenceDateMonth());
        assertNotNull(profile.getDateOfCreation());
        assertEquals(false, profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());
        assertNotNull(profile.getConfirmationStatementNextMadeUpTo());
        assertEquals(false, profile.getConfirmationStatementOverdue());
        assertNotNull(profile.getConfirmationStatementNextDue());
        assertEquals(false, profile.getRegisteredOfficeIsInDispute());
        assertEquals(false, profile.getHasInsolvencyHistory());
        assertEquals(false, profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
    }

    @Test
    void createDuplicateKeyException() {
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.companyProfileService.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.companyProfileService.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
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
        assertEquals("failed to delete", exception.getMessage());
    }

}