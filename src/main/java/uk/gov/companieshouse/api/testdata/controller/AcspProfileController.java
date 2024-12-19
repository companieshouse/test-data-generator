package uk.gov.companieshouse.api.testdata.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.service.AcspTestDataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcspProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private AcspTestDataService acspTestDataService;

    @PostMapping("/acsp")
    public ResponseEntity<AcspData> create(@Valid @RequestBody(required = false) AcspSpec request) throws DataException {

        // Optional<AcspSpec> optionalRequest = Optional.ofNullable(request);
        AcspSpec spec = new AcspSpec();


        AcspData createdAcsp = acspTestDataService.createAcspData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp number", createdAcsp.getAcspNumber());
        LOG.info("New acsp profile created", data);
        return new ResponseEntity<>(createdAcsp, HttpStatus.CREATED);
    }

    @DeleteMapping("/acsp/{acspNumber}")
    public ResponseEntity<Void> delete(@PathVariable("acspNumber") long acspNumber)
            throws DataException, NoDataFoundException {

        acspTestDataService.deleteAcspData(acspNumber);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp number", acspNumber);
        LOG.info("Acsp Profile deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}