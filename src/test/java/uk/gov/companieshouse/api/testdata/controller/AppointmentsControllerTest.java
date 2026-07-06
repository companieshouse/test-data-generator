package uk.gov.companieshouse.api.testdata.controller;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AppointmentsControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentsController appointmentsController;

    @Test
    void createAppointmentsSuccess() throws DataException {
        AppointmentCreationRequest request = AppointmentCreationRequest.builder()
                .companyNumber("12345678")
                .build();

        Appointment appointment = new Appointment();
        appointment.setId("APPOINTMENT_ID");

        AppointmentsResultResponse serviceResponse = new AppointmentsResultResponse();
        serviceResponse.setAppointment(List.of(appointment));

        when(appointmentService.createAppointment(request)).thenReturn(serviceResponse);

        ResponseEntity<AppointmentsResultResponse> response =
                appointmentsController.createAppointments(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("APPOINTMENT_ID",
                response.getBody().getAppointment().getFirst().getId());

        verify(appointmentService).createAppointment(request);
    }

    @Test
    void createAppointmentsMissingCompanyNumberShouldThrowDataException() {
        AppointmentCreationRequest request = AppointmentCreationRequest.builder()
                .companyNumber(null)
                .build();

        DataException exception = assertThrows(
                DataException.class,
                () -> appointmentsController.createAppointments(request)
        );

        assertEquals("Company Number is required to create appointments",
                exception.getMessage());

        verify(appointmentService, never())
                .createAppointment(any(AppointmentCreationRequest.class));
    }

    @Test
    void createAppointmentsServiceThrowsExceptionShouldWrapInDataException() {
        AppointmentCreationRequest request = AppointmentCreationRequest.builder()
                .companyNumber("12345678")
                .build();

        RuntimeException serviceException = new RuntimeException("Service failure");

        when(appointmentService.createAppointment(request))
                .thenThrow(serviceException);

        DataException thrown = assertThrows(
                DataException.class,
                () -> appointmentsController.createAppointments(request)
        );

        assertEquals("Error creating appointments", thrown.getMessage());
        assertEquals(serviceException, thrown.getCause());

        verify(appointmentService).createAppointment(request);
    }

    @Test
    void deleteAppointmentsSuccess() {
        final String companyNumber = "12345678";
        when(appointmentService.deleteAllAppointments(companyNumber)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = appointmentsController.deleteAppointments(companyNumber);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(appointmentService).deleteAllAppointments(companyNumber);
    }

    @Test
    void deleteAppointmentsNotFound() {
        final String companyNumber = "12345678";
        when(appointmentService.deleteAllAppointments(companyNumber)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = appointmentsController.deleteAppointments(companyNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(companyNumber, Objects.requireNonNull(response.getBody()).get("company-number"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(appointmentService).deleteAllAppointments(companyNumber);
    }

    @Test
    void deleteAppointmentsException() {
        final String companyNumber = "12345678";
        RuntimeException exception = new RuntimeException("Error message");
        when(appointmentService.deleteAllAppointments(companyNumber)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appointmentsController.deleteAppointments(companyNumber));
        assertEquals(exception, thrown);
    }

}
