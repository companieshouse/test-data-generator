package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles public v2 company creation endpoint.
 */
@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicCompanyControllerV2 {

    private static final String API_VERSION_HEADER = "X-API-Version";
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String COMPANY_NUMBER_DATA = "company number";
    private static final String JURISDICTION_DATA = "jurisdiction";
    private static final String NEW_COMPANY_CREATED = "New company created";

    private final CreateCompanyWorkflowService createCompanyWorkflowService;
    private final Validator validator;

    public PublicCompanyControllerV2(CreateCompanyWorkflowService createCompanyWorkflowService,
                                     Validator validator) {
        this.createCompanyWorkflowService = createCompanyWorkflowService;
        this.validator = validator;
    }

    /**
     * V2 endpoint: Handles PublicCompanyRequestV2 format.
     * INTERNAL ONLY - temporarily hosted at /internal/v2/company to avoid clashing
     * with InternalCompanyController's existing /internal/company?X-API-Version=2 mapping.
     * Requires X-API-Version: 2 so clients/tests keep the same version-header behavior
     * that will be used when this route moves to /company.
     * Added to internal path to prevent access from external clients until the new endpoint is
     * fully implemented and tested.
     * When V2 schema migration is complete this will move to the public /company path.
     * See: taf-api-karate/.../company_test_data/public/v2/README.md for full rationale.
     */
    @PostMapping(value = "/internal/v2/company", headers = API_VERSION_HEADER + "=2")
    public ResponseEntity<CompanyProfileResponse> createPublicCompanyV2(
            @RequestBody(required = false) PublicCompanyRequestV2 request) throws DataException {

        LOG.info("Received request to create a new company (v2) in public company API");

        PublicCompanyRequestV2 publicCompanyRequestV2 = request == null ? new PublicCompanyRequestV2() : request;

        validateV2(publicCompanyRequestV2);
        PublicCompanyRequest publicCompanyRequest = publicCompanyRequestV2.toPublicCompanyRequest();

        var createdCompany = createCompanyWorkflowService.createPublicCompany(publicCompanyRequest);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, publicCompanyRequest.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    private void validateV2(PublicCompanyRequestV2 publicCompanyRequestV2) {
        Set<ConstraintViolation<PublicCompanyRequestV2>> violations = validator.validate(publicCompanyRequestV2);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
