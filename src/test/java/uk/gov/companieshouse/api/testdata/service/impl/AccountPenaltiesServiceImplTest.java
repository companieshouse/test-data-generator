package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalties;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalty;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltySpec;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.repository.AccountPenaltiesRepository;

@ExtendWith(MockitoExtension.class)
class AccountPenaltiesServiceImplTest {

    @Mock
    private AccountPenaltiesRepository repository;

    @InjectMocks
    private AccountPenaltiesServiceImpl service;

    private static final AccountPenalties ACCOUNT_PENALTIES = new AccountPenalties();
    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "12345678";
    private static final String PENALTY_REF = "A1234567";
    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final Instant NOW = Instant.now();

    @Test
    void testGetAccountPenaltySuccess() throws NoDataFoundException {
        AccountPenalties accountPenalties = createAccountPenalties();

        when(repository.findPenalty(
                COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF))
                .thenReturn(Optional.of(accountPenalties));

        AccountPenaltiesData result = service.getAccountPenalty(COMPANY_CODE, CUSTOMER_CODE,
                PENALTY_REF);

        assertEquals(accountPenalties.getCustomerCode(), result.getCustomerCode());
        assertEquals(accountPenalties.getCompanyCode(), result.getCompanyCode());
        assertEquals(accountPenalties.getPenalties().getFirst().getTransactionReference(),
                result.getPenalties().getFirst().getTransactionReference());
        verify(repository, times(1))
                .findPenalty(COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF);
    }

    @Test
    void testGetAccountPenaltyNotFound() {
        when(repository.findPenalty(
                COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.getAccountPenalty(COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF));
        assertEquals("penalty not found", exception.getMessage());
    }

    @Test
    void testGetAccountPenaltiesSuccess() throws NoDataFoundException {
        AccountPenalties accountPenalties = createAccountPenalties();

        when(repository.findAllById(PENALTY_ID))
                .thenReturn(Optional.of(accountPenalties));

        AccountPenaltiesData result = service.getAccountPenalties(PENALTY_ID);

        assertEquals(accountPenalties.getCustomerCode(), result.getCustomerCode());
        assertEquals(accountPenalties.getCompanyCode(), result.getCompanyCode());
        assertFalse(accountPenalties.getPenalties().isEmpty());
        verify(repository, times(1))
                .findAllById(PENALTY_ID);
    }

    @Test
    void testGetAccountPenaltiesNotFound() {
        when(repository.findAllById(PENALTY_ID))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.getAccountPenalties(PENALTY_ID));
        assertEquals("no account penalties", exception.getMessage());
    }

    @Test
    void testDeleteAccountPenaltiesSuccess() throws NoDataFoundException {
        when(repository.findAllById(PENALTY_ID)).thenReturn(Optional.of(ACCOUNT_PENALTIES));

        service.deleteAccountPenalties(PENALTY_ID);

        verify(repository, times(1)).deleteById(PENALTY_ID);
    }

    @Test
    void testDeleteAccountPenaltiesNotFound() {
        when(repository.findAllById(PENALTY_ID))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.deleteAccountPenalties(PENALTY_ID));

        assertEquals("no account penalties", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("updateArguments")
    void testUpdateAccountPenaltiesSuccess(Instant createdAt, Instant closedAt, Boolean isPaid,
            Double amount, Double outstandingAmount) throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setCreatedAt(createdAt);
        request.setClosedAt(closedAt);
        request.setIsPaid(isPaid);
        request.setAmount(amount);
        request.setOutstandingAmount(outstandingAmount);

        AccountPenalties accountPenalties = createAccountPenalties();

        when(repository.findPenalty(
                COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF))
                .thenReturn(Optional.of(accountPenalties));

        AccountPenalties updatedPenalties = createAccountPenalties();
        updatedPenalties.setCreatedAt(NOW.plusSeconds(10));
        updatedPenalties.setClosedAt(NOW.plusSeconds(10));
        updatedPenalties.getPenalties().getFirst().setIsPaid(true);
        updatedPenalties.getPenalties().getFirst().setAmount(0.0);
        updatedPenalties.getPenalties().getFirst().setOutstandingAmount(0.0);

        when(repository.save(accountPenalties)).thenReturn(accountPenalties);

        AccountPenaltiesData result = service.updateAccountPenalties(PENALTY_REF, request);

        assertEquals(accountPenalties.getCustomerCode(), result.getCustomerCode());
        assertEquals(accountPenalties.getCompanyCode(), result.getCompanyCode());
        assertEquals(accountPenalties.getPenalties().getFirst().getTransactionReference(),
                result.getPenalties().getFirst().getTransactionReference());
        verify(repository, times(1))
                .findPenalty(COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF);
        verify(repository, times(1))
                .save(accountPenalties);
    }

    @Test
    void testUpdateAccountPenaltiesNotFound() {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        when(repository.findPenalty(COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.updateAccountPenalties(PENALTY_REF, request));

        assertEquals("penalty not found", exception.getMessage());
    }

    @Test
    void testUpdateAccountPenaltiesErrorOnSave() {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        AccountPenalties accountPenalties = createAccountPenalties();

        when(repository.findPenalty(
                COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF))
                .thenReturn(Optional.of(accountPenalties));

        doThrow(ConstraintViolationException.class)
                .when(repository).save(accountPenalties);

        DataException exception =
                assertThrows(DataException.class,
                        () -> service.updateAccountPenalties(PENALTY_REF, request));

        verify(repository, times(1))
                .findPenalty(COMPANY_CODE, CUSTOMER_CODE, PENALTY_REF);
        verify(repository, times(1))
                .save(accountPenalties);

        assertEquals("failed to update the account penalties", exception.getMessage());
    }

    @Test
    void createAccountPenalties_success() throws DataException {
        PenaltySpec penaltySpec = new PenaltySpec();
        penaltySpec.setCompanyCode("LP");
        penaltySpec.setCustomerCode("NI23456");
        penaltySpec.setNumberOfPenalties(2);
        penaltySpec.setAmount(100.0);
        penaltySpec.setIsPaid(false);

        AccountPenalties savedEntity = createAccountPenalties();

        when(repository.save(any(AccountPenalties.class))).thenReturn(savedEntity);

        AccountPenaltiesData result = service.createAccountPenalties(penaltySpec);

        assertNotNull(result);
        assertEquals(savedEntity.getCompanyCode(), result.getCompanyCode());
        assertEquals(savedEntity.getCustomerCode(), result.getCustomerCode());
        verify(repository, times(1)).save(any(AccountPenalties.class));
    }

    @Test
    void createAccountPenalties_repositoryThrowsException() {
        PenaltySpec penaltySpec = new PenaltySpec();
        penaltySpec.setCompanyCode("LP");
        penaltySpec.setCustomerCode("NI23456");

        when(repository.save(any(AccountPenalties.class))).thenThrow(new RuntimeException("DB error"));

        DataException ex = assertThrows(DataException.class, () -> service.createAccountPenalties(penaltySpec));
        assertTrue(ex.getMessage().contains("Failed to create account penalties"));
        verify(repository, times(1)).save(any(AccountPenalties.class));
    }

    private static AccountPenalties createAccountPenalties() {
        AccountPenalty penalty = getAccountPenalty();
        List<AccountPenalty> penalties = new ArrayList<>();
        penalties.add(penalty);

        AccountPenalties accountPenalties = new AccountPenalties();
        accountPenalties.setId(new ObjectId());
        accountPenalties.setCompanyCode(COMPANY_CODE);
        accountPenalties.setCustomerCode(CUSTOMER_CODE);
        accountPenalties.setCreatedAt(NOW);
        accountPenalties.setClosedAt(null);
        accountPenalties.setPenalties(penalties);

        return accountPenalties;
    }

    private static AccountPenalty getAccountPenalty() {
        AccountPenalty penalty = new AccountPenalty();
        penalty.setCompanyCode(COMPANY_CODE);
        penalty.setCustomerCode(CUSTOMER_CODE);
        penalty.setTransactionReference(PENALTY_REF);
        penalty.setTransactionDate("2025-02-25");
        penalty.setMadeUpDate("2025-02-12");
        penalty.setAmount(15.0);
        penalty.setOutstandingAmount(150.0);
        penalty.setIsPaid(false);
        penalty.setAccountStatus("CHS");
        penalty.setDunningStatus("PEN1");
        penalty.setTransactionType("1");
        penalty.setTransactionSubType("C1");
        penalty.setTransactionDate("2025-02-25");
        penalty.setDueDate("2025-02-12");
        penalty.setTypeDescription("PEN2");
        penalty.setLedgerCode("EW");
        return penalty;
    }

    public static Stream<Arguments> updateArguments() {
        return Stream.of(
                Arguments.of(NOW.minusSeconds(10), NOW.minusSeconds(10), true, 0.0, 0.0),
                Arguments.of(null, NOW.minusSeconds(10), null, null, null),
                Arguments.of(null, NOW.minusSeconds(10), true, null, null)
        );
    }
}
