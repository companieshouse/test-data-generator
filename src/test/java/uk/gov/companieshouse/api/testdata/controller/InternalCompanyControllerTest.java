package uk.gov.companieshouse.api.testdata.controller;

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
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DisqualificationsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalCompanyControllerTest {

    private static final String COMPANY_NUMBER = "TC123456";
    private static final String COMPANY_URI = "http://localhost:1234/company/12345678";

    @Mock
    private CreateCompanyWorkflowService createCompanyWorkflowService;

    @Mock
    private DeleteCompanyService deleteCompanyService;

    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

    @InjectMocks
    private InternalCompanyController internalCompanyController;

    @Captor
    private ArgumentCaptor<CompanyRequest> specCaptor;

    @Test
    void createInternalCompany() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createInternalCompany(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = internalCompanyController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createInternalCompanyNoRequest() throws Exception {
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createInternalCompany(any())).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = internalCompanyController.createCompany(null);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(createCompanyWorkflowService).createInternalCompany(specCaptor.capture());
        CompanyRequest usedSpec = specCaptor.getValue();

        assertEquals(JurisdictionType.ENGLAND_WALES, usedSpec.getJurisdiction());
    }

    @Test
    void createInternalCompanyDefaultJurisdiction() throws Exception {
        CompanyRequest request = new CompanyRequest();
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createInternalCompany(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = internalCompanyController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(JurisdictionType.ENGLAND_WALES, request.getJurisdiction());
    }

    @Test
    void createInternalCompanyWithDisqualifications() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        DisqualificationsRequest disqSpec = new DisqualificationsRequest();
        disqSpec.setCorporateOfficer(false);
        request.setDisqualifiedOfficers(java.util.List.of(disqSpec));

        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", "http://localhost:4001/company/12345678");

        when(createCompanyWorkflowService.createInternalCompany(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = internalCompanyController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createInternalCompanyException() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setJurisdiction(JurisdictionType.NI);
        DataException exception = new DataException("Error message");
        when(createCompanyWorkflowService.createInternalCompany(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyController.createCompany(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteCompanyInternalSuccess() throws Exception {
        ResponseEntity<Void> response = internalCompanyController.deleteCompany(COMPANY_NUMBER, null);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteCompanyService).deleteCompany(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyInternalWithValidAuthCode() throws Exception {
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("654321");

        when(companyAuthCodeService.verifyAuthCode(COMPANY_NUMBER, "654321")).thenReturn(true);

        ResponseEntity<Void> response = internalCompanyController.deleteCompany(COMPANY_NUMBER, request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteCompanyService).deleteCompany(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyInternalWithInvalidAuthCode() throws Exception {
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("wrongCode");

        when(companyAuthCodeService.verifyAuthCode(COMPANY_NUMBER, "wrongCode")).thenReturn(false);

        InvalidAuthCodeException thrown = assertThrows(InvalidAuthCodeException.class,
                () -> internalCompanyController.deleteCompany(COMPANY_NUMBER, request));
        assertEquals(COMPANY_NUMBER, thrown.getCompanyNumber());
    }

    @Test
    void deleteCompanyInternalDataException() throws Exception {
        DataException ex = new DataException("error");
        doThrow(ex).when(deleteCompanyService).deleteCompany(COMPANY_NUMBER);

        DataException thrown = assertThrows(DataException.class,
                () -> internalCompanyController.deleteCompany(COMPANY_NUMBER, null));
        assertEquals(ex, thrown);
    }

    @Test
    void deleteCompanyInternalNoDataFoundException() throws Exception {
        NoDataFoundException ex = new NoDataFoundException("Company not found");
        doThrow(ex).when(deleteCompanyService).deleteCompany(COMPANY_NUMBER);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class,
                () -> internalCompanyController.deleteCompany(COMPANY_NUMBER, null));
        assertEquals(ex, thrown);
    }

    @Test
    void buildCompanyDataStructureSuccess() throws Exception {
        CompanyRequest request = new CompanyRequest();
        PopulatedCompanyDetailsResponse responseObj = new PopulatedCompanyDetailsResponse();
        when(createCompanyWorkflowService.buildCompanyDataStructure(request)).thenReturn(responseObj);

        ResponseEntity<PopulatedCompanyDetailsResponse> response =
                internalCompanyController.buildCompanyDataStructure(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseObj, response.getBody());
        verify(createCompanyWorkflowService, times(1)).buildCompanyDataStructure(request);
    }

    @Test
    void buildCompanyDataStructureNullRequestUsesDefault() throws Exception {
        PopulatedCompanyDetailsResponse responseObj = new PopulatedCompanyDetailsResponse();
        when(createCompanyWorkflowService.buildCompanyDataStructure(any(CompanyRequest.class)))
                .thenReturn(responseObj);

        ResponseEntity<PopulatedCompanyDetailsResponse> response =
                internalCompanyController.buildCompanyDataStructure(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseObj, response.getBody());
        verify(createCompanyWorkflowService, times(1)).buildCompanyDataStructure(any(CompanyRequest.class));
    }

    @Test
    void buildCompanyDataStructureThrowsDataException() throws Exception {
        CompanyRequest request = new CompanyRequest();
        DataException exception = new DataException("error");
        when(createCompanyWorkflowService.buildCompanyDataStructure(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyController.buildCompanyDataStructure(request));
        assertEquals(exception, thrown);
    }

    @Test
    void persistCompanyDataStructureSuccess() throws Exception {
        CompanyWithPopulatedStructureRequest request = new CompanyWithPopulatedStructureRequest();
        CompanyProfileResponse companyData =
                new CompanyProfileResponse("12345678", "123456", "http://localhost:4001/company/12345678");
        when(createCompanyWorkflowService.persistCompanyDataStructure(request)).thenReturn(companyData);

        ResponseEntity<CompanyProfileResponse> response =
                internalCompanyController.persistCompanyDataStructure(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(companyData, response.getBody());
        verify(createCompanyWorkflowService, times(1)).persistCompanyDataStructure(request);
    }

    @Test
    void persistCompanyDataStructureNullRequestUsesDefault() throws Exception {
        CompanyProfileResponse companyData =
                new CompanyProfileResponse("12345678", "123456", "http://localhost:4001/company/12345678");
        when(createCompanyWorkflowService.persistCompanyDataStructure(any(CompanyWithPopulatedStructureRequest.class)))
                .thenReturn(companyData);

        ResponseEntity<CompanyProfileResponse> response =
                internalCompanyController.persistCompanyDataStructure(null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(companyData, response.getBody());
        verify(createCompanyWorkflowService, times(1)).persistCompanyDataStructure(any(CompanyWithPopulatedStructureRequest.class));
    }

    @Test
    void persistCompanyDataStructureThrowsDataException() throws Exception {
        CompanyWithPopulatedStructureRequest request = new CompanyWithPopulatedStructureRequest();
        DataException exception = new DataException("error");
        when(createCompanyWorkflowService.persistCompanyDataStructure(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyController.persistCompanyDataStructure(request));
        assertEquals(exception, thrown);
    }
}

