package uk.gov.companieshouse.api.testdata.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteAcspRequest;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcspProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private AcspProfileService acspProfileService;

    @PostMapping("/acsp")
    public ResponseEntity<AcspProfileData> create(@Valid @RequestBody(required = false) AcspProfileSpec request)
            throws DataException {

        Optional<AcspProfileSpec> optionalRequest = Optional.ofNullable(request);
        AcspProfileSpec spec = optionalRequest.orElse(new AcspProfileSpec());

        AcspProfileData createdAcsp = acspProfileService.createAcspData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp_number", createdAcsp.getAcspNumber());
        LOG.info("New ACSP created", data);
        return new ResponseEntity<>(createdAcsp, HttpStatus.CREATED);
    }

    @DeleteMapping("/acsp/{acspNumber}")
    public ResponseEntity<Void> delete(@PathVariable("acspNumber") String acspNumber,
                                       @Valid @RequestBody DeleteAcspRequest request)
            throws DataException, NoDataFoundException {

        // add authentication checks similar to the companyAuthCodeService above
        // possibly use request.getAuthCode() similarly if needed.

        acspProfileService.deleteAcspData(acspNumber);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp_number", acspNumber);
        LOG.info("ACSP deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
