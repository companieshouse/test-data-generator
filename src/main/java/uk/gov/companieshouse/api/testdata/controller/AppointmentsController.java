package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentsController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final AppointmentService appointmentService;

    public AppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentsResultResponse> createAppointments(
            @Valid @RequestBody AppointmentCreationRequest request) throws DataException {

        if (request.getCompanyNumber() == null) {
            throw new DataException("Company Number is required to create appointments");
        }

        try {
            var createdAppointments = appointmentService.createAppointment(request);

            Map<String, Object> data = new HashMap<>();
            data.put("Appointment-id", createdAppointments.getAppointment().getFirst().getId());
            LOG.info("New appointments added", data);
            return new ResponseEntity<>(createdAppointments, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new DataException("Error creating appointments", ex);
        }
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
