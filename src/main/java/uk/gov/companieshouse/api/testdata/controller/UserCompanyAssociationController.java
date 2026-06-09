package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.bson.types.ObjectId;
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
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.service.UserCompanyAssociationService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserCompanyAssociationController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final UserCompanyAssociationService userCompanyAssociationService;

    public UserCompanyAssociationController(UserCompanyAssociationService userCompanyAssociationService) {
        this.userCompanyAssociationService = userCompanyAssociationService;
    }

    @PostMapping("/associations")
    public ResponseEntity<UserCompanyAssociationResponse> createAssociation(
            @Valid @RequestBody UserCompanyAssociationRequest request) throws DataException {
        if (request.getUserId() == null && request.getUserEmail() == null) {
            throw new DataException("A user_id or a user_email is required to create an association");
        }

        if (request.getCompanyNumber() == null || request.getCompanyNumber().isEmpty()) {
            throw new DataException("Company number is required to create an association");
        }

        try {
            UserCompanyAssociationResponse createdAssociation = userCompanyAssociationService.create(request);
            UserCompanyAssociationResponse response = new UserCompanyAssociationResponse(
                    new ObjectId(createdAssociation.getId()),
                    createdAssociation.getCompanyNumber(),
                    createdAssociation.getUserId(),
                    createdAssociation.getUserEmail(),
                    createdAssociation.getStatus(),
                    createdAssociation.getApprovalRoute(),
                    createdAssociation.getInvitations()
            );

            Map<String, Object> data = new HashMap<>();
            data.put("association_id", response.getId());
            LOG.info("New association created", data);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new DataException("Error creating the association", ex);
        }
    }

    @DeleteMapping("/associations/{associationId}")
    public ResponseEntity<Map<String, Object>> deleteAssociation(
            @PathVariable("associationId") String associationId) throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("association_id", associationId);
        boolean deleteAssociation;
        try {
            deleteAssociation = userCompanyAssociationService.delete(associationId);
        } catch (Exception ex) {
            throw new DataException("Error deleting association", ex);
        }

        if (deleteAssociation) {
            LOG.info("Association is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        response.put(STATUS, HttpStatus.NOT_FOUND);
        LOG.info("Association is not found", response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
