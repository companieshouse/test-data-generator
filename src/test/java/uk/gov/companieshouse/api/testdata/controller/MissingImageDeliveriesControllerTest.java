package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.service.MissingImageDeliveriesService;

@ExtendWith(MockitoExtension.class)
class MissingImageDeliveriesControllerTest {

    @Mock
    private MissingImageDeliveriesService missingImageDeliveriesService;

    @InjectMocks
    private MissingImageDeliveriesController missingImageDeliveriesController;

    @Test
    void createMissingImageDeliveries() throws Exception {
        MissingImageDeliveriesRequest request = new MissingImageDeliveriesRequest();
        request.setUserId("user-123");
        CertificatesResponse created = createCertificatesResponse("MID-123456-789012");

        when(missingImageDeliveriesService.create(request)).thenReturn(created);

        ResponseEntity<CertificatesResponse> response =
                missingImageDeliveriesController.createMissingImageDeliveries(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(created, response.getBody());
        verify(missingImageDeliveriesService).create(request);
    }

    @Test
    void createMissingImageDeliveriesWithoutUserIdThrowsDataException() {
        MissingImageDeliveriesRequest request = new MissingImageDeliveriesRequest();

        DataException thrown = assertThrows(
                DataException.class,
                () -> missingImageDeliveriesController.createMissingImageDeliveries(request));

        assertEquals("User ID is required to create missing image deliveries", thrown.getMessage());
        verifyNoInteractions(missingImageDeliveriesService);
    }

    @Test
    void createMissingImageDeliveriesWrapsServiceException() throws Exception {
        MissingImageDeliveriesRequest request = new MissingImageDeliveriesRequest();
        request.setUserId("user-123");
        RuntimeException exception = new RuntimeException("create failure");
        when(missingImageDeliveriesService.create(request)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> missingImageDeliveriesController.createMissingImageDeliveries(request));

        assertEquals("Error creating missing image deliveries", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    @Test
    void deleteMissingImageDeliveries() throws Exception {
        String missingImageDeliveryId = "MID-123456-789012";
        when(missingImageDeliveriesService.delete(missingImageDeliveryId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                missingImageDeliveriesController.deleteMissingImageDeliveries(missingImageDeliveryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(missingImageDeliveriesService).delete(missingImageDeliveryId);
    }

    @Test
    void deleteMissingImageDeliveriesNotFound() throws Exception {
        String missingImageDeliveryId = "MID-123456-789012";
        when(missingImageDeliveriesService.delete(missingImageDeliveryId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                missingImageDeliveriesController.deleteMissingImageDeliveries(missingImageDeliveryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(missingImageDeliveryId, response.getBody().get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(missingImageDeliveriesService).delete(missingImageDeliveryId);
    }

    @Test
    void deleteMissingImageDeliveriesWrapsServiceException() {
        String missingImageDeliveryId = "MID-123456-789012";
        RuntimeException exception = new RuntimeException("delete failure");
        when(missingImageDeliveriesService.delete(missingImageDeliveryId)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> missingImageDeliveriesController.deleteMissingImageDeliveries(missingImageDeliveryId));

        assertEquals("Error deleting missing image deliveries", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    private CertificatesResponse createCertificatesResponse(String id) {
        return new CertificatesResponse(List.of(
                new CertificatesResponse.CertificateEntry(id, "2026-01-01T00:00:00Z", "2026-01-01T00:00:00Z")));
    }
}
