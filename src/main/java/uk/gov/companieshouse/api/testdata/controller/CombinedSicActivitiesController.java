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
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.service.CombinedSicActivitiesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class CombinedSicActivitiesController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final CombinedSicActivitiesService combinedSicActivitiesService;

    public CombinedSicActivitiesController(CombinedSicActivitiesService
                                                   combinedSicActivitiesService) {
        this.combinedSicActivitiesService = combinedSicActivitiesService;
    }

    @PostMapping("/combined-sic-activities")
    public ResponseEntity<CombinedSicActivitiesResponse> createCombinedSicActivities(
            @Valid @RequestBody CombinedSicActivitiesRequest request) throws DataException {

        var createdSicCodeKeyword = combinedSicActivitiesService.create(request);

        Map<String, Object> data = Map.of("sic-code-keyword-id", createdSicCodeKeyword.getId());
        LOG.info("New sic code keyword added", new HashMap<>(data));
        return new ResponseEntity<>(createdSicCodeKeyword, HttpStatus.CREATED);
    }

    @DeleteMapping("/combined-sic-activities/{id}")
    public ResponseEntity<Map<String, Object>> deleteCombinedSicActivities(
            @PathVariable("id") String id) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("id", id);
        boolean deleteCombinedSicActivities = combinedSicActivitiesService.delete(id);

        if (deleteCombinedSicActivities) {
            LOG.info("Combined Sic Activities is deleted", logMap);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logMap.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Combined Sic Activities Not Found", logMap);
            return new ResponseEntity<>(logMap, HttpStatus.NOT_FOUND);
        }
    }
}
