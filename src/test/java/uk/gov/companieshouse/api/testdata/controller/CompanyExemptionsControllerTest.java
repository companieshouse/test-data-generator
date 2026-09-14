package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyExemptionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyExemptionsResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyExemptionsService;

@ExtendWith(MockitoExtension.class)
class CompanyExemptionsControllerTest {

    private static final String COMPANY_NUMBER = "AC123456";

    @Mock
    private CompanyExemptionsService companyExemptionsService;

    @InjectMocks
    private CompanyExemptionsController companyExemptionsController;

    @Test
    void createOrUpdateCompanyExemptions() throws DataException {
        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);
        request.setExemptionType("psc_exempt_as_trading_on_regulated_market");

        CompanyExemptionsResponse responseBody = new CompanyExemptionsResponse();
        responseBody.setCompanyNumber(COMPANY_NUMBER);
        responseBody.setDeltaAt("2026-08-19T11:00:00Z");
        responseBody.setCreatedAt(Instant.parse("2026-08-19T11:00:00Z"));
        responseBody.setUpdatedAt(Instant.parse("2026-08-19T11:00:00Z"));

        when(companyExemptionsService.createOrUpdate(request)).thenReturn(responseBody);

        ResponseEntity<CompanyExemptionsResponse> response =
                companyExemptionsController.createOrUpdateCompanyExemptions(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
        verify(companyExemptionsService, times(1)).createOrUpdate(request);
    }
}