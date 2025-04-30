package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(repository.findAllByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE))
                .thenReturn(Optional.of(accountPenalties));

        AccountPenaltiesData result = service.getAccountPenalties(COMPANY_CODE, CUSTOMER_CODE);

        assertEquals(accountPenalties.getCustomerCode(), result.getCustomerCode());
        assertEquals(accountPenalties.getCompanyCode(), result.getCompanyCode());
        assertFalse(accountPenalties.getPenalties().isEmpty());
        verify(repository, times(1))
                .findAllByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE);
    }

    @Test
    void testGetAccountPenaltiesNotFound() {
        when(repository.findAllByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.getAccountPenalties(COMPANY_CODE, CUSTOMER_CODE));
        assertEquals("no account penalties", exception.getMessage());
    }

    @Test
    void testDeleteAccountPenaltiesSuccess() throws NoDataFoundException {
        when(repository.findAllByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE))
                .thenReturn(Optional.of(ACCOUNT_PENALTIES));
        when(repository.deleteByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE))
                .thenReturn(Optional.of(ACCOUNT_PENALTIES));

        service.deleteAccountPenalties(COMPANY_CODE, CUSTOMER_CODE);

        verify(repository, times(1))
                .deleteByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE);
    }

    @Test
    void testDeleteAccountPenaltiesNotFound() {
        when(repository.findAllByCompanyCodeAndCustomerCode(COMPANY_CODE, CUSTOMER_CODE))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.deleteAccountPenalties(COMPANY_CODE, CUSTOMER_CODE));

        assertEquals("no account penalties", exception.getMessage());
    }

    public static Stream<Arguments> updateArguments() {
        return Stream.of(
                Arguments.of(NOW.minusSeconds(10), NOW.minusSeconds(10), true, 0.0, 0.0),
                Arguments.of(null, NOW.minusSeconds(10), null, null, null)
        );
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

}
