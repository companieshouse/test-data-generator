package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionsControllerTest {
    @Mock
    private TransactionService testDataService;

    @InjectMocks
    private TransactionsController testDataController;

    private static final String TRANSACTION_ID = "412123-412123-412123";

    @Test
    void createTransaction() throws Exception {
        TransactionsRequest request = new TransactionsRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setReference("ACSP Registration");

        TransactionsResponse txn = new TransactionsResponse(
                "rsf3pdwywvse5yz55mfodfx8","email@email.com","forename",
                "surname","resumeURI","status", "250788-250788-250788");
        when(this.testDataService.create(request)).thenReturn(txn);
        ResponseEntity<TransactionsResponse> response
                = this.testDataController.createTransaction(request);

        assertEquals(txn, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createTransactionException() throws Exception {
        TransactionsRequest request = new TransactionsRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setReference("ACSP Registration");
        Throwable exception = new DataException("Error message");

        when(this.testDataService.create(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createTransaction(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteTransaction() throws Exception {
        when(this.testDataService.delete(TRANSACTION_ID))
                .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                this.testDataController.deleteTransaction(TRANSACTION_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionNotFound() throws Exception {
        when(this.testDataService.delete(TRANSACTION_ID))
                .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                this.testDataController.deleteTransaction(TRANSACTION_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(TRANSACTION_ID,
                Objects.requireNonNull(response.getBody()).get("transaction_id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionException() {
        Throwable exception = new RuntimeException("Error deleting transaction");

        when(this.testDataService.delete(TRANSACTION_ID))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> this.testDataController.deleteTransaction(TRANSACTION_ID));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).delete(TRANSACTION_ID);
    }

}
