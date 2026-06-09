package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles public-facing company endpoints.
 * Company creation does not require an auth code, while company deletion requires
 * a valid auth code in the request body.
 */
@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicCompanyController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private final CreateCompanyWorkflowService createCompanyWorkflowService;
    private final DeleteCompanyService deleteCompanyService;
    private final CompanyAuthCodeService companyAuthCodeService;

    private static final String COMPANY_NUMBER_DATA = "company number";
    private static final String JURISDICTION_DATA = "jurisdiction";
    private static final String NEW_COMPANY_CREATED = "New company created";

    public PublicCompanyController(
            CreateCompanyWorkflowService createCompanyWorkflowService,
            DeleteCompanyService deleteCompanyService,
            CompanyAuthCodeService companyAuthCodeService) {
        this.createCompanyWorkflowService = createCompanyWorkflowService;
        this.deleteCompanyService = deleteCompanyService;
        this.companyAuthCodeService = companyAuthCodeService;
    }

    @PostMapping("/company")
    public ResponseEntity<CompanyProfileResponse> createCompany(
            @Valid @RequestBody(required = false) PublicCompanyRequest request) throws DataException {

        Optional<PublicCompanyRequest> optionalRequest = Optional.ofNullable(request);
        PublicCompanyRequest spec = optionalRequest.orElse(new PublicCompanyRequest());

        var createdCompany = createCompanyWorkflowService.createPublicCompany(spec);

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

        deleteCompanyService.deleteCompany(companyNumber);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, companyNumber);
        LOG.info("Company deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

