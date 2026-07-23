package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalCompanyControllerV2Test {

    private static final String COMPANY_URI = "http://localhost:1234/company/12345678";

    @Mock
    private CreateCompanyWorkflowService createCompanyWorkflowService;

    private InternalCompanyControllerV2 internalCompanyControllerV2;

    @Captor
    private ArgumentCaptor<InternalCompanyRequest> internalCompanyRequestCaptor;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        internalCompanyControllerV2 = new InternalCompanyControllerV2(createCompanyWorkflowService);
    }

    @Test
    void createInternalCompanyV2() throws Exception {
        InternalCompanyRequestV2 request = new InternalCompanyRequestV2();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        InternalCompanyRequestV2.CompanyTypeV2 companyTypeV2 = new InternalCompanyRequestV2.CompanyTypeV2();
        companyTypeV2.setSubType("community-interest-company");
        request.setCompanyTypeDetails(companyTypeV2);
        CompanyProfileResponse company = new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(createCompanyWorkflowService.createInternalCompany(any())).thenReturn(company);

        ResponseEntity<CompanyProfileResponse> response = internalCompanyControllerV2.createInternalCompanyV2(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(createCompanyWorkflowService).createInternalCompany(internalCompanyRequestCaptor.capture());
        assertEquals("community-interest-company", internalCompanyRequestCaptor.getValue().getSubType());
    }

    @Test
    void createInternalCompanyV2NullRequestUsesDefault() throws Exception {
        CompanyProfileResponse company = new CompanyProfileResponse("12345678", "123456", COMPANY_URI);
        when(createCompanyWorkflowService.createInternalCompany(any())).thenReturn(company);

        ResponseEntity<CompanyProfileResponse> response = internalCompanyControllerV2.createInternalCompanyV2(null);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(createCompanyWorkflowService).createInternalCompany(internalCompanyRequestCaptor.capture());
        assertEquals(JurisdictionType.ENGLAND_WALES, internalCompanyRequestCaptor.getValue().getJurisdiction());
    }

    @Test
    void buildCompanyDataStructureV2Success() throws Exception {
        InternalCompanyRequestV2 request = new InternalCompanyRequestV2();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        InternalCompanyRequestV2.CompanyTypeV2 companyTypeV2 = new InternalCompanyRequestV2.CompanyTypeV2();
        companyTypeV2.setSubType("community-interest-company");
        request.setCompanyTypeDetails(companyTypeV2);

        PopulatedCompanyDetailsResponse responseObj = new PopulatedCompanyDetailsResponse();
        when(createCompanyWorkflowService.buildCompanyDataStructure(any(InternalCompanyRequest.class)))
                .thenReturn(responseObj);

        ResponseEntity<PopulatedCompanyDetailsResponse> response =
                internalCompanyControllerV2.buildCompanyDataStructureV2(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseObj, response.getBody());
        verify(createCompanyWorkflowService).buildCompanyDataStructure(internalCompanyRequestCaptor.capture());
        assertEquals("community-interest-company", internalCompanyRequestCaptor.getValue().getSubType());
    }

    @Test
    void buildCompanyDataStructureV2NullRequestUsesDefault() throws Exception {
        PopulatedCompanyDetailsResponse responseObj = new PopulatedCompanyDetailsResponse();
        when(createCompanyWorkflowService.buildCompanyDataStructure(any(InternalCompanyRequest.class)))
                .thenReturn(responseObj);

        ResponseEntity<PopulatedCompanyDetailsResponse> response =
                internalCompanyControllerV2.buildCompanyDataStructureV2(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseObj, response.getBody());
        verify(createCompanyWorkflowService).buildCompanyDataStructure(internalCompanyRequestCaptor.capture());
        assertEquals(JurisdictionType.ENGLAND_WALES, internalCompanyRequestCaptor.getValue().getJurisdiction());
    }

    @Test
    void buildCompanyDataStructureV2ThrowsDataException() throws Exception {
        InternalCompanyRequestV2 request = new InternalCompanyRequestV2();
        DataException exception = new DataException("error");
        when(createCompanyWorkflowService.buildCompanyDataStructure(any(InternalCompanyRequest.class)))
                .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                internalCompanyControllerV2.buildCompanyDataStructureV2(request));
        assertEquals(exception, thrown);
    }
}
