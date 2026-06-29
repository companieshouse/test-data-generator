package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentsController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final AppointmentService appointmentService;

    public AppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @DeleteMapping("/company/{companyNumber}/appointments")
    public ResponseEntity<Map<String, Object>> deleteAppointments(@PathVariable String companyNumber) {
        Map<String, Object> response = new HashMap<>();
        response.put("company-number", companyNumber);
        boolean deleteAppointments = appointmentService.deleteAllAppointments(companyNumber);
        if (deleteAppointments) {
            LOG.info("Appointments deleted for company", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("No Appointments found for company", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}