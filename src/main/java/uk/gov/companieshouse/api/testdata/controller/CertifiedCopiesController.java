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
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.service.CertifiedCopiesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertifiedCopiesController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final CertifiedCopiesService certifiedCopiesService;

    public CertifiedCopiesController(CertifiedCopiesService certifiedCopiesService) {
        this.certifiedCopiesService = certifiedCopiesService;
    }

    @PostMapping("/internal/certified-copies")
    public ResponseEntity<CertificatesResponse> createCertifiedCopies(
            @Valid @RequestBody CertifiedCopiesRequest request) throws DataException {

        if (request.getUserId() == null) {
            throw new DataException("User ID is required to create certified copies");
        }

        try {
            var createdCertifiedCopies = certifiedCopiesService.create(request);

            Map<String, Object> data = new HashMap<>();
            data.put("certificates-copies-id",
                    createdCertifiedCopies.getCertificates().getFirst().getId());
            LOG.info("New certified copies added", data);
            return new ResponseEntity<>(createdCertifiedCopies, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new DataException("Error creating certified copies", ex);
        }
    }

    @DeleteMapping("/internal/certified-copies/{id}")
    public ResponseEntity<Map<String, Object>> deleteCertifiedCopies(@PathVariable("id") String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);

        try {
            boolean deleteCertifiedCopies = certifiedCopiesService.delete(id);

            if (deleteCertifiedCopies) {
                LOG.info("Certified Copies is deleted", response);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                response.put(STATUS, HttpStatus.NOT_FOUND);
                LOG.info("Certified Copies Not Found", response);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            throw new DataException("Error deleting certified copies", ex);
        }
    }
}
