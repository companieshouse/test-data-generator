package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppealController {
    private final AppealsService appealsService;
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    public AppealController(AppealsService appealsService) {
        this.appealsService = appealsService;
    }

    @DeleteMapping("/appeals")
    public ResponseEntity<Void> deleteAppeal(
            @Valid @RequestBody DeleteAppealsRequest request) {

        if (request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean isDeleted = appealsService
                .delete(request.getCompanyNumber(), request.getPenaltyReference());

        if (isDeleted) {
            LOG.info("Appeals data deleted for company number: " + request.getCompanyNumber()
                    + " and penalty reference: " + request.getPenaltyReference());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            LOG.info("No appeals data found for company number: " + request.getCompanyNumber()
                    + " and penalty reference: " + request.getPenaltyReference());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
