package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void createAppointmentReturnsCreatedAndBody() throws Exception {
        // build a request (only minimal fields required for test)
        AppointmentCreationRequest request = AppointmentCreationRequest.builder()
                .companyNumber("01234567")
                .officerId("officer-456")
                .build();

        // mock service response
        AppointmentsResultResponse mockResponse = new AppointmentsResultResponse();
        mockResponse.setAppointmentId("appt-123");
        mockResponse.setOfficerId("officer-456");

        when(appointmentService.createAppointmentFromRequest(any(AppointmentCreationRequest.class)))
                .thenReturn(mockResponse);

        // call controller directly
        ResponseEntity<AppointmentsResultResponse> response = appointmentController.createAppointment(request);

        // assertions consistent with other controller tests in repo
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("appt-123", response.getBody().getAppointmentId());
        assertEquals("officer-456", response.getBody().getOfficerId());
    }
}