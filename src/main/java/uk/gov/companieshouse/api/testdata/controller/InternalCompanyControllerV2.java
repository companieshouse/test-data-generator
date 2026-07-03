package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles internal v2 company endpoints.
 */
@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class InternalCompanyControllerV2 {

    private static final String API_VERSION_HEADER = "X-API-Version";
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String COMPANY_NUMBER_DATA = "company number";
    private static final String JURISDICTION_DATA = "jurisdiction";
    private static final String NEW_COMPANY_CREATED = "New company created";

    private final CreateCompanyWorkflowService createCompanyWorkflowService;

    public InternalCompanyControllerV2(CreateCompanyWorkflowService createCompanyWorkflowService) {
        this.createCompanyWorkflowService = createCompanyWorkflowService;
    }

    @PostMapping(value = "/company", headers = API_VERSION_HEADER + "=2")
    public ResponseEntity<CompanyProfileResponse> createInternalCompanyV2(
            @Valid @RequestBody(required = false) InternalCompanyRequestV2 request) throws DataException {
        LOG.info("Received request to create a new company (v2) in internal company API");
        InternalCompanyRequest internalCompanyRequest = request == null
                ? new InternalCompanyRequest()
                : request.toInternalCompanyRequest();
        return createCompanyResponse(internalCompanyRequest);
    }

    @PostMapping(value = "/get-populated-company-structure", headers = API_VERSION_HEADER + "=2")
    public ResponseEntity<PopulatedCompanyDetailsResponse> buildCompanyDataStructureV2(
            @Valid @RequestBody(required = false) InternalCompanyRequestV2 request) throws DataException {

        LOG.info("Received request to build a populated company data structure (v2) in internal company API");

        InternalCompanyRequest internalCompanyRequest = request == null
                ? new InternalCompanyRequest()
                : request.toInternalCompanyRequest();

        var companyData = createCompanyWorkflowService.buildCompanyDataStructure(internalCompanyRequest);
        return new ResponseEntity<>(companyData, HttpStatus.OK);
    }

    private ResponseEntity<CompanyProfileResponse> createCompanyResponse(InternalCompanyRequest request)
            throws DataException {
        CompanyProfileResponse createdCompany = createCompanyWorkflowService.createInternalCompany(request);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, request.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }
}
