package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;



@ExtendWith(MockitoExtension.class)
class IdentityVerificationControllerTest {
    @Mock
    private VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService;

    @InjectMocks
    private IdentityVerificationController identityVerificationController;

    @Test
    void getIdentityVerification_serviceReturnsData_returnsOk() throws Exception {
        final String email = "user@example.com";
        var data = new IdentityVerificationResponse("identity-id-123", "UVID-ABC",
                "Firstname", "Lastname");

        when(this.verifiedIdentityService.getIdentityVerificationData(email))
                .thenReturn(data);

        ResponseEntity<IdentityVerificationResponse> response = this.identityVerificationController
                .getIdentityVerification(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(data, response.getBody());

        verify(verifiedIdentityService, times(1)).getIdentityVerificationData(email);
    }

    @Test
    void getIdentityVerification_serviceReturnsNull_throwsNoDataFoundException() throws Exception {
        final String email = "missing@example.com";

        when(this.verifiedIdentityService.getIdentityVerificationData(email))
                .thenReturn(null);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.identityVerificationController.getIdentityVerification(email));

        assertEquals("No identity verification found for email: " + email, thrown.getMessage());

        verify(verifiedIdentityService, times(1)).getIdentityVerificationData(email);
    }

}
