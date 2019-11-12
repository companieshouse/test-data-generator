package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HealthCheckControllerTest {

    private HealthCheckController controller = new HealthCheckController();

    @Test
    void isHealthy() throws Exception {
        ResponseEntity<Void> response = controller.isHealthy();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}