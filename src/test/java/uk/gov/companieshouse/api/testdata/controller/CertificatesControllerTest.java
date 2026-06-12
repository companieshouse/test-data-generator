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
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.service.CertificatesService;

@ExtendWith(MockitoExtension.class)
class CertificatesControllerTest {

    @Mock
    private CertificatesService certificatesService;

    @InjectMocks
    private CertificatesController certificatesController;

    @Test
    void createCertificates() throws Exception {
        CertificatesRequest request = new CertificatesRequest();
        request.setUserId("user-123");
        CertificatesResponse created = createCertificatesResponse("CRT-123456-789012");

        when(certificatesService.create(request)).thenReturn(created);

        ResponseEntity<CertificatesResponse> response = certificatesController.createCertificates(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(created, response.getBody());
        verify(certificatesService).create(request);
    }

    @Test
    void createCertificatesWithoutUserIdThrowsDataException() {
        CertificatesRequest request = new CertificatesRequest();

        DataException thrown = assertThrows(
                DataException.class,
                () -> certificatesController.createCertificates(request));

        assertEquals("User ID is required to create certificates", thrown.getMessage());
        verifyNoInteractions(certificatesService);
    }

    @Test
    void createCertificatesWrapsServiceException() throws Exception {
        CertificatesRequest request = new CertificatesRequest();
        request.setUserId("user-123");
        RuntimeException exception = new RuntimeException("create failure");
        when(certificatesService.create(request)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> certificatesController.createCertificates(request));

        assertEquals("Error creating certificates", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    @Test
    void deleteCertificates() throws Exception {
        String certificateId = "CRT-123456-789012";
        when(certificatesService.delete(certificateId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                certificatesController.deleteCertificates(certificateId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(certificatesService).delete(certificateId);
    }

    @Test
    void deleteCertificatesNotFound() throws Exception {
        String certificateId = "CRT-123456-789012";
        when(certificatesService.delete(certificateId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                certificatesController.deleteCertificates(certificateId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(certificateId, response.getBody().get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(certificatesService).delete(certificateId);
    }

    @Test
    void deleteCertificatesWrapsServiceException() {
        String certificateId = "CRT-123456-789012";
        RuntimeException exception = new RuntimeException("delete failure");
        when(certificatesService.delete(certificateId)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> certificatesController.deleteCertificates(certificateId));

        assertEquals("Error deleting certificates", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    private CertificatesResponse createCertificatesResponse(String id) {
        return new CertificatesResponse(List.of(
                new CertificatesResponse.CertificateEntry(id, "2026-01-01T00:00:00Z", "2026-01-01T00:00:00Z")));
    }
}
