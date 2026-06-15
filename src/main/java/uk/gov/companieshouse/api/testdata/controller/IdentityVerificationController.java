package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityVerificationController {
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
}
