package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.service.IdentityService;

@ExtendWith(MockitoExtension.class)
class IdentityControllerTest {

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private IdentityController identityController;

    private static final String IDENTITY_ID = "identity-123";

    @Test
    void createIdentitySuccess() {
        IdentityVerificationRequest request = new IdentityVerificationRequest();
        IdentityVerificationResponse response =
                new IdentityVerificationResponse(
                        IDENTITY_ID,
                        "uvid-123",
                        "John",
                        "Smith",
                        "acsp-123",
                        "acsp-user-123");

        when(identityService.createIdentity(request)).thenReturn(response);

        ResponseEntity<IdentityVerificationResponse> result = identityController.createIdentity(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(identityService, times(1)).createIdentity(request);
    }

    @Test
    void getIdentitySuccess() {

        IdentityVerificationResponse response =
                new IdentityVerificationResponse(
                        IDENTITY_ID,
                        "uvid-123",
                        "John",
                        "Smith",
                        "acsp-123",
                        "acsp-user-123");

        when(identityService.getIdentity(IDENTITY_ID)).thenReturn(response);

        ResponseEntity<?> result = identityController.getIdentity(IDENTITY_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        IdentityVerificationResponse body = (IdentityVerificationResponse) result.getBody();

        assertEquals(IDENTITY_ID, body.getIdentityId());
        assertEquals("uvid-123", body.getUvid());
        assertEquals("John", body.getFirstName());
        assertEquals("Smith", body.getLastName());
        assertEquals("acsp-123", body.getAcspId());
        assertEquals("acsp-user-123", body.getAcspUserId());

        verify(identityService, times(1)).getIdentity(IDENTITY_ID);
    }

    @Test
    void getIdentityNotFound() {
        when(identityService.getIdentity(IDENTITY_ID)).thenReturn(null);

        ResponseEntity<?> result = identityController.getIdentity(IDENTITY_ID);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) result.getBody();

        assertEquals(IDENTITY_ID, body.get("identity id"));
        assertEquals(HttpStatus.NOT_FOUND, body.get("status"));

        verify(identityService, times(1)).getIdentity(IDENTITY_ID);
    }

    @Test
    void deleteIdentitySuccess() {
        when(identityService.deleteIdentity(IDENTITY_ID)).thenReturn(true);

        ResponseEntity<Map<String, Object>> result = identityController.deleteIdentity(IDENTITY_ID);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());

        verify(identityService, times(1)).deleteIdentity(IDENTITY_ID);
    }

    @Test
    void deleteIdentityNotFound() {

        when(identityService.deleteIdentity(IDENTITY_ID)).thenReturn(false);

        ResponseEntity<Map<String, Object>> result = identityController.deleteIdentity(IDENTITY_ID);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Map<String, Object> body = result.getBody();

        assertEquals(IDENTITY_ID, body.get("identity id"));
        assertEquals(HttpStatus.NOT_FOUND, body.get("status"));

        verify(identityService, times(1)).deleteIdentity(IDENTITY_ID);
    }
}