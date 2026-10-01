package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
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
    public ResponseEntity<IdentityVerificationResponse> createIdentity(
            @Valid @RequestBody IdentityVerificationRequest request) {

        return new ResponseEntity<>(
                identityService.createIdentity(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/identity/{id}")
    public ResponseEntity<?> getIdentity(
            @PathVariable String id) {

        var identity = identityService.getIdentity(id);

        if (identity == null) {

            Map<String, Object> response = new HashMap<>();
            response.put("identity id", id);
            response.put("status", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(identity);
    }

    @DeleteMapping("/identity/{id}")
    public ResponseEntity<Map<String, Object>> deleteIdentity(
            @PathVariable String id) {

        Map<String, Object> response = new HashMap<>();
        response.put("identity id", id);

        boolean deleted = identityService.deleteIdentity(id);

        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        response.put("status", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}