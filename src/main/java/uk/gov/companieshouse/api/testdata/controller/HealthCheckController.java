package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * HealthCheck returns a 200 response if the service is running.
 */
@Controller
@RequestMapping("/healthcheck")
public class HealthCheckController {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Void> isHealthy() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
