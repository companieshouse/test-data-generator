package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicCompanyControllerV2Test {

    private static final String COMPANY_URI = "http://localhost:1234/company/12345678";

    @Mock
    private CreateCompanyWorkflowService createCompanyWorkflowService;

    @Mock
    private Validator validator;

    private PublicCompanyControllerV2 publicCompanyControllerV2;

    @BeforeEach
    void setUp() {
        publicCompanyControllerV2 = new PublicCompanyControllerV2(
                createCompanyWorkflowService,
                validator);
    }

    @Test
    void createPublicCompanyV2() throws Exception {
        PublicCompanyRequestV2 request = new PublicCompanyRequestV2();
        request.setJurisdiction(JurisdictionType.NI);
        request.setNumberOfUkEstablishments(3);
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", COMPANY_URI);

        when(validator.validate(any(PublicCompanyRequestV2.class))).thenReturn(Collections.emptySet());
        when(createCompanyWorkflowService.createPublicCompany(any(PublicCompanyRequest.class))).thenReturn(company);

        ResponseEntity<CompanyProfileResponse> response = publicCompanyControllerV2.createPublicCompanyV2(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        var captor = org.mockito.ArgumentCaptor.forClass(PublicCompanyRequest.class);
        verify(createCompanyWorkflowService).createPublicCompany(captor.capture());
        PublicCompanyRequest passed = captor.getValue();
        assertEquals(3, passed.getNumberOfUkEstablishments().intValue());
    }

    @Test
    void createPublicCompanyV2ValidationFailure() {
        PublicCompanyRequestV2 request = new PublicCompanyRequestV2();
        @SuppressWarnings("unchecked")
        ConstraintViolation<PublicCompanyRequestV2> violation = mock(ConstraintViolation.class);

        when(validator.validate(any(PublicCompanyRequestV2.class))).thenReturn(Set.of(violation));
        when(violation.getMessage()).thenReturn("Invalid company status");

        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> publicCompanyControllerV2.createPublicCompanyV2(request));
    }
}
