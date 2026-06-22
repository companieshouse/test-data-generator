package uk.gov.companieshouse.api.testdata.controller;

import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
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

    @PostMapping("/alu/new")
    public ResponseEntity<AppointmentsResultResponse> createAppointmentDummy(
            @Valid @RequestBody AppointmentCreationRequest request) throws DataException {
        return new ResponseEntity<>( HttpStatus.CREATED);

    }

    @GetMapping("/alu/newpath")
    public ResponseEntity<AppointmentsResultResponse> getAppointmentDummy(
            @Valid @RequestBody AppointmentCreationRequest request) throws DataException {
        return new ResponseEntity<>( HttpStatus.OK);

    }
}
