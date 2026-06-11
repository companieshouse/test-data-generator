package uk.gov.companieshouse.api.testdata.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcspProfileController {

    private final AcspProfileService acspProfileService;

    public AcspProfileController(AcspProfileService acspProfileService) {
        this.acspProfileService = acspProfileService;
    }

    @GetMapping("/acsp-profile/{acspNumber}")
    public ResponseEntity<Optional<AcspProfile>> getAcspProfile(
            @PathVariable String acspNumber) throws NoDataFoundException {

        Optional<AcspProfile> acspProfile =
                acspProfileService.getAcspProfile(acspNumber);

        return new ResponseEntity<>(acspProfile, HttpStatus.OK);
    }
}
