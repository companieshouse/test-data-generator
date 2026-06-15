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
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.service.AcspWorkflowService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;


@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcspMemberController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final AcspWorkflowService acspWorkflowService;

    public AcspMemberController(AcspWorkflowService acspWorkflowService) {
        this.acspWorkflowService = acspWorkflowService;
    }

    @PostMapping("/acsp-members")
    public ResponseEntity<AcspMembersResponse> createAcspMember(
            @Valid @RequestBody AcspMembersRequest request) throws DataException {

        var createdAcspMember = acspWorkflowService.createAcspMember(request);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp-member-id", createdAcspMember.getAcspMemberId());
        LOG.info("New acsp member created", data);
        return new ResponseEntity<>(createdAcspMember, HttpStatus.CREATED);
    }

    @DeleteMapping("/acsp-members/{acspMemberId}")
    public ResponseEntity<Map<String, Object>> deleteAcspMember(
            @PathVariable String acspMemberId)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("acsp-member-id", acspMemberId);
        boolean deleteAcspMember = acspWorkflowService.deleteAcspMember(acspMemberId);

        if (deleteAcspMember) {
            LOG.info("Acsp Member Deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Acsp Member Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
