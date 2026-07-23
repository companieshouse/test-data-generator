package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.OfficerType;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyWorkflowService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicCompanyControllerTest {

    private static final String COMPANY_URI = "http://localhost:1234/company/12345678";

    @Mock
    private CreateCompanyWorkflowService createCompanyWorkflowService;

    @Mock
    private DeleteCompanyWorkflowService deleteCompanyWorkflowService;

    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

    private PublicCompanyController publicCompanyController;

    @Captor
    private ArgumentCaptor<PublicCompanyRequest> publicSpecCaptor;

    @BeforeEach
    void setUp() {
        publicCompanyController = new PublicCompanyController(
                createCompanyWorkflowService,
                deleteCompanyWorkflowService,
                companyAuthCodeService);
    }

    @Test
    void createPublicCompanyV1() throws Exception {
        PublicCompanyRequest request = new PublicCompanyRequest();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createPublicCompany(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = publicCompanyController.createPublicCompanyV1(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createCompanyDefaultWithEmptyRequest() throws Exception {
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createPublicCompany(any())).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = publicCompanyController.createPublicCompanyV1(null);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(createCompanyWorkflowService).createPublicCompany(publicSpecCaptor.capture());
        PublicCompanyRequest usedPublicCompanyRequest = publicSpecCaptor.getValue();

        assertEquals(JurisdictionType.ENGLAND_WALES, usedPublicCompanyRequest.getJurisdiction());
    }

    @Test
    void createPublicCompanyV1DefaultJurisdiction() throws Exception {
        PublicCompanyRequest request = new PublicCompanyRequest();
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createPublicCompany(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = publicCompanyController.createPublicCompanyV1(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(JurisdictionType.ENGLAND_WALES, request.getJurisdiction());
    }

    @Test
    void createPublicCompanyV1AcceptsLlpPayloadThatV2Rejects() throws Exception {
        PublicCompanyRequest request = new PublicCompanyRequest();
        request.setCompanyType(CompanyType.LLP);
        request.setNumberOfAppointments(1);
        request.setOfficerRoles(java.util.List.of(OfficerType.LLP_MEMBER));
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createPublicCompany(any(PublicCompanyRequest.class))).thenReturn(company);

        ResponseEntity<CompanyProfileResponse> response = publicCompanyController.createPublicCompanyV1(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(company, response.getBody());
        verify(createCompanyWorkflowService).createPublicCompany(publicSpecCaptor.capture());
        PublicCompanyRequest usedPublicCompanyRequest = publicSpecCaptor.getValue();
        assertEquals(CompanyType.LLP, usedPublicCompanyRequest.getCompanyType());
        assertEquals(1, usedPublicCompanyRequest.getNumberOfAppointments());
        assertEquals(java.util.List.of(OfficerType.LLP_MEMBER), usedPublicCompanyRequest.getOfficerRoles());
    }

    @Test
    void createPublicCompanyV1Exception() throws Exception {
        PublicCompanyRequest request = new PublicCompanyRequest();
        request.setJurisdiction(JurisdictionType.NI);
        DataException exception = new DataException("Error message");
        when(createCompanyWorkflowService.createPublicCompany(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                publicCompanyController.createPublicCompanyV1(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteCompany() throws Exception {
        String companyNumber = "123456";
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode()))
                .thenReturn(true);

        ResponseEntity<Void> response = publicCompanyController.deleteCompany(companyNumber, request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteCompanyWorkflowService).deleteCompany(companyNumber);
    }

    @Test
    void deleteCompanyDataException() throws Exception {
        String companyNumber = "123456";
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode()))
                .thenReturn(true);

        DataException ex = new DataException("Error message");
        doThrow(ex).when(deleteCompanyWorkflowService).deleteCompany(companyNumber);

        DataException thrown = assertThrows(DataException.class,
                () -> publicCompanyController.deleteCompany(companyNumber, request));
        assertEquals(ex, thrown);
    }

    @Test
    void deleteCompanyInvalidAuthCode() throws Exception {
        String companyNumber = "123456";
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode()))
                .thenReturn(false);

        InvalidAuthCodeException thrown = assertThrows(InvalidAuthCodeException.class,
                () -> publicCompanyController.deleteCompany(companyNumber, request));
        assertEquals(companyNumber, thrown.getCompanyNumber());
    }

    @Test
    void deleteCompanyNoAuthCodeFound() throws Exception {
        String companyNumber = "123456";
        DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        NoDataFoundException ex = new NoDataFoundException("no auth code");

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())).thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class,
                () -> publicCompanyController.deleteCompany(companyNumber, request));
        assertEquals(ex, thrown);
    }
}
