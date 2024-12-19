package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.AcspTestDataService;

@ExtendWith(MockitoExtension.class)
class AcspProfileControllerTest {

    @Mock
    private AcspTestDataService acspTestDataService;

    @InjectMocks
    private AcspProfileController acspProfileController;

    @Captor
    private ArgumentCaptor<AcspSpec> specCaptor;

    @Test
    void create() throws Exception {
        // The controller creates a new AcspSpec regardless of the request, ignoring the input 'request'
        AcspSpec request = new AcspSpec();
        request.setJurisdiction(Jurisdiction.SCOTLAND);

        AcspData acspData = new AcspData(123456L, "http://localhost:4001/acsp/123456");
        when(acspTestDataService.createAcspData(any(AcspSpec.class))).thenReturn(acspData);

        ResponseEntity<AcspData> response = acspProfileController.create(request);

        // Even though we passed a spec, the controller uses a new AcspSpec internally.
        // We only verify the returned data matches the mocked service response.
        assertEquals(acspData, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createNoRequest() throws Exception {
        // Passing null request, controller still uses a new AcspSpec.
        AcspData acspData = new AcspData(123456L, "http://localhost:4001/acsp/123456");

        when(acspTestDataService.createAcspData(any(AcspSpec.class))).thenReturn(acspData);
        ResponseEntity<AcspData> response = acspProfileController.create(null);

        assertEquals(acspData, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // We can capture the AcspSpec used to confirm default values are set if desired.
        verify(acspTestDataService).createAcspData(specCaptor.capture());
        AcspSpec usedSpec = specCaptor.getValue();
        // Default jurisdiction is ENGLAND_WALES
        assertEquals(Jurisdiction.ENGLAND_WALES, usedSpec.getJurisdiction());
    }

    @Test
    void createException() throws Exception {
        AcspSpec request = new AcspSpec();
        request.setJurisdiction(Jurisdiction.NI);

        DataException exception = new DataException("Error message");
        when(acspTestDataService.createAcspData(any(AcspSpec.class))).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () -> {
            acspProfileController.create(request);
        });
        assertEquals(exception, thrown);
    }

    @Test
    void delete() throws Exception {
        long acspNumber = 123456L;
        // No exception thrown, means success
        ResponseEntity<Void> response = acspProfileController.delete(acspNumber);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(acspTestDataService).deleteAcspData(acspNumber);
    }

    @Test
    void deleteDataException() throws Exception {
        long acspNumber = 123456L;
        DataException ex = new DataException("Error message");
        doThrow(ex).when(acspTestDataService).deleteAcspData(acspNumber);

        DataException thrown = assertThrows(DataException.class, () -> {
            acspProfileController.delete(acspNumber);
        });
        assertEquals(ex, thrown);
    }

    @Test
    void deleteNoDataFound() throws Exception {
        long acspNumber = 123456L;
        NoDataFoundException ex = new NoDataFoundException("no data found");
        doThrow(ex).when(acspTestDataService).deleteAcspData(acspNumber);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () -> {
            acspProfileController.delete(acspNumber);
        });
        assertEquals(ex, thrown);
    }
}
