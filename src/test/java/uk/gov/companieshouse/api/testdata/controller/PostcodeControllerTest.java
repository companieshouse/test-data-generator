package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostcodeControllerTest {
    @Mock
    private PostcodeService postcodeService;

    @InjectMocks
    private PostcodeController postcodeController;

    @Test
    void getPostcodeSuccess() throws Exception {
        String country = "England";
        PostcodesResponse postcodesResponse =
                new PostcodesResponse(12, "Thoroughfare Name", "Dependent Locality",
                        "Locality Post Town", "ABC 123");

        when(postcodeService.getPostcodes(country)).thenReturn(postcodesResponse);

        ResponseEntity<PostcodesResponse> response = postcodeController.getPostcode(country);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postcodesResponse, response.getBody());
        verify(postcodeService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeNoDataFound() throws Exception {
        String country = "UnknownCountry";

        when(postcodeService.getPostcodes(country)).thenReturn(null);

        ResponseEntity<PostcodesResponse> response = postcodeController.getPostcode(country);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postcodeService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeDataException() throws Exception {
        String country = "ErrorCountry";

        when(postcodeService.getPostcodes(country))
                .thenThrow(new DataException("Error retrieving postcodes"));

        DataException thrown = assertThrows(DataException.class, () ->
                postcodeController.getPostcode(country));

        assertEquals("Error retrieving postcodes", thrown.getMessage());
        verify(postcodeService, times(1)).getPostcodes(country);
    }

    @Test
    void testGetPostcodeIsNull() throws Exception {
        ResponseEntity<PostcodesResponse> response = postcodeController.getPostcode(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postcodeService, times(0)).getPostcodes(anyString());
    }

}
