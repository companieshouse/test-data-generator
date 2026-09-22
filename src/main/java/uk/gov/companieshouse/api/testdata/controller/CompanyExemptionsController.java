package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyExemptionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyExemptionsResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyExemptionsService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyExemptionsController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final CompanyExemptionsService companyExemptionsService;

    public CompanyExemptionsController(CompanyExemptionsService companyExemptionsService) {
        this.companyExemptionsService = companyExemptionsService;
    }

    @PostMapping("/exemptions")
    public ResponseEntity<CompanyExemptionsResponse> createOrUpdateCompanyExemptions(
            @Valid @RequestBody CompanyExemptionsRequest request) throws DataException {

        var createdExemptions = companyExemptionsService.createOrUpdate(request);
        LOG.info("Company exemptions created or updated for company number: "
                + createdExemptions.getCompanyNumber());
        return new ResponseEntity<>(createdExemptions, HttpStatus.CREATED);
    }

    @GetMapping("/exemptions/{companyNumber}")
    public ResponseEntity<CompanyExemptionsResponse> getCompanyExemptions(
            @PathVariable("companyNumber") String companyNumber) throws NoDataFoundException {
        return ResponseEntity.ok(companyExemptionsService.getByCompanyNumber(companyNumber));
    }

    @DeleteMapping("/exemptions/{companyNumber}")
    public ResponseEntity<Map<String, Object>> deleteCompanyExemptions(
            @PathVariable("companyNumber") String companyNumber) {
        boolean deleted = companyExemptionsService.deleteByCompanyNumber(companyNumber);
        if (deleted) {
            LOG.info("Company exemptions is deleted for company number: " + companyNumber);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("company_number", companyNumber);
            body.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Company exemptions not found for company number: " + companyNumber);
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
    }
}