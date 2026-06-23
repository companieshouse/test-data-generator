package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppointmentController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;

    }

    @PostMapping("/appointment")
    public ResponseEntity<AppointmentsResultResponse> createAppointment(
            @Valid @RequestBody AppointmentCreationRequest request) throws DataException {

        var createAppointmentFromRequest = appointmentService.createAppointmentFromRequest(request);

        Map<String, Object> data = new HashMap<>();
        data.put("appointment-id", createAppointmentFromRequest.getAppointmentId());
        data.put("officer-id", createAppointmentFromRequest.getOfficerId());
        LOG.info("New officer appointment created", data);
        return new ResponseEntity<>(createAppointmentFromRequest, HttpStatus.CREATED);

    }
    @PostMapping("/appointments")
    public ResponseEntity<List<AppointmentsResultResponse>> createListOfAppointments(
            @RequestParam int count,
            @RequestBody AppointmentCreationRequest request) throws DataException {
        List<AppointmentsResultResponse> responses = appointmentService.createAppointments(request,count);
        LOG.info("Created list of appointments: "+ responses.size());
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }
}
