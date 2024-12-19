package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspTestDataServiceImplTest {

    private static final String API_URL = "http://localhost:4001";
    private static final int ACSP_NUMBER_LENGTH = 8;

    @Mock
    private AcspProfileService acspProfileService;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private AcspTestDataServiceImpl acspTestDataService;

    @Captor
    private ArgumentCaptor<AcspSpec> specCaptor;

    @BeforeEach
    void setUp() {
        acspTestDataService.setAPIUrl(API_URL);
    }

    @Test
    void createAcspDataSuccess() throws DataException {
        AcspSpec spec = new AcspSpec();
        long acspNumber = 12345678L;
        when(randomService.getNumber(ACSP_NUMBER_LENGTH)).thenReturn(acspNumber);
        when(acspProfileService.acspProfileExists(acspNumber)).thenReturn(false);

        AcspData acspData = new AcspData(acspNumber, API_URL + "/acsp/" + acspNumber);
        // Mock create to not throw exception
        when(acspProfileService.create(spec)).thenReturn(null);

        AcspData result = acspTestDataService.createAcspData(spec);

        verify(acspProfileService).create(spec);
        assertEquals(acspData.getAcspNumber(), result.getAcspNumber());
        assertEquals(acspData.getAcspUri(), result.getAcspUri());
    }

    @Test
    void createAcspDataNullSpec() throws DataException {
        assertThrows(IllegalArgumentException.class, () -> acspTestDataService.createAcspData(null));
        verify(acspProfileService, never()).create(any());
    }

    @Test
    void createAcspDataExistingNumberRetries() throws DataException {
        AcspSpec spec = new AcspSpec();
        long existingNumber = 11111111L;
        long newNumber = 22222222L;

        // First time returns existing number, which exists, second time returns a new unique number
        when(randomService.getNumber(ACSP_NUMBER_LENGTH)).thenReturn(existingNumber, newNumber);
        when(acspProfileService.acspProfileExists(existingNumber)).thenReturn(true);
        when(acspProfileService.acspProfileExists(newNumber)).thenReturn(false);

        when(acspProfileService.create(spec)).thenReturn(null);

        AcspData result = acspTestDataService.createAcspData(spec);

        assertEquals(newNumber, result.getAcspNumber());
        assertEquals(API_URL + "/acsp/" + newNumber, result.getAcspUri());
    }

    @Test
    void createAcspDataExceptionAndRollback() throws DataException {
        AcspSpec spec = new AcspSpec();
        long acspNumber = 12345678L;
        when(randomService.getNumber(ACSP_NUMBER_LENGTH)).thenReturn(acspNumber);
        when(acspProfileService.acspProfileExists(acspNumber)).thenReturn(false);

        RuntimeException creationException = new RuntimeException("creation failed");
        doThrow(creationException).when(acspProfileService).create(spec);

        DataException thrown = assertThrows(DataException.class, () -> acspTestDataService.createAcspData(spec));
        assertEquals(creationException, thrown.getCause());

        // Verify rollback attempt
        verify(acspProfileService).delete(acspNumber);
    }

    @Test
    void deleteAcspDataSuccess() throws DataException {
        long acspNumber = 12345678L;
        // No exceptions thrown
        acspTestDataService.deleteAcspData(acspNumber);

        verify(acspProfileService, times(1)).delete(acspNumber);
    }

    @Test
    void deleteAcspDataException() {
        long acspNumber = 12345678L;
        RuntimeException deleteException = new RuntimeException("delete failed");
        doThrow(deleteException).when(acspProfileService).delete(acspNumber);

        DataException thrown = assertThrows(DataException.class, () -> acspTestDataService.deleteAcspData(acspNumber));

        assertEquals("Error deleting acsp data", thrown.getMessage());
        // The suppressed exception should contain the deleteException
        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(deleteException, thrown.getSuppressed()[0]);
    }
}
