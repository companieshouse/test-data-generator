package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolationException;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;


@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "12345678";
    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final String PENALTY_REF = "A1234567";
    private static final String USER_ID = "sZJQcNxzPvcwcqDwpUyRKNvVbcq";
    private static final String CERTIFICATES_ID = "CRT-123456-789012";
    private static final String CERTIFIED_COPIES_ID = "CCD-123456-789012";
    private static final String MISSING_IMAGE_DELIVERIES_ID = "MID-123456-789012";
    private static final String SIC_ACTIVITY_ID = "6242bbbbafaaaa93274b2efd";
    private static final String TRANSACTION_ID = "903085-903085-903085";

    @Mock
    private AppealsService appealsService;
    @Mock
    private DataService<CombinedSicActivitiesResponse, CombinedSicActivitiesRequest> combinedSicActivitiesService;
    @Mock
    private AccountPenaltiesService accountPenaltiesService;
    @Mock
    private PostcodeService postcodeService;
    @Mock private DataService<TransactionsResponse, TransactionsRequest> transactionService;
    @Mock
    private ItemGroupsService itemGroupsService;

    @InjectMocks
    private TestDataServiceImpl testDataService;

    @Test
    void deleteAppealsDataSuccess() throws DataException {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";

        when(appealsService.delete(companyNumber, penaltyReference)).thenReturn(true);

        boolean result = testDataService.deleteAppealsData(companyNumber, penaltyReference);

        assertTrue(result);
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void deleteAppealsDataFailure() throws DataException {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";

        when(appealsService.delete(companyNumber, penaltyReference)).thenReturn(false);

        boolean result = testDataService.deleteAppealsData(companyNumber, penaltyReference);

        assertFalse(result);
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void deleteAppealsDataThrowsException() {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";
        RuntimeException ex = new RuntimeException("error");

        when(appealsService.delete(companyNumber, penaltyReference)).thenThrow(ex);
        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteAppealsData(companyNumber, penaltyReference));
        assertEquals("Error deleting appeals data", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void getAccountPenaltiesData() throws Exception {
        testDataService.getAccountPenaltiesData(PENALTY_ID);
        verify(accountPenaltiesService).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void getAccountPenaltiesDataNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error retrieving account penalties - not found");
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void getAccountPenaltiesDataByCustomerCodeAndCompanyCode() throws Exception {
        testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE);
        verify(accountPenaltiesService).getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE);
    }

    @Test
    void getAccountPenaltiesDataByCustomerCodeAndCompanyCodeNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error retrieving account penalties - not found");
        when(accountPenaltiesService.getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void updateAccountPenaltiesData() throws Exception {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        testDataService.updateAccountPenaltiesData(PENALTY_REF, request);
        verify(accountPenaltiesService).updateAccountPenalties(PENALTY_REF, request);
    }

    @Test
    void updateAccountPenaltiesDataNotFoundException() throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        NoDataFoundException ex = new NoDataFoundException(
                "Error updating account penalties - not found");
        when(accountPenaltiesService.updateAccountPenalties(PENALTY_REF, request))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.updateAccountPenaltiesData(PENALTY_REF, request));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void updateAccountPenaltiesDataDataException() throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        DataException ex = new DataException("Error updating account penalties");
        when(accountPenaltiesService.updateAccountPenalties(PENALTY_REF, request))
                .thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.updateAccountPenaltiesData(PENALTY_REF, request));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteAccountPenaltiesData() throws Exception {
        testDataService.deleteAccountPenaltiesData(PENALTY_ID);
        verify(accountPenaltiesService).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesDataNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error deleting account penalties - not found");
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataException() throws NoDataFoundException {
        DataException ex = new DataException("Error deleting account penalties");
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(ConstraintViolationException.class);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void createPenaltyDataSuccess() throws DataException {
        PenaltyRequest penaltyRequest = new PenaltyRequest();
        penaltyRequest.setCompanyCode("LP");
        penaltyRequest.setCustomerCode("NI23456");

        AccountPenaltiesResponse expectedData = new AccountPenaltiesResponse();
        expectedData.setCompanyCode("LP");
        expectedData.setCustomerCode("NI23456");

        when(accountPenaltiesService.createAccountPenalties(penaltyRequest)).thenReturn(expectedData);

        AccountPenaltiesResponse result = testDataService.createPenaltyData(penaltyRequest);

        assertEquals(expectedData, result);
        verify(accountPenaltiesService, times(1)).createAccountPenalties(penaltyRequest);
    }

    @Test
    void createPenaltyDataThrowsException() throws DataException {
        PenaltyRequest penaltyRequest = new PenaltyRequest();
        penaltyRequest.setCompanyCode("LP");
        penaltyRequest.setCustomerCode("NI23456");

        DataException ex = new DataException("creation failed");
        when(accountPenaltiesService.createAccountPenalties(penaltyRequest)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createPenaltyData(penaltyRequest));
        assertEquals("Error creating account penalties", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
        verify(accountPenaltiesService, times(1)).createAccountPenalties(penaltyRequest);
    }

    @Test
    void getAccountPenaltiesDataDelegatesToService() throws Exception {
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenReturn(new AccountPenaltiesResponse());
        AccountPenaltiesResponse result = testDataService.getAccountPenaltiesData(PENALTY_ID);
        assertNotNull(result);
        verify(accountPenaltiesService).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void getAccountPenaltiesDataThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error retrieving account penalties - not found", ex.getMessage());
    }

    @Test
    void createPenaltyDataDelegatesToService() throws Exception {
        PenaltyRequest spec = new PenaltyRequest();
        AccountPenaltiesResponse data = new AccountPenaltiesResponse();
        when(accountPenaltiesService.createAccountPenalties(spec)).thenReturn(data);
        AccountPenaltiesResponse result = testDataService.createPenaltyData(spec);
        assertEquals(data, result);
        verify(accountPenaltiesService).createAccountPenalties(spec);
    }

    @Test
    void createPenaltyDataThrowsDataException() throws Exception {
        PenaltyRequest spec = new PenaltyRequest();
        when(accountPenaltiesService.createAccountPenalties(spec))
                .thenThrow(new DataException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.createPenaltyData(spec));
        assertEquals("Error creating account penalties", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataDelegatesToService() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = testDataService.deleteAccountPenaltiesData(PENALTY_ID);
        assertNotNull(result);
        verify(accountPenaltiesService).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesDataThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error deleting account penalties - not found", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataThrowsDataException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(new RuntimeException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error deleting account penalties", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltyByReferenceDelegatesToService() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF);
        assertNotNull(result);
        verify(accountPenaltiesService).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF);
    }

    @Test
    void deleteAccountPenaltyByReferenceThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF));
        assertEquals("Error deleting account penalty - not found", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltyByReferenceThrowsDataException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenThrow(new RuntimeException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF));
        assertEquals("Error deleting account penalty", ex.getMessage());
    }

    @Test
    void createTransactionData() throws DataException {
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        transactionsRequest.setUserId("Test12454");
        transactionsRequest.setReference("ACSP Registration");
        TransactionsResponse txn = new TransactionsResponse("Test12454","email@email.com" ,"forename","surname","resumeURI","status", "250788-250788-250788");
        when(transactionService.create(transactionsRequest)).thenReturn(txn);
        TransactionsResponse result = testDataService.createTransactionData(transactionsRequest);
        assertEquals(txn, result);
    }

    @Test
    void createTransactionDataException() throws DataException {
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        transactionsRequest.setUserId("Test12454");
        transactionsRequest.setReference("ACSP Registration");
        DataException ex = new DataException("creation failed");
        when(transactionService.create(transactionsRequest)).thenThrow(ex);
        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createTransactionData(transactionsRequest));
        assertEquals("Error creating transaction", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
    }

    @Test
    void deleteTransactionData() throws DataException {
        when(transactionService.delete(TRANSACTION_ID)).thenReturn(true);

        boolean result = testDataService.deleteTransaction(TRANSACTION_ID);

        assertTrue(result);
        verify(transactionService).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionDataFailure() throws DataException {
        when(transactionService.delete(TRANSACTION_ID)).thenReturn(false);

        boolean result = testDataService.deleteTransaction(TRANSACTION_ID);

        assertFalse(result);
        verify(transactionService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(transactionService.delete(TRANSACTION_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteTransaction(TRANSACTION_ID));

        assertEquals("Error deleting transaction", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(transactionService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void createCombinedSicActivitiesData() throws DataException {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        CombinedSicActivitiesResponse expectedData =
                new CombinedSicActivitiesResponse(
                        new ObjectId().toHexString(),
                        "12345",
                        "Abbau von Braunkohle"
                );

        when(combinedSicActivitiesService.create(any(CombinedSicActivitiesRequest.class)))
                .thenReturn(expectedData);

        CombinedSicActivitiesResponse result =
                testDataService.createCombinedSicActivitiesData(spec);

        assertNotNull(result);
        assertEquals("12345", result.getSicCode());
        assertEquals("Abbau von Braunkohle", result.getSicDescription());

        verify(combinedSicActivitiesService).create(spec);
    }

    @Test
    void createCombinedSicActivitiesDataException() throws DataException {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setSicDescription("Test Sic Description");

        when(combinedSicActivitiesService.create(any(CombinedSicActivitiesRequest.class)))
                .thenThrow(new DataException("Error creating Sic code and keyword"));

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCombinedSicActivitiesData(spec));

        assertEquals("Error creating Sic code and keyword", exception.getMessage());
    }

    @Test
    void deleteCombinedSicActivitiesData() throws DataException {
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenReturn(true);

        boolean result = testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);

        assertTrue(result);
        verify(combinedSicActivitiesService).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesDataFailure() throws DataException {
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenReturn(false);

        boolean result = testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);

        assertFalse(result);
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID));

        assertEquals("Error deleting appeals data", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteItemGroupsDataSuccess() throws DataException {
        String orderNumber = "ORD-1234-5678";

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenReturn(true);

        boolean result = testDataService.deleteItemGroupsData(orderNumber);

        assertTrue(result);
        verify(itemGroupsService, times(1)).deleteItemGroups(orderNumber);
    }

    @Test
    void deleteItemGroupsDataReturnsFalse() throws DataException {
        String orderNumber = "ORD-0000-0000";

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenReturn(false);

        boolean result = testDataService.deleteItemGroupsData(orderNumber);

        assertFalse(result);
        verify(itemGroupsService, times(1)).deleteItemGroups(orderNumber);
    }

    @Test
    void deleteItemGroupsDataThrowsException() {
        String orderNumber = "ORD-ERROR-1234";
        RuntimeException cause = new RuntimeException("Mongo failure");

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenThrow(cause);

        DataException exception = assertThrows(
                DataException.class,
                () -> testDataService.deleteItemGroupsData(orderNumber)
        );

        assertEquals("Error deleting Item Groups", exception.getMessage());
        assertSame(cause, exception.getCause());
        verify(itemGroupsService, times(1)).deleteItemGroups(orderNumber);
    }

}
