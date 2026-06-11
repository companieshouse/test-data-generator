package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;

@ExtendWith(MockitoExtension.class)
class AcspProfileControllerTest {

    @Mock
    private AcspProfileService acspProfileService;

    @InjectMocks
    private AcspProfileController acspProfileController;

    @Test
    void getAcspProfileFound() throws Exception {
        String acspNumber = "AP000036";

        AcspProfile profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setAcspNumber(acspNumber);
        profile.setName("Test ACSP Company");
        profile.setStatus("active");

        when(acspProfileService.getAcspProfile(acspNumber)).thenReturn(Optional.of(profile));

        ResponseEntity<Optional<AcspProfile>> response =
                acspProfileController.getAcspProfile(acspNumber);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());

        AcspProfile returnedProfile = response.getBody().get();
        assertEquals(acspNumber, returnedProfile.getId());
        assertEquals(acspNumber, returnedProfile.getAcspNumber());
        assertEquals("Test ACSP Company", returnedProfile.getName());
        assertEquals("active", returnedProfile.getStatus());

        verify(acspProfileService, times(1)).getAcspProfile(acspNumber);
    }

    @Test
    void getAcspProfileNotFound() throws Exception {
        String acspNumber = "NON_EXISTENT";

        when(acspProfileService.getAcspProfile(acspNumber)).thenReturn(Optional.empty());

        ResponseEntity<Optional<AcspProfile>> response =
                acspProfileController.getAcspProfile(acspNumber);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(acspProfileService, times(1)).getAcspProfile(acspNumber);
    }

    @Test
    void getAcspProfileThrowsNoDataFoundException() throws Exception {
        String acspNumber = "AP000036";
        NoDataFoundException ex = new NoDataFoundException("ACSP not found");

        when(acspProfileService.getAcspProfile(acspNumber)).thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                acspProfileController.getAcspProfile(acspNumber));
        assertEquals(ex, thrown);
    }
}
