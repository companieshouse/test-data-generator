package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.PscDiscrepanciesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PscDiscrepanciesResponse;
import uk.gov.companieshouse.api.testdata.service.PscDiscrepanciesService;

@ExtendWith(MockitoExtension.class)
class PscDiscrepanciesControllerTest {

    private static final String PSC_DISCREPANCY_ID =
            "5d03f438-972b-4d60-b0ae-f32ae8e22833";

    private static final String PSC_DISCREPANCY_REPORT_ID =
            "c119c044-2c78-4083-b633-c397e4f64eda";

    @Mock
    private PscDiscrepanciesService pscDiscrepanciesService;

    @InjectMocks
    private PscDiscrepanciesController pscDiscrepanciesController;

    @Test
    void createPscDiscrepancySuccess() throws Exception {

        PscDiscrepanciesRequest request = new PscDiscrepanciesRequest();
        request.setUserId("test-user");
        request.setPscType("individual-person-with-significant-control");

        PscDiscrepanciesResponse response =
                new PscDiscrepanciesResponse();
        response.setPscDiscrepanciesId(PSC_DISCREPANCY_ID);
        response.setPscDiscrepancyReportId(
                PSC_DISCREPANCY_REPORT_ID);

        when(pscDiscrepanciesService.create(request))
                .thenReturn(response);

        ResponseEntity<PscDiscrepanciesResponse> result =
                pscDiscrepanciesController.createPscDiscrepancy(
                        request);

        assertEquals(HttpStatus.CREATED,
                result.getStatusCode());
        assertEquals(response,
                result.getBody());

        verify(pscDiscrepanciesService, times(1))
                .create(request);
    }

    @Test
    void createPscDiscrepancyThrowsDataException() throws Exception {

        PscDiscrepanciesRequest request =
                new PscDiscrepanciesRequest();

        RuntimeException exception =
                new RuntimeException("Create failure");

        when(pscDiscrepanciesService.create(request))
                .thenThrow(exception);

        DataException thrown =
                assertThrows(DataException.class,
                        () -> pscDiscrepanciesController
                                .createPscDiscrepancy(request));

        assertEquals(
                "Error creating PSC discrepancy",
                thrown.getMessage());

        verify(pscDiscrepanciesService, times(1))
                .create(request);
    }

    @Test
    void deletePscDiscrepancySuccess() throws Exception {

        when(pscDiscrepanciesService.delete(
                PSC_DISCREPANCY_ID))
                .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                pscDiscrepanciesController
                        .deletePscDiscrepancy(
                                PSC_DISCREPANCY_ID);

        assertEquals(HttpStatus.NO_CONTENT,
                response.getStatusCode());

        verify(pscDiscrepanciesService, times(1))
                .delete(PSC_DISCREPANCY_ID);
    }

    @Test
    void deletePscDiscrepancyNotFound() throws Exception {

        when(pscDiscrepanciesService.delete(
                PSC_DISCREPANCY_ID))
                .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                pscDiscrepanciesController
                        .deletePscDiscrepancy(
                                PSC_DISCREPANCY_ID);

        assertEquals(HttpStatus.NOT_FOUND,
                response.getStatusCode());

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getBody().get("status"));

        assertEquals(
                PSC_DISCREPANCY_ID,
                response.getBody().get("id"));

        verify(pscDiscrepanciesService, times(1))
                .delete(PSC_DISCREPANCY_ID);
    }

    @Test
    void deletePscDiscrepancyThrowsDataException()
            throws Exception {

        RuntimeException exception =
                new RuntimeException("Delete failure");

        when(pscDiscrepanciesService.delete(
                PSC_DISCREPANCY_ID))
                .thenThrow(exception);

        DataException thrown =
                assertThrows(DataException.class,
                        () -> pscDiscrepanciesController
                                .deletePscDiscrepancy(
                                        PSC_DISCREPANCY_ID));

        assertEquals(
                "Error deleting PSC discrepancy",
                thrown.getMessage());

        verify(pscDiscrepanciesService, times(1))
                .delete(PSC_DISCREPANCY_ID);
    }
}