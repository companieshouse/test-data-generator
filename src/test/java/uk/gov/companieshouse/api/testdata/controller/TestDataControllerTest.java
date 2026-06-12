package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyDeleteRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PenaltyResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.service.FilingHistoryService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "NI23456";
    private static final String PENALTY_REFERENCE = "A1234567";
    private static final String SIC_ACTIVITY_ID = "6242bbbbafaaaa93274b2efd";
    private static final String TRANSACTION_ID = "412123-412123-412123";

    @Mock
    private TestDataService testDataService;

    @InjectMocks
    private TestDataController testDataController;

    @Mock
    private FilingHistoryService filingHistoryService;

    @Mock
    private VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService;

    @Test
    void deleteAppealSuccess() throws Exception {
        DeleteAppealsRequest request = new DeleteAppealsRequest();
        request.setCompanyNumber("12345678");
        request.setPenaltyReference("PR123");

        when(testDataService.deleteAppealsData(
                request.getCompanyNumber(), request.getPenaltyReference()))
                .thenReturn(true);

        ResponseEntity<Void> response = testDataController.deleteAppeal(request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1))
                .deleteAppealsData(request.getCompanyNumber(), request.getPenaltyReference());
    }

    @Test
    void deleteAppealNotFound() throws Exception {
        DeleteAppealsRequest request = new DeleteAppealsRequest();
        request.setCompanyNumber("12345678");
        request.setPenaltyReference("PR123");

        when(testDataService.deleteAppealsData(
                request.getCompanyNumber(), request.getPenaltyReference()))
                .thenReturn(false);

        ResponseEntity<Void> response = testDataController.deleteAppeal(request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(1))
                .deleteAppealsData(request.getCompanyNumber(), request.getPenaltyReference());
    }

    @Test
    void deleteAppealBadRequest() throws Exception {
        ResponseEntity<Void> response = testDataController.deleteAppeal(null);

        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(testDataService, times(0)).deleteAppealsData(anyString(), anyString());
    }

    @Test
    void getAccountPenaltyNotFound() throws Exception {
        when(this.testDataService.getAccountPenaltiesData(PENALTY_ID))
                .thenThrow(new NoDataFoundException("no account penalties"));

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.getAccountPenalties(PENALTY_ID, PENALTY_REFERENCE);
        });
    }

    @Test
    void getAccountPenalties() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setId(PENALTY_ID);

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();

        when(this.testDataService.getAccountPenaltiesData(request.getId()))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response = this.testDataController
                .getAccountPenalties(request.getId(), null);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesNotFound() throws Exception {
        String penaltyId = PENALTY_ID;

        when(this.testDataService.getAccountPenaltiesData(penaltyId))
                .thenThrow(new NoDataFoundException("Account penalties not found"));

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.getAccountPenalties(penaltyId, null);
        });
    }

    @Test
    void getAccountPenaltiesByCustomerCodeAndCompanyCode() throws Exception {
        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();

        when(this.testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response = this.testDataController
                .getAccountPenaltiesByCustomerCodeAndCompanyCode(CUSTOMER_CODE, COMPANY_CODE);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesByCustomerCodeAndCompanyCodeNotFound() throws Exception {
        when(this.testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE))
                .thenThrow(new NoDataFoundException("Account penalties not found"));

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.getAccountPenaltiesByCustomerCodeAndCompanyCode(CUSTOMER_CODE, COMPANY_CODE);
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

        when(this.testDataService.updateAccountPenaltiesData(PENALTY_REFERENCE, request))
                .thenReturn(accountPenaltiesResponse);
        ResponseEntity<AccountPenaltiesResponse> response = this.testDataController
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

        when(testDataService.getAccountPenaltiesData(PENALTY_ID)).thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response =
                testDataController.getAccountPenalties(PENALTY_ID, PENALTY_REFERENCE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getPenalties().size());
        assertEquals(PENALTY_REFERENCE, response.getBody().getPenalties().get(0).getTransactionReference());
        verify(testDataService, times(1)).getAccountPenaltiesData(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltyByReferenceSuccess() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        when(testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = testDataController.deleteAccountPenalties(PENALTY_ID, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE);
    }

    @Test
    void deleteAccountPenaltyByReferenceNotFound() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataController.deleteAccountPenalties(PENALTY_ID, request));
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE);
    }

    @Test
    void deleteAccountPenaltyByReferenceOtherError() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        DataException exception = new DataException("error during deletion");
        when(testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteAccountPenalties(PENALTY_ID, request));
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteAccountPenaltyByReference(PENALTY_ID, "A1234567");
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
        when(this.testDataService.updateAccountPenaltiesData(penaltyRef, request))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.updateAccountPenalties(penaltyRef, request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAccountPenaltiesSuccess() throws Exception {
        when(this.testDataService.deleteAccountPenaltiesData(PENALTY_ID))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = testDataController.deleteAccountPenalties(PENALTY_ID, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteAccountPenaltiesData(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesNotFound() throws Exception {
        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(this.testDataService.deleteAccountPenaltiesData(PENALTY_ID)).thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.deleteAccountPenalties(PENALTY_ID, null));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAccountPenaltiesOtherError() throws Exception {
        DataException exception = new DataException("error during deletion");
        when(this.testDataService.deleteAccountPenaltiesData(PENALTY_ID)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.deleteAccountPenalties(PENALTY_ID, null));
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

        when(testDataService.createPenaltyData(request)).thenReturn(createdPenalties);

        ResponseEntity<?> response = testDataController.createPenalty(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdPenalties, response.getBody());
        verify(testDataService, times(1)).createPenaltyData(request);
    }

    @Test
    void createPenaltyThrowsDataException() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        DataException exception = new DataException("Failed to create penalty");
        when(testDataService.createPenaltyData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createPenalty(request));
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).createPenaltyData(request);
    }

    @Test
    void createPenaltyDuplicateButNoPenalties() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setDuplicate(true); // simulate duplicate request

        AccountPenaltiesResponse createdPenalties = new AccountPenaltiesResponse();
        createdPenalties.setPenalties(Collections.emptyList()); // empty list simulates failure

        when(testDataService.createPenaltyData(request)).thenReturn(createdPenalties);

        ResponseEntity<?> response = testDataController.createPenalty(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("number_of_penalties should be greater than 1 for duplicate penalties", body.get("error"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void createPenaltyDuplicateButNoCreatedPenalties() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setDuplicate(true);

        when(testDataService.createPenaltyData(request)).thenReturn(null);

        ResponseEntity<?> response = testDataController.createPenalty(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("number_of_penalties should be greater than 1 for duplicate penalties", body.get("error"));
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
    void getPostcodeSuccess() throws Exception {
        String country = "England";
        PostcodesResponse postcodesResponse =
                new PostcodesResponse(12, "Thoroughfare Name", "Dependent Locality",
                        "Locality Post Town", "ABC 123");

        when(testDataService.getPostcodes(country)).thenReturn(postcodesResponse);

        ResponseEntity<PostcodesResponse> response = testDataController.getPostcode(country);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postcodesResponse, response.getBody());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeNoDataFound() throws Exception {
        String country = "UnknownCountry";

        when(testDataService.getPostcodes(country)).thenReturn(null);

        ResponseEntity<PostcodesResponse> response = testDataController.getPostcode(country);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeDataException() throws Exception {
        String country = "ErrorCountry";

        when(testDataService.getPostcodes(country))
                .thenThrow(new DataException("Error retrieving postcodes"));

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.getPostcode(country));

        assertEquals("Error retrieving postcodes", thrown.getMessage());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void testGetPostcodeIsNull() throws Exception {
        ResponseEntity<PostcodesResponse> response = testDataController.getPostcode(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(0)).getPostcodes(anyString());
    }


    @Test
    void createTransaction() throws Exception {
        TransactionsRequest request = new TransactionsRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setReference("ACSP Registration");

        TransactionsResponse txn = new TransactionsResponse("rsf3pdwywvse5yz55mfodfx8","email@email.com" ,"forename","surname","resumeURI","status", "250788-250788-250788");
        when(this.testDataService.createTransactionData(request)).thenReturn(txn);
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

        when(this.testDataService.createTransactionData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createTransaction(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteTransaction() throws Exception {
        when(this.testDataService.deleteTransaction(TRANSACTION_ID))
                .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                this.testDataController.deleteTransaction(TRANSACTION_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteTransaction(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionNotFound() throws Exception {
        when(this.testDataService.deleteTransaction(TRANSACTION_ID))
                .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                this.testDataController.deleteTransaction(TRANSACTION_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(TRANSACTION_ID,
                Objects.requireNonNull(response.getBody()).get("transaction_id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, times(1)).deleteTransaction(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionException() throws Exception {
        Throwable exception = new DataException("Error deleting transaction");

        when(this.testDataService.deleteTransaction(TRANSACTION_ID))
                .thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> this.testDataController.deleteTransaction(TRANSACTION_ID));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteTransaction(TRANSACTION_ID);
    }

    @Test
    void createCombinedSicActivities() throws Exception {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        CombinedSicActivitiesResponse data =
            new CombinedSicActivitiesResponse(
                new ObjectId().toHexString(),
                "21017",
                "Abbau von Braunkohle");

        when(this.testDataService.createCombinedSicActivitiesData(spec))
            .thenReturn(data);

        ResponseEntity<CombinedSicActivitiesResponse> response =
            this.testDataController.createCombinedSicActivities(spec);

        assertEquals(data, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(testDataService, times(1)).createCombinedSicActivitiesData(spec);
    }

    @Test
    void createCombinedSicActivitiesException() throws Exception {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        Throwable exception = new DataException("Error creating combined sic activities");

        when(this.testDataService.createCombinedSicActivitiesData(spec))
            .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class,
            () -> this.testDataController.createCombinedSicActivities(spec));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).createCombinedSicActivitiesData(spec);
    }

    @Test
    void deleteCombinedSicActivities() throws Exception {
        when(this.testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID))
            .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
            this.testDataController.deleteCombinedSicActivities(SIC_ACTIVITY_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesNotFound() throws Exception {
        when(this.testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID))
            .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
            this.testDataController.deleteCombinedSicActivities(SIC_ACTIVITY_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(SIC_ACTIVITY_ID,
            Objects.requireNonNull(response.getBody()).get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, times(1)).deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesException() throws Exception {
        Throwable exception = new DataException("Error deleting combined sic activities");

        when(this.testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID))
            .thenThrow(exception);

        DataException thrown = assertThrows(
            DataException.class,
            () -> this.testDataController.deleteCombinedSicActivities(SIC_ACTIVITY_ID));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);
    }

    @Test
    void createAdminPermissions_success() throws Exception {
        AdminPermissionsRequest spec = new AdminPermissionsRequest();
        AdminPermissionsResponse data = new AdminPermissionsResponse("permId", "groupName");

        when(testDataService.createAdminPermissionsData(spec)).thenReturn(data);

        ResponseEntity<AdminPermissionsResponse> response = testDataController.createAdminPermissions(spec);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(data, response.getBody());
        verify(testDataService, times(1)).createAdminPermissionsData(spec);
    }

    @Test
    void createAdminPermissions_throwsException() throws Exception {
        AdminPermissionsRequest spec = new AdminPermissionsRequest();
        DataException exception = new DataException("Error creating admin permissions");

        when(testDataService.createAdminPermissionsData(spec)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createAdminPermissions(spec)
        );
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).createAdminPermissionsData(spec);
    }

    @Test
    void deleteAdminPermissions_success() throws Exception {
        String id = "permId";
        when(testDataService.deleteAdminPermissionsData(id)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = testDataController.deleteAdminPermissions(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(testDataService, times(1)).deleteAdminPermissionsData(id);
    }

    @Test
    void deleteAdminPermissions_notFound() throws Exception {
        String id = "permId";
        when(testDataService.deleteAdminPermissionsData(id)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = testDataController.deleteAdminPermissions(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().get("admin-permissions-id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(testDataService, times(1)).deleteAdminPermissionsData(id);
    }

    @Test
    void deleteAdminPermissions_throwsException() throws Exception {
        String id = "permId";
        DataException exception = new DataException("Error deleting admin permissions");
        when(testDataService.deleteAdminPermissionsData(id)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteAdminPermissions(id)
        );
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteAdminPermissionsData(id);
    }

    @Test
    void getIdentityVerification_serviceReturnsData_returnsOk() throws Exception {
        final String email = "user@example.com";
        var data = new IdentityVerificationResponse("identity-id-123", "UVID-ABC", "Firstname", "Lastname");

        when(this.verifiedIdentityService.getIdentityVerificationData(email))
                .thenReturn(data);

        ResponseEntity<IdentityVerificationResponse> response = this.testDataController.getIdentityVerification(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(data, response.getBody());

        verify(verifiedIdentityService, times(1)).getIdentityVerificationData(email);
    }

    @Test
    void getIdentityVerification_serviceReturnsNull_throwsNoDataFoundException() throws Exception {
        final String email = "missing@example.com";

        when(this.verifiedIdentityService.getIdentityVerificationData(email))
                .thenReturn(null);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.getIdentityVerification(email));

        assertEquals("No identity verification found for email: " + email, thrown.getMessage());

        verify(verifiedIdentityService, times(1)).getIdentityVerificationData(email);
    }

    @Test
    void deleteItemGroups() throws Exception {
        final String orderNumber = "ORD-1776329853";
        when(this.testDataService.deleteItemGroupsData(orderNumber)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteItemGroups(orderNumber);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService).deleteItemGroupsData(orderNumber);
    }


    @Test
    void deleteItemGroupsNoData() throws Exception {
        final String orderNumber = "ORD-1776329853";

        when(this.testDataService.deleteItemGroupsData(orderNumber)).thenReturn(false);
        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteItemGroups(orderNumber);

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("orderNumber", orderNumber);
        expectedBody.put("message", "Item Groups Not Found");
        expectedBody.put("status", HttpStatus.NOT_FOUND);

        assertEquals(expectedBody, response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService).deleteItemGroupsData(orderNumber);
    }

    @Test
    void deleteItemGroupsException() throws Exception {
        final String orderNumber = "ORD-1776329853";
        DataException exception = new DataException("Error deleting item groups");

        when(testDataService.deleteItemGroupsData(orderNumber)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteItemGroups(orderNumber));
        assertEquals(exception, thrown);
        verify(testDataService).deleteItemGroupsData(orderNumber);
    }

    @Test
    void deleteAccountPenaltiesWithNonNullRequestButNullTransactionReference() throws Exception {
        PenaltyDeleteRequest request = new PenaltyDeleteRequest();
        // transactionReference is null by default

        when(testDataService.deleteAccountPenaltiesData(PENALTY_ID))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = testDataController.deleteAccountPenalties(PENALTY_ID, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteAccountPenaltiesData(PENALTY_ID);
    }
    @Test
    void getCompanyFilingHistoryNoParamsReturnsBadRequest() {
        ResponseEntity<Object> response =
                testDataController.getCompanyFilingHistory(null, null);

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
                testDataController.getCompanyFilingHistory(companyNumber, null);

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
                testDataController.getCompanyFilingHistory(companyNumber, null);

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
                testDataController.getCompanyFilingHistory(null, filingHistoryId);

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
                testDataController.getCompanyFilingHistory(null, filingHistoryId);

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
                testDataController.deleteCompanyFilingHistory(companyNumber);

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
                testDataController.deleteCompanyFilingHistory(companyNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(companyNumber, body.get("companyNumber"));
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));

        verify(filingHistoryService, times(1))
                .deleteCompanyFilingHistory(companyNumber);
    }

}
