package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityResponse;
import uk.gov.companieshouse.api.testdata.service.IdentityService;

@RestController
@RequestMapping(
        value = "${api.endpoint}/internal",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/identity")
    public ResponseEntity<IdentityResponse> createIdentity(
            @Valid @RequestBody IdentityVerificationRequest request) {

        return new ResponseEntity<>(
                identityService.createIdentity(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/identity/{id}")
    public ResponseEntity<IdentityResponse> getIdentity(
            @PathVariable String id) {

        var identity = identityService.getIdentity(id);

        if (identity == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(identity);
    }

    @DeleteMapping("/identity/{id}")
    public ResponseEntity<Void> deleteIdentity(
            @PathVariable String id) {

        boolean deleted = identityService.deleteIdentity(id);

        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}