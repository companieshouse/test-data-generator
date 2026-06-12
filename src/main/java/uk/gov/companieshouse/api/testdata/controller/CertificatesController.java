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
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.service.CertificatesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificatesController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final CertificatesService certificatesService;

    public CertificatesController(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }

    @PostMapping("/internal/certificates")
    public ResponseEntity<CertificatesResponse> createCertificates(
            @Valid @RequestBody CertificatesRequest request) throws DataException {

        if (request.getUserId() == null) {
            throw new DataException("User ID is required to create certificates");
        }

        try {
            var createdCertificates = certificatesService.create(request);

            Map<String, Object> data = new HashMap<>();
            data.put("certificated-id", createdCertificates.getCertificates().getFirst().getId());
            LOG.info("New certificates added", data);
            return new ResponseEntity<>(createdCertificates, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new DataException("Error creating certificates", ex);
        }
    }

    @DeleteMapping("/internal/certificates/{id}")
    public ResponseEntity<Map<String, Object>> deleteCertificates(@PathVariable("id") String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);

        try {
            boolean deleteCertificates = certificatesService.delete(id);

            if (deleteCertificates) {
                LOG.info("Certificate is deleted", response);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                response.put(STATUS, HttpStatus.NOT_FOUND);
                LOG.info("Certificate Not Found", response);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            throw new DataException("Error deleting certificates", ex);
        }
    }
}
