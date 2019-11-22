package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.NewCompanyRequest;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock
    private TestDataService testDataService;

    @InjectMocks
    private TestDataController testDataController;

    @Test
    void create() throws Exception {
        NewCompanyRequest request = new NewCompanyRequest();
        request.setJurisdiction(Jurisdiction.SCOTLAND);
        CompanyData company = new CompanyData("12345678", "123456");

        when(this.testDataService.createCompanyData(request.getJurisdiction())).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.create(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void createNoRequest() throws Exception {
        NewCompanyRequest request = null;
        CompanyData company = new CompanyData("12345678", "123456");

        // ENGLAND/WALES is the default jurisdiction
        when(this.testDataService.createCompanyData(Jurisdiction.ENGLAND_WALES)).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.create(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void createNoJurisdiction() throws Exception {
        NewCompanyRequest request = new NewCompanyRequest();
        CompanyData company = new CompanyData("12345678", "123456");

        // ENGLAND/WALES is the default jurisdiction
        when(this.testDataService.createCompanyData(Jurisdiction.ENGLAND_WALES)).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.create(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createException() throws Exception {
        NewCompanyRequest request = new NewCompanyRequest();
        request.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        Throwable exception = new DataException("Error message");
        when(this.testDataService.createCompanyData(request.getJurisdiction())).thenThrow(exception);

        assertThrows(DataException.class, () -> {
            this.testDataController.create(request);
        }, exception.getMessage());
    }

    @Test
    void delete() throws Exception {
        final String companyId = "123456";
        ResponseEntity<Void> response = this.testDataController.delete(companyId);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(testDataService).deleteCompanyData(companyId);
    }

    @Test
    void deleteException() throws Exception {
        final String companyId = "123456";
        
        NoDataFoundException ex = new NoDataFoundException("Error message");
        doThrow(ex).when(this.testDataService).deleteCompanyData(companyId);

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.delete(companyId);
        }, ex.getMessage());
    }
}