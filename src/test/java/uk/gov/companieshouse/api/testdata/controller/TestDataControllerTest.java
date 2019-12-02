package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock
    private TestDataService testDataService;
    
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

    @InjectMocks
    private TestDataController testDataController;
    
    @Captor
    private ArgumentCaptor<CompanySpec> specCaptor;

    @Test
    void create() throws Exception {
        CompanySpec request = new CompanySpec();
        request.setJurisdiction(Jurisdiction.SCOTLAND);
        CompanyData company = new CompanyData("12345678", "123456");

        when(this.testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.create(Optional.ofNullable(request));

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    
    @Test
    void createNoRequest() throws Exception {
        CompanySpec request = null;
        CompanyData company = new CompanyData("12345678", "123456");

        when(this.testDataService.createCompanyData(any())).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.create(Optional.ofNullable(request));

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        verify(testDataService).createCompanyData(specCaptor.capture());
        CompanySpec usedSpec = specCaptor.getValue();

        assertEquals(Jurisdiction.ENGLAND_WALES, usedSpec.getJurisdiction());
    }
    
    @Test
    void createDefaultJurisdiction() throws Exception {
        CompanySpec request = new CompanySpec();
        CompanyData company = new CompanyData("12345678", "123456");

        when(this.testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.create(Optional.ofNullable(request));

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // england/wales is the default jurisdiction
        assertEquals(Jurisdiction.ENGLAND_WALES, request.getJurisdiction());
    }

    @Test
    void createException() throws Exception {
        CompanySpec request = new CompanySpec();
        request.setJurisdiction(Jurisdiction.NI);
        Throwable exception = new DataException("Error message");
        when(this.testDataService.createCompanyData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () -> {
            this.testDataController.create(Optional.ofNullable(request));
        });
        assertEquals(exception, thrown);
    }

    @Test
    void delete() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        final boolean validAuthCode = true;

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())).thenReturn(validAuthCode);

        ResponseEntity<Void> response = this.testDataController.delete(companyNumber, request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(testDataService).deleteCompanyData(companyNumber);
    }

    @Test
    void deleteDataException() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        final boolean validAuthCode = true;

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())).thenReturn(validAuthCode);

        DataException ex = new DataException("Error message");
        doThrow(ex).when(this.testDataService).deleteCompanyData(companyNumber);

        DataException thrown = assertThrows(DataException.class, () -> {
            this.testDataController.delete(companyNumber, request);
        });
        assertEquals(ex, thrown);
    }

    @Test
    void deleteInvalidAuthCode() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        final boolean validAuthCode = false;

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())).thenReturn(validAuthCode);

        InvalidAuthCodeException thrown = assertThrows(InvalidAuthCodeException.class, () -> {
            this.testDataController.delete(companyNumber, request);
        });
        assertEquals(companyNumber, thrown.getCompanyNumber());
    }
    
    @Test
    void deleteNoAuthCodeFound() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        NoDataFoundException ex = new NoDataFoundException("no auth code");

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())).thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.delete(companyNumber, request);
        });
        assertEquals(ex, thrown);
    }
}