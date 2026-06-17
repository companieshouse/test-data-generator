package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowServiceV2;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyWorkflowService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles public-facing company endpoints.
 * Company creation does not require an auth code, while company deletion requires
 * a valid auth code in the request body.
 */
@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicCompanyController {

    private static final String API_VERSION_HEADER = "X-API-Version";

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private final CreateCompanyWorkflowService createCompanyWorkflowService;
    private final CreateCompanyWorkflowServiceV2 createCompanyWorkflowServiceV2;
    private final DeleteCompanyWorkflowService deleteCompanyWorkflowService;
    private final CompanyAuthCodeService companyAuthCodeService;
    private final Validator validator;

    private static final String COMPANY_NUMBER_DATA = "company number";
    private static final String JURISDICTION_DATA = "jurisdiction";
    private static final String NEW_COMPANY_CREATED = "New company created";

    public PublicCompanyController(
            CreateCompanyWorkflowService createCompanyWorkflowService,
            CreateCompanyWorkflowServiceV2 createCompanyWorkflowServiceV2,
            DeleteCompanyWorkflowService deleteCompanyWorkflowService,
            CompanyAuthCodeService companyAuthCodeService,
            Validator validator) {
        this.createCompanyWorkflowService = createCompanyWorkflowService;
        this.createCompanyWorkflowServiceV2 = createCompanyWorkflowServiceV2;
        this.deleteCompanyWorkflowService = deleteCompanyWorkflowService;
        this.companyAuthCodeService = companyAuthCodeService;
        this.validator = validator;
    }

    /**
     * V1 endpoint: Handles legacy PublicCompanyRequest format (flat company_type field).
     * This route is the default for /company and is used when no version header is provided.
     */
    @PostMapping("/company")
    public ResponseEntity<CompanyProfileResponse> createCompanyV1(
            @Valid @RequestBody(required = false) PublicCompanyRequest request) throws DataException {
        PublicCompanyRequest spec = request == null ? new PublicCompanyRequest() : request;

        var createdCompany = createCompanyWorkflowService.createPublicCompany(spec);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, spec.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    /**
     * V2 endpoint: Handles PublicCompanyRequestV2 format (nested company_type)
     * Routed via header: X-API-Version: 2
     */
    @PostMapping(value = "/company", headers = API_VERSION_HEADER + "=2")
    public ResponseEntity<CompanyProfileResponse> createCompanyV2(
            @RequestBody(required = false) PublicCompanyRequestV2 request) throws DataException {

        PublicCompanyRequestV2 spec = request == null ? new PublicCompanyRequestV2() : request;

        validateV2(spec);

        var createdCompany = createCompanyWorkflowServiceV2.createPublicCompanyV2(spec);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, spec.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @DeleteMapping({"/company/{companyNumber}"})
    public ResponseEntity<Void> deleteCompany(
            @PathVariable("companyNumber") String companyNumber,
            @Valid @RequestBody DeleteCompanyRequest request)
            throws DataException, InvalidAuthCodeException, NoDataFoundException {

        if (!companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())) {
            throw new InvalidAuthCodeException(companyNumber);
        }

        deleteCompanyWorkflowService.deleteCompany(companyNumber);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, companyNumber);
        LOG.info("Company deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private void validateV2(PublicCompanyRequestV2 spec) {
        Set<ConstraintViolation<PublicCompanyRequestV2>> violations = validator.validate(spec);
        if (!violations.isEmpty()) {
            String errorMessage = violations.iterator().next().getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }

}

