package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.BacklogRequest;
import uk.gov.companieshouse.api.testdata.service.BacklogService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(
        value = "${api.endpoint}/internal",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BacklogController {

    private static final Logger LOG =
            LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private final BacklogService backlogService;

    public BacklogController(BacklogService backlogService) {
        this.backlogService = backlogService;
    }

    @PutMapping("/identity/verification/backlog/{backlogId}")
    public ResponseEntity<Void> updateBacklogCaseId(
            @PathVariable String backlogId,
            @RequestBody BacklogRequest backlogRequest)
        throws NoDataFoundException, DataException {
        backlogService.updateBacklogCaseId(backlogId, backlogRequest.getCaseId());
        return ResponseEntity.ok().build();
    }
}