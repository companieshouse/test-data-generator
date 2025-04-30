package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalties;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalty;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltyData;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.repository.AccountPenaltiesRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;

@Service
public class AccountPenaltiesServiceImpl implements AccountPenaltiesService {

    @Autowired
    private AccountPenaltiesRepository repository;

    private static final NoDataFoundException PENALTY_NOT_FOUND_EX =
            new NoDataFoundException("penalty not found");

    @Override
    public AccountPenaltiesData getAccountPenalty(String companyCode, String customerCode,
            String penaltyRef) throws NoDataFoundException {
        var accountPenalties =
                repository.findPenalty(companyCode, customerCode, penaltyRef)
                        .orElseThrow(() -> PENALTY_NOT_FOUND_EX);

        return mapToAccountPenaltiesData(accountPenalties);
    }

    @Override
    public AccountPenaltiesData getAccountPenalties(String companyCode, String customerCode)
            throws NoDataFoundException {
        var accountPenalties =
                repository.findAllByCompanyCodeAndCustomerCode(companyCode, customerCode)
                        .orElseThrow(() -> new NoDataFoundException("no account penalties"));

        return mapToAccountPenaltiesData(accountPenalties);
    }

    @Override
    public AccountPenaltiesData updateAccountPenalties(String penaltyRef,
            UpdateAccountPenaltiesRequest request) throws NoDataFoundException, DataException {

        AccountPenalties accountPenalties =
                repository.findPenalty(
                                request.getCompanyCode(), request.getCustomerCode(), penaltyRef)
                        .orElseThrow(() -> PENALTY_NOT_FOUND_EX);

        AccountPenalty updatedPenalty = createPenaltyUpdate(
                accountPenalties.getPenalties().getFirst(),
                request.getIsPaid(), request.getAmount(), request.getOutstandingAmount());

        accountPenalties.setClosedAt(request.getClosedAt());
        if (Objects.nonNull(request.getCreatedAt())) {
            accountPenalties.setCreatedAt(request.getCreatedAt());
        }

        for (int i = 0; i < accountPenalties.getPenalties().size(); i++) {
            if (accountPenalties.getPenalties().get(i)
                    .getTransactionReference().equals(penaltyRef)) {
                accountPenalties.getPenalties().set(i, updatedPenalty);
                break;
            }
        }

        try {
            AccountPenalties updateAccountPenaltyData = repository.save(accountPenalties);
            return mapToAccountPenaltiesData(updateAccountPenaltyData);
        } catch (Exception ex) {
            throw new DataException("failed to update the account penalties");
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenalties(String companyCode, String customerCode)
            throws NoDataFoundException {
        repository.findAllByCompanyCodeAndCustomerCode(companyCode, customerCode)
                        .orElseThrow(() -> new NoDataFoundException("no account penalties"));

        repository.deleteByCompanyCodeAndCustomerCode(companyCode, customerCode);

        return ResponseEntity.noContent().build();
    }

    private AccountPenalty createPenaltyUpdate(AccountPenalty accountPenalty, Boolean isPaid,
            Double amount, Double outStandingAmount) {
        AccountPenalty updatedPenalty = new AccountPenalty();
        updatedPenalty.setIsPaid(isPaid != null ? isPaid : accountPenalty.isPaid());
        updatedPenalty.setAmount(amount != null ? amount : accountPenalty.getAmount());
        updatedPenalty.setOutstandingAmount(outStandingAmount != null ? outStandingAmount
                : accountPenalty.getOutstandingAmount());
        updatedPenalty.setCompanyCode(accountPenalty.getCompanyCode());
        updatedPenalty.setLedgerCode(accountPenalty.getLedgerCode());
        updatedPenalty.setCustomerCode(accountPenalty.getCustomerCode());
        updatedPenalty.setTransactionReference(accountPenalty.getTransactionReference());
        updatedPenalty.setTransactionDate(accountPenalty.getTransactionDate());
        updatedPenalty.setMadeUpDate(accountPenalty.getMadeUpDate());
        updatedPenalty.setTransactionType(accountPenalty.getTransactionType());
        updatedPenalty.setTransactionSubType(accountPenalty.getTransactionSubType());
        updatedPenalty.setTypeDescription(accountPenalty.getTypeDescription());
        updatedPenalty.setDueDate(accountPenalty.getDueDate());
        updatedPenalty.setAccountStatus(accountPenalty.getAccountStatus());
        updatedPenalty.setDunningStatus(accountPenalty.getDunningStatus());

        return updatedPenalty;
    }

    private AccountPenaltiesData mapToAccountPenaltiesData(AccountPenalties accountPenalties) {
        AccountPenaltiesData accountPenaltiesData = new AccountPenaltiesData();
        accountPenaltiesData.setCompanyCode(accountPenalties.getCompanyCode());
        accountPenaltiesData.setCustomerCode(accountPenalties.getCustomerCode());
        accountPenaltiesData.setCreatedAt(accountPenalties.getCreatedAt());
        accountPenaltiesData.setClosedAt(accountPenalties.getClosedAt());

        List<PenaltyData> apd = accountPenalties.getPenalties().stream()
                .map(this::mapToAccountPenaltyData).toList();

        accountPenaltiesData.setPenalties(apd);

        return accountPenaltiesData;
    }

    private PenaltyData mapToAccountPenaltyData(AccountPenalty penalty) {
        PenaltyData penaltyData = new PenaltyData();
        penaltyData.setCompanyCode(penalty.getCompanyCode());
        penaltyData.setCustomerCode(penalty.getCustomerCode());
        penaltyData.setTransactionReference(penalty.getTransactionReference());
        penaltyData.setTransactionDate(penalty.getTransactionDate());
        penaltyData.setMadeUpDate(penalty.getMadeUpDate());
        penaltyData.setAmount(penalty.getAmount());
        penaltyData.setOutstandingAmount(penalty.getOutstandingAmount());
        penaltyData.setIsPaid(penalty.isPaid());
        penaltyData.setAccountStatus(penalty.getAccountStatus());
        penaltyData.setDunningStatus(penalty.getDunningStatus());
        return penaltyData;
    }
}
