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
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DisqualificationsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyAuthCodeResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyUpdateResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyWorkflowService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private DeleteCompanyWorkflowService deleteCompanyWorkflowService;

    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

    @Mock
    private CompanyProfileService companyProfileService;

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
        verify(deleteCompanyWorkflowService).deleteCompany(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyInternalWithValidAuthCode() throws Exception {
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("654321");

        when(companyAuthCodeService.verifyAuthCode(COMPANY_NUMBER, "654321")).thenReturn(true);

        ResponseEntity<Void> response = internalCompanyController.deleteCompany(COMPANY_NUMBER, request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteCompanyWorkflowService).deleteCompany(COMPANY_NUMBER);
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
        doThrow(ex).when(deleteCompanyWorkflowService).deleteCompany(COMPANY_NUMBER);

        DataException thrown = assertThrows(DataException.class,
                () -> internalCompanyController.deleteCompany(COMPANY_NUMBER, null));
        assertEquals(ex, thrown);
    }

    @Test
    void deleteCompanyInternalNoDataFoundException() throws Exception {
        NoDataFoundException ex = new NoDataFoundException("Company not found");
        doThrow(ex).when(deleteCompanyWorkflowService).deleteCompany(COMPANY_NUMBER);

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

    @Test
    void updateCompanySuccess() throws Exception {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        CompanyProfile updatedProfile = new CompanyProfile();
        updatedProfile.setCompanyNumber(COMPANY_NUMBER);

        when(companyProfileService.updateCompanyProfile(request)).thenReturn(updatedProfile);

        ResponseEntity<Object> response = internalCompanyController.updateCompany(request);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CompanyUpdateResponse body = (CompanyUpdateResponse) response.getBody();
        assertEquals(COMPANY_NUMBER, body.getCompanyNumber());
        assertEquals("updated", body.getStatus());

        verify(companyProfileService).updateCompanyProfile(request);
    }

    @Test
    void updateCompanyNotFound() throws Exception {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        String errorMessage = "Company not found";
        when(companyProfileService.updateCompanyProfile(request))
                .thenThrow(new NoDataFoundException(errorMessage));

        ResponseEntity<Object> response = internalCompanyController.updateCompany(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, body.get("error"));
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));
    }

    @Test
    void updateCompanyDataException() throws Exception {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        String errorMessage = "Internal server error";
        when(companyProfileService.updateCompanyProfile(request))
                .thenThrow(new DataException(errorMessage));

        ResponseEntity<Object> response = internalCompanyController.updateCompany(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(errorMessage, body.get("error"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
    }

    @Test
    void findOrCreateCompanyAuthCodeSuccess() throws Exception {
        var authCode = new CompanyAuthCode();
        authCode.setId(COMPANY_NUMBER);
        authCode.setAuthCode("CODE123");

        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenReturn(authCode);

        ResponseEntity<CompanyAuthCodeResponse> response =
                internalCompanyController.findOrCreateCompanyAuthCode(COMPANY_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CODE123", response.getBody().getAuthCode());
    }

    @Test
    void findOrCreateCompanyAuthCodeNullCompanyNumberThrowsDataException() {
        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyController.findOrCreateCompanyAuthCode(null));
        assertEquals("companyNumber query parameter is required", thrown.getMessage());
    }

    @Test
    void findOrCreateCompanyAuthCodeEmptyCompanyNumberThrowsDataException() {
        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyController.findOrCreateCompanyAuthCode(""));
        assertEquals("companyNumber query parameter is required", thrown.getMessage());
    }

    @Test
    void findOrCreateCompanyAuthCodeServiceThrowsDataException() throws Exception {
        DataException ex = new DataException("Service error");
        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyController.findOrCreateCompanyAuthCode(COMPANY_NUMBER));
        assertEquals(ex, thrown);
    }

    @Test
    void findOrCreateCompanyAuthCodeServiceThrowsNoDataFoundException() throws Exception {
        NoDataFoundException ex = new NoDataFoundException("Not found");
        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                internalCompanyController.findOrCreateCompanyAuthCode(COMPANY_NUMBER));
        assertEquals(ex, thrown);
    }
}
