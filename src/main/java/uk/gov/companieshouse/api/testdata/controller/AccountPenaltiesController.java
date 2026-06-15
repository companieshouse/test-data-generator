package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyDeleteRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountPenaltiesController {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String ERROR = "error";
    private static final String STATUS = "status";

    private final AccountPenaltiesService accountPenaltiesService;

    public AccountPenaltiesController(AccountPenaltiesService accountPenaltiesService) {
        this.accountPenaltiesService = accountPenaltiesService;
    }

    @PostMapping("/penalties")
    public ResponseEntity<Object> createPenalty(
            @Valid @RequestBody PenaltyRequest request) throws DataException {
        LOG.info("Creating new account penalties for company code: " + request.getCompanyCode()
                + " and customer code: " + request.getCustomerCode());

        var createdPenalties = accountPenaltiesService.createAccountPenalties(request);

        if (Boolean.TRUE.equals(request.isDuplicate())
                && (createdPenalties == null || createdPenalties.getPenalties().isEmpty())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ERROR, "number_of_penalties "
                    + "should be greater than 1 for duplicate penalties");
            errorResponse.put(STATUS, HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        LOG.info("Successfully created account penalties with ID: "
                + createdPenalties.getIdAsString());
        return new ResponseEntity<>(createdPenalties, HttpStatus.CREATED);
    }

    @GetMapping("/penalties/{id}")
    public ResponseEntity<AccountPenaltiesResponse> getAccountPenalties(
            @PathVariable("id") String id,
            @RequestParam(name = "transactionReference", required = false)
            String transactionReference) throws NoDataFoundException {
        var accountPenaltiesData = accountPenaltiesService.getAccountPenalties(id);
        if (transactionReference != null) {
            accountPenaltiesData.setPenalties(
                    accountPenaltiesData.getPenalties().stream()
                            .filter(penalty -> transactionReference.equals(
                                    penalty.getTransactionReference()))
                            .toList()
            );
        }
        return ResponseEntity.ok(accountPenaltiesData);
    }

    @GetMapping("/penalties/query")
    public ResponseEntity<AccountPenaltiesResponse> getAccountPenaltiesByCustomerCodeAndCompanyCode(
            @RequestParam(name = "customerCode") String customerCode,
            @RequestParam(name = "companyCode") String companyCode) throws NoDataFoundException {

        var accountPenaltiesData = accountPenaltiesService.getAccountPenalties(
                customerCode, companyCode);

        return ResponseEntity.ok(accountPenaltiesData);
    }

    @PutMapping("/penalties/{penaltyRef}")
    public ResponseEntity<AccountPenaltiesResponse> updateAccountPenalties(
            @PathVariable("penaltyRef") String penaltyRef,
            @Valid @RequestBody UpdateAccountPenaltiesRequest request)
            throws NoDataFoundException, DataException {

        var accountPenaltiesData = accountPenaltiesService.updateAccountPenalties(
                penaltyRef, request);

        return new ResponseEntity<>(accountPenaltiesData, HttpStatus.CREATED);
    }

    @DeleteMapping("/penalties/{id}")
    public ResponseEntity<Void> deleteAccountPenalties(
            @PathVariable("id") String id,
            @RequestBody(required = false) PenaltyDeleteRequest request)
            throws NoDataFoundException {

        if (request == null || request.getTransactionReference() == null) {
            return accountPenaltiesService.deleteAccountPenalties(id);
        } else {
            return accountPenaltiesService.deleteAccountPenaltyByReference(
                    id, request.getTransactionReference());
        }
    }

}
