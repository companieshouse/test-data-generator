package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.service.FilingHistoryService;

@ExtendWith(MockitoExtension.class)
class FilingHistoryControllerTest {
    @Mock
    private FilingHistoryService filingHistoryService;

    @InjectMocks
    private FilingHistoryController filingHistoryController;

    @Test
    void getCompanyFilingHistoryNoParamsReturnsBadRequest() {
        ResponseEntity<Object> response =
                filingHistoryController.getCompanyFilingHistory(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Either companyNumber or id must be provided", body.get("error"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void getCompanyFilingHistoryByCompanyNumberFound() {
        String companyNumber = "12345678";

        FilingHistory filingHistory = new FilingHistory();
        List<FilingHistory> results = List.of(filingHistory);

        when(filingHistoryService.getCompanyFilingHistoryByCompanyNumber(companyNumber))
                .thenReturn(results);

        ResponseEntity<?> response =
                filingHistoryController.getCompanyFilingHistory(companyNumber, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(results, response.getBody());

        verify(filingHistoryService, times(1))
                .getCompanyFilingHistoryByCompanyNumber(companyNumber);
    }

    @Test
    void getCompanyFilingHistoryByCompanyNumberNotFound() {
        String companyNumber = "12345678";

        when(filingHistoryService.getCompanyFilingHistoryByCompanyNumber(companyNumber))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response =
                filingHistoryController.getCompanyFilingHistory(companyNumber, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(filingHistoryService, times(1))
                .getCompanyFilingHistoryByCompanyNumber(companyNumber);
    }

    @Test
    void getCompanyFilingHistoryByIdFound() {
        String filingHistoryId = "FH123";

        FilingHistory fh = new FilingHistory();

        when(filingHistoryService.getCompanyFilingHistoryById(filingHistoryId))
                .thenReturn(Optional.of(fh));

        ResponseEntity<?> response =
                filingHistoryController.getCompanyFilingHistory(null, filingHistoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fh, response.getBody());

        verify(filingHistoryService, times(1))
                .getCompanyFilingHistoryById(filingHistoryId);
    }

    @Test
    void getCompanyFilingHistoryByIdNotFound() {
        String filingHistoryId = "FH123";

        when(filingHistoryService.getCompanyFilingHistoryById(filingHistoryId))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response =
                filingHistoryController.getCompanyFilingHistory(null, filingHistoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(filingHistoryService, times(1))
                .getCompanyFilingHistoryById(filingHistoryId);
    }

    @Test
    void deleteCompanyFilingHistorySuccess() throws Exception {
        String companyNumber = "12345678";

        when(filingHistoryService.deleteCompanyFilingHistory(companyNumber))
                .thenReturn(true);

        ResponseEntity<?> response =
                filingHistoryController.deleteCompanyFilingHistory(companyNumber);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(filingHistoryService, times(1))
                .deleteCompanyFilingHistory(companyNumber);
    }

    @Test
    void deleteCompanyFilingHistoryNotFound() throws Exception {
        String companyNumber = "12345678";

        when(filingHistoryService.deleteCompanyFilingHistory(companyNumber))
                .thenReturn(false);

        ResponseEntity<?> response =
                filingHistoryController.deleteCompanyFilingHistory(companyNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(companyNumber, body.get("companyNumber"));
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));

        verify(filingHistoryService, times(1))
                .deleteCompanyFilingHistory(companyNumber);
    }
}
