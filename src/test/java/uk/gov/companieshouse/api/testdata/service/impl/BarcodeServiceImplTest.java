package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceImplTest {

    private static final String BARCODE_URL = "test";
    private static final String RETURNED_BARCODE = "BARCODE";

    @Mock
    private RestTemplate template;

    @InjectMocks
    private BarcodeServiceImpl barcodeService;

    @BeforeEach
    void setUp() {
        this.barcodeService.setBarcodeUrl(BARCODE_URL);
    }

    @Test
    void getBarcode() throws BarcodeServiceException {
        ResponseEntity<String> response = new ResponseEntity<>("{\"barcode\" : \"" + RETURNED_BARCODE + "\"}", HttpStatus.OK);
        when(template.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(response);
        String barcode = barcodeService.getBarcode();

        assertEquals(RETURNED_BARCODE, barcode);
    }

    @Test
    void getBarcodeInvalidResponseBody() {
        ResponseEntity<String> invalidResponse = new ResponseEntity<>("This is invalid", HttpStatus.OK);

        when(template.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(invalidResponse);

        BarcodeServiceException exception = assertThrows(BarcodeServiceException.class, () ->
                this.barcodeService.getBarcode()
        );
        Assertions.assertEquals("Error creating barcode", exception.getMessage());
    }
}