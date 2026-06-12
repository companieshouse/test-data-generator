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
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.service.DataService;

@ExtendWith(MockitoExtension.class)
class CertifiedCopiesControllerTest {

    @Mock
    private DataService<CertificatesResponse, CertifiedCopiesRequest> certifiedCopiesService;

    @InjectMocks
    private CertifiedCopiesController certifiedCopiesController;

    @Test
    void createCertifiedCopies() throws Exception {
        CertifiedCopiesRequest request = new CertifiedCopiesRequest();
        request.setUserId("user-123");
        CertificatesResponse created = createCertificatesResponse("CCD-123456-789012");

        when(certifiedCopiesService.create(request)).thenReturn(created);

        ResponseEntity<CertificatesResponse> response = certifiedCopiesController.createCertifiedCopies(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(created, response.getBody());
        verify(certifiedCopiesService).create(request);
    }

    @Test
    void createCertifiedCopiesWithoutUserIdThrowsDataException() {
        CertifiedCopiesRequest request = new CertifiedCopiesRequest();

        DataException thrown = assertThrows(
                DataException.class,
                () -> certifiedCopiesController.createCertifiedCopies(request));

        assertEquals("User ID is required to create certified copies", thrown.getMessage());
        verifyNoInteractions(certifiedCopiesService);
    }

    @Test
    void createCertifiedCopiesWrapsServiceException() throws Exception {
        CertifiedCopiesRequest request = new CertifiedCopiesRequest();
        request.setUserId("user-123");
        RuntimeException exception = new RuntimeException("create failure");
        when(certifiedCopiesService.create(request)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> certifiedCopiesController.createCertifiedCopies(request));

        assertEquals("Error creating certified copies", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    @Test
    void deleteCertifiedCopies() throws Exception {
        String certifiedCopyId = "CCD-123456-789012";
        when(certifiedCopiesService.delete(certifiedCopyId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                certifiedCopiesController.deleteCertifiedCopies(certifiedCopyId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(certifiedCopiesService).delete(certifiedCopyId);
    }

    @Test
    void deleteCertifiedCopiesNotFound() throws Exception {
        String certifiedCopyId = "CCD-123456-789012";
        when(certifiedCopiesService.delete(certifiedCopyId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                certifiedCopiesController.deleteCertifiedCopies(certifiedCopyId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(certifiedCopyId, response.getBody().get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(certifiedCopiesService).delete(certifiedCopyId);
    }

    @Test
    void deleteCertifiedCopiesWrapsServiceException() {
        String certifiedCopyId = "CCD-123456-789012";
        RuntimeException exception = new RuntimeException("delete failure");
        when(certifiedCopiesService.delete(certifiedCopyId)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> certifiedCopiesController.deleteCertifiedCopies(certifiedCopyId));

        assertEquals("Error deleting certified copies", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    private CertificatesResponse createCertificatesResponse(String id) {
        return new CertificatesResponse(List.of(
                new CertificatesResponse.CertificateEntry(id, "2026-01-01T00:00:00Z", "2026-01-01T00:00:00Z")));
    }
}
