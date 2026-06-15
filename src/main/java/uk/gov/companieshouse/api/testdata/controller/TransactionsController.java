package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.service.TransactionService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionsController {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionsResponse> createTransaction(
            @Valid @RequestBody TransactionsRequest request) throws DataException {

        Optional<TransactionsRequest> optionalRequest = Optional.ofNullable(request);
        TransactionsRequest spec = optionalRequest.orElse(new TransactionsRequest());

        TransactionsResponse createdTransaction = transactionService.create(spec);

        Map<String, Object> data = new HashMap<>();
        data.put("_id", createdTransaction.getId());
        data.put("reference", spec.getReference());
        LOG.info("Transaction created", data);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable("transactionId") String transactionId) {

        Map<String, Object> response = new HashMap<>();
        response.put("transaction_id", transactionId);
        boolean deleteTransaction = transactionService.delete(transactionId);

        if (deleteTransaction) {
            LOG.info("Transaction is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Transaction is not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
