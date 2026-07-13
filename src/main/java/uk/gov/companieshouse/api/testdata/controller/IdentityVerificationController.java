package uk.gov.companieshouse.api.testdata.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityVerificationController {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";
    private static final String USER_NOT_FOUND = "User not found";

    private final VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService;

    public IdentityVerificationController(VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService) {
        this.verifiedIdentityService = verifiedIdentityService;
    }

    @GetMapping("/identity/verification")
    public ResponseEntity<IdentityVerificationResponse> getIdentityVerification(
            @RequestParam("email") String email)
            throws DataException, NoDataFoundException {

        var data = verifiedIdentityService.getIdentityVerificationData(email);
        if (data == null) {
            throw new NoDataFoundException("No identity verification found for email: " + email);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @DeleteMapping(value = "/identity/verification", params = {"identityemail", "userid"})
    public ResponseEntity<Map<String, Object>> deleteIdentity(  @RequestParam("identityemail") String identityEmail, @RequestParam("userid") String userId )
            throws DataException, NoDataFoundException {
        Map<String, Object> response = new HashMap<>();
        response.put("Identity Email", identityEmail);

        IdentityVerificationResponse identity = verifiedIdentityService.getIdentityVerificationData(identityEmail);

        if (identity == null) {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info(USER_NOT_FOUND, response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        boolean deleteIdentity = verifiedIdentityService.deleteIdentityData(identity, userId);
        if (deleteIdentity) {
            LOG.info("Identity, Uvid and Backlog deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        response.put(STATUS, HttpStatus.NOT_FOUND);
        LOG.info(USER_NOT_FOUND, response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
