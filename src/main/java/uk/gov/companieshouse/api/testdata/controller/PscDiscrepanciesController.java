package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
import uk.gov.companieshouse.api.testdata.model.rest.request.PscDiscrepanciesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PscDiscrepanciesResponse;
import uk.gov.companieshouse.api.testdata.service.PscDiscrepanciesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class PscDiscrepanciesController {

    private static final Logger LOG =
            LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private static final String STATUS = "status";

    private final PscDiscrepanciesService pscDiscrepanciesService;

    public PscDiscrepanciesController(
            PscDiscrepanciesService pscDiscrepanciesService) {
        this.pscDiscrepanciesService = pscDiscrepanciesService;
    }

    @PostMapping("/pscdiscrepancies")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PSC endpoint works");
    }

//    @PostMapping("/pscdiscrepancies")
//    public ResponseEntity<PscDiscrepanciesResponse> createPscDiscrepancy(
//            @Valid @RequestBody PscDiscrepanciesRequest request)
//            throws DataException {
//
//        try {
//            var createdPscDiscrepancy =
//                    pscDiscrepanciesService.create(request);
//
//            Map<String, Object> data = new HashMap<>();
//            data.put(
//                    "psc-discrepancies-id",
//                    createdPscDiscrepancy.getPscDiscrepanciesId());
//
//            data.put(
//                    "psc-discrepancy-report-id",
//                    createdPscDiscrepancy.getPscDiscrepancyReportId());
//
//            LOG.info("New PSC discrepancy created", data);
//
//            return new ResponseEntity<>(
//                    createdPscDiscrepancy,
//                    HttpStatus.CREATED);
//
//        } catch (Exception ex) {
//            throw new DataException(
//                    "Error creating PSC discrepancy",
//                    ex);
//        }
//    }

    @DeleteMapping("/pscdiscrepancies/{pscDiscrepanciesId}")
    public ResponseEntity<Map<String, Object>> deletePscDiscrepancy(
            @PathVariable("pscDiscrepanciesId") String pscDiscrepanciesId)
            throws DataException {

        Map<String, Object> response = new HashMap<>();
        response.put("id", pscDiscrepanciesId);

        try {

            boolean deleted =
                    pscDiscrepanciesService.delete(pscDiscrepanciesId);

            if (deleted) {
                LOG.info("PSC discrepancy deleted", response);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("PSC discrepancy not found", response);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);

        } catch (Exception ex) {
            throw new DataException(
                    "Error deleting PSC discrepancy",
                    ex);
        }
    }
}