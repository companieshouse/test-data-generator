package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyDeleteRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PenaltyResponse;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;

@ExtendWith(MockitoExtension.class)
class AccountPenaltiesControllerTest {
    @Mock
    private AccountPenaltiesService accountPenaltiesService;

    @InjectMocks
    private AccountPenaltiesController accountPenaltiesController;

    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "NI23456";
    private static final String PENALTY_REFERENCE = "A1234567";

    @Test
    void getAccountPenaltyNotFound() throws Exception {
        when(this.accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("no account penalties"));

        assertThrows(NoDataFoundException.class, () -> {
            this.accountPenaltiesController.getAccountPenalties(PENALTY_ID, PENALTY_REFERENCE);
        });
    }

    @Test
    void getAccountPenalties() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setId(PENALTY_ID);

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();

        when(this.accountPenaltiesService.getAccountPenalties(request.getId()))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response = this.accountPenaltiesController
                .getAccountPenalties(request.getId(), null);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesNotFound() throws Exception {
        String penaltyId = PENALTY_ID;

        when(this.accountPenaltiesService.getAccountPenalties(penaltyId))
                .thenThrow(new NoDataFoundException("Account penalties not found"));

        assertThrows(NoDataFoundException.class, () -> {
            this.accountPenaltiesController.getAccountPenalties(penaltyId, null);
        });
    }

    @Test
    void getAccountPenaltiesByCustomerCodeAndCompanyCode() throws Exception {
        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();

        when(this.accountPenaltiesService.getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response = this.accountPenaltiesController
                .getAccountPenaltiesByCustomerCodeAndCompanyCode(CUSTOMER_CODE, COMPANY_CODE);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesByCustomerCodeAndCompanyCodeNotFound() throws Exception {
        when(this.accountPenaltiesService.getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE))
                .thenThrow(new NoDataFoundException("Account penalties not found"));

        assertThrows(NoDataFoundException.class, () -> {
            this.accountPenaltiesController
                    .getAccountPenaltiesByCustomerCodeAndCompanyCode(CUSTOMER_CODE, COMPANY_CODE);
        });
    }

    @Test
    void updateAccountPenalties() throws Exception {
        String companyCode = COMPANY_CODE;
        String customerCode = CUSTOMER_CODE;
        Instant now = Instant.now();

        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(companyCode);
        request.setCustomerCode(customerCode);
        request.setCreatedAt(now);
        request.setClosedAt(now);
        request.setAmount(0.0);
        request.setOutstandingAmount(0.0);
        request.setIsPaid(true);

        PenaltyResponse penalty = createPenaltyData(companyCode,
                customerCode, PENALTY_REFERENCE, 0.0, true);

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();
        accountPenaltiesResponse.setCompanyCode(companyCode);
        accountPenaltiesResponse.setCreatedAt(now);
        accountPenaltiesResponse.setClosedAt(now);
        accountPenaltiesResponse.setPenalties(Collections.singletonList(penalty));

        when(this.accountPenaltiesService.updateAccountPenalties(PENALTY_REFERENCE, request))
                .thenReturn(accountPenaltiesResponse);
        ResponseEntity<AccountPenaltiesResponse> response = this.accountPenaltiesController
                .updateAccountPenalties(PENALTY_REFERENCE, request);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesFiltersByTransactionReference() throws Exception {
        PenaltyResponse penalty1 = new PenaltyResponse();
        penalty1.setTransactionReference(PENALTY_REFERENCE);
        PenaltyResponse penalty2 = new PenaltyResponse();
        penalty2.setTransactionReference("A2345678");

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();
        accountPenaltiesResponse.setPenalties(List.of(penalty1, penalty2));

        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response =
                accountPenaltiesController.getAccountPenalties(PENALTY_ID, PENALTY_REFERENCE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getPenalties().size());
        assertEquals(PENALTY_REFERENCE, response.getBody().getPenalties().getFirst()
                .getTransactionReference());
        verify(accountPenaltiesService, times(1)).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltyByReferenceSuccess() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = accountPenaltiesController
                .deleteAccountPenalties(PENALTY_ID, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountPenaltiesService, times(1))
                .deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE);
    }

    @Test
    void deleteAccountPenaltyByReferenceNotFound() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                accountPenaltiesController.deleteAccountPenalties(PENALTY_ID, request));
        assertEquals(exception, thrown);
        verify(accountPenaltiesService, times(1))
                .deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE);
    }

    @Test
    void deleteAccountPenaltyByReferenceOtherError() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        RuntimeException exception = new RuntimeException("error during deletion");
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                accountPenaltiesController.deleteAccountPenalties(PENALTY_ID, request));
        assertEquals(exception, thrown);
        verify(accountPenaltiesService, times(1))
                .deleteAccountPenaltyByReference(PENALTY_ID, "A1234567");
    }

    @Test
    void updateAccountPenaltiesNotFound() throws Exception {
        Instant now = Instant.now();

        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setCreatedAt(now);
        request.setClosedAt(now);
        request.setAmount(0.0);
        request.setOutstandingAmount(0.0);
        request.setIsPaid(true);

        NoDataFoundException exception = new NoDataFoundException("Account penalty not found");

        String penaltyRef = "A1234567";
        when(this.accountPenaltiesService.updateAccountPenalties(penaltyRef, request))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.accountPenaltiesController.updateAccountPenalties(penaltyRef, request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAccountPenaltiesSuccess() throws Exception {
        when(this.accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = accountPenaltiesController
                .deleteAccountPenalties(PENALTY_ID, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountPenaltiesService, times(1)).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesNotFound() throws Exception {
        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(this.accountPenaltiesService.deleteAccountPenalties(PENALTY_ID)).thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.accountPenaltiesController.deleteAccountPenalties(PENALTY_ID, null));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAccountPenaltiesOtherError() throws Exception {
        RuntimeException exception = new RuntimeException("error during deletion");
        when(this.accountPenaltiesService.deleteAccountPenalties(PENALTY_ID)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                this.accountPenaltiesController.deleteAccountPenalties(PENALTY_ID, null));
        assertEquals(exception, thrown);
    }

    private PenaltyResponse createPenaltyData(String companyCode, String customerCode,
                                              String penaltyRef, double amount, boolean paid) {
        PenaltyResponse penalty = new PenaltyResponse();
        penalty.setCompanyCode(companyCode);
        penalty.setCustomerCode(customerCode);
        penalty.setTransactionReference(penaltyRef);
        penalty.setTransactionDate("2025-02-25");
        penalty.setMadeUpDate("2025-02-12");
        penalty.setAmount(amount);
        penalty.setOutstandingAmount(amount);
        penalty.setIsPaid(paid);
        penalty.setAccountStatus("CHS");
        penalty.setDunningStatus("PEN1");
        return penalty;
    }

    @Test
    void createPenaltySuccess() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        AccountPenaltiesResponse createdPenalties = new AccountPenaltiesResponse();
        createdPenalties.setCompanyCode(COMPANY_CODE);
        createdPenalties.setCustomerCode(CUSTOMER_CODE);

        when(accountPenaltiesService.createAccountPenalties(request)).thenReturn(createdPenalties);

        ResponseEntity<?> response = accountPenaltiesController.createPenalty(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdPenalties, response.getBody());
        verify(accountPenaltiesService, times(1)).createAccountPenalties(request);
    }

    @Test
    void createPenaltyThrowsDataException() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        DataException exception = new DataException("Failed to create penalty");
        when(accountPenaltiesService.createAccountPenalties(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                accountPenaltiesController.createPenalty(request));
        assertEquals(exception, thrown);
        verify(accountPenaltiesService, times(1)).createAccountPenalties(request);
    }

    @Test
    void createPenaltyDuplicateButNoPenalties() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setDuplicate(true); // simulate duplicate request

        AccountPenaltiesResponse createdPenalties = new AccountPenaltiesResponse();
        createdPenalties.setPenalties(Collections.emptyList()); // empty list simulates failure

        when(accountPenaltiesService.createAccountPenalties(request)).thenReturn(createdPenalties);

        ResponseEntity<?> response = accountPenaltiesController.createPenalty(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("number_of_penalties should be greater than 1 for duplicate penalties",body
                .get("error"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void createPenaltyDuplicateButNoCreatedPenalties() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setDuplicate(true);

        when(accountPenaltiesService.createAccountPenalties(request)).thenReturn(null);

        ResponseEntity<?> response = accountPenaltiesController.createPenalty(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("number_of_penalties should be greater than 1 for duplicate penalties",
                body.get("error"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void testPenaltyOutstandingAmountWhenNotPaid() {
        PenaltyResponse penaltyCopy = new PenaltyResponse();
        penaltyCopy.setAmount(100.0);

        boolean isPaid = false;
        penaltyCopy.setOutstandingAmount(isPaid ? 0.0 : penaltyCopy.getAmount());

        assertEquals(100.0, penaltyCopy.getOutstandingAmount());
    }

    @Test
    void deleteAccountPenaltiesWithNonNullRequestButNullTransactionReference() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        // transactionReference is null by default

        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response =
                accountPenaltiesController.deleteAccountPenalties(PENALTY_ID, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountPenaltiesService, times(1)).deleteAccountPenalties(PENALTY_ID);
    }

}
