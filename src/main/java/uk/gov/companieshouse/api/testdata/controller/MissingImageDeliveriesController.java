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
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.service.MissingImageDeliveriesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class MissingImageDeliveriesController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final MissingImageDeliveriesService missingImageDeliveriesService;

    public MissingImageDeliveriesController(
            MissingImageDeliveriesService missingImageDeliveriesService) {
        this.missingImageDeliveriesService = missingImageDeliveriesService;
    }

    @PostMapping("/internal/missing-image-deliveries")
    public ResponseEntity<CertificatesResponse> createMissingImageDeliveries(
            @Valid @RequestBody MissingImageDeliveriesRequest request) throws DataException {

        if (request.getUserId() == null) {
            throw new DataException("User ID is required to create missing image deliveries");
        }

        try {
            var createdMissingImageDeliveries = missingImageDeliveriesService.create(request);

            Map<String, Object> data = new HashMap<>();
            data.put("missing-image-deliveries-id",
                    createdMissingImageDeliveries.getCertificates().getFirst().getId());
            LOG.info("New missing image deliveries added", data);
            return new ResponseEntity<>(createdMissingImageDeliveries, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new DataException("Error creating missing image deliveries", ex);
        }
    }

    @DeleteMapping("/internal/missing-image-deliveries/{id}")
    public ResponseEntity<Map<String, Object>> deleteMissingImageDeliveries(@PathVariable("id") String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);

        try {
            boolean deleteMissingImageDeliveries = missingImageDeliveriesService.delete(id);

            if (deleteMissingImageDeliveries) {
                LOG.info("Missing Image Deliveries is deleted", response);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                response.put(STATUS, HttpStatus.NOT_FOUND);
                LOG.info("Missing Image Deliveries Not Found", response);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            throw new DataException("Error deleting missing image deliveries", ex);
        }
    }
}
