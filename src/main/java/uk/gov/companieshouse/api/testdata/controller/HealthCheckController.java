package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * HealthCheck returns a 200 response if the service is running.
 */
@Controller
@RequestMapping(value = "${api.endpoint}")
public class HealthCheckController {

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> isHealthy() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
