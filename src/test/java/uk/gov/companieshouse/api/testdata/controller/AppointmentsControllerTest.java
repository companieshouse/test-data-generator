package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentsControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentsController appointmentsController;

    @Test
    void deleteAppointments_success() {
        final String companyNumber = "12345678";
        when(appointmentService.deleteAllAppointments(companyNumber)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = appointmentsController.deleteAppointments(companyNumber);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(appointmentService).deleteAllAppointments(companyNumber);
    }

    @Test
    void deleteAppointments_notFound() {
        final String companyNumber = "12345678";
        when(appointmentService.deleteAllAppointments(companyNumber)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = appointmentsController.deleteAppointments(companyNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(companyNumber, Objects.requireNonNull(response.getBody()).get("company-number"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(appointmentService).deleteAllAppointments(companyNumber);
    }

    @Test
    void deleteAppointments_exception() {
        final String companyNumber = "12345678";
        RuntimeException exception = new RuntimeException("Error message");
        when(appointmentService.deleteAllAppointments(companyNumber)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                appointmentsController.deleteAppointments(companyNumber));
        assertEquals(exception, thrown);
    }

}
