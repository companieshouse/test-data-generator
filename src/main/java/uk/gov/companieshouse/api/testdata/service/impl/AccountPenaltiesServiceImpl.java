package uk.gov.companieshouse.api.testdata.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalties;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalty;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltyData;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltySpec;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.repository.AccountPenaltiesRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;


@Service
public class AccountPenaltiesServiceImpl implements AccountPenaltiesService {

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AccountPenaltiesServiceImpl.class));
    private static final String EXCEPTION_MSG = "no account penalties";

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

        accountPenalties.setPenalties(filterByPenaltyRef(penaltyRef, accountPenalties));

        return mapToAccountPenaltiesData(accountPenalties);
    }

    private static List<AccountPenalty> filterByPenaltyRef(String penaltyRef,
                                                           AccountPenalties accountPenalties) {
        return accountPenalties.getPenalties().stream()
                .filter(p -> p.getTransactionReference().equals(penaltyRef))
                .collect(Collectors.toList());
    }

    @Override
    public AccountPenaltiesData getAccountPenalties(String id)
            throws NoDataFoundException {
        var accountPenalties =
                repository.findAllById(id)
                        .orElseThrow(() -> new NoDataFoundException(EXCEPTION_MSG));

        return mapToAccountPenaltiesData(accountPenalties);
    }

    @Override
    public AccountPenaltiesData updateAccountPenalties(
            String penaltyRef, UpdateAccountPenaltiesRequest request)
            throws NoDataFoundException, DataException {

        var accountPenalties =
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

        for (var i = 0; i < accountPenalties.getPenalties().size(); i++) {
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
    public ResponseEntity<Void> deleteAccountPenalties(String id)
            throws NoDataFoundException {
        Optional<AccountPenalties> accountPenalties =
                repository.findAllById(id);

        if (accountPenalties.isEmpty()) {
            throw new NoDataFoundException(EXCEPTION_MSG);
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenaltyByReference(
            String id, String transactionReference)
            throws NoDataFoundException {
        Optional<AccountPenalties> accountPenaltiesOpt = repository.findAllById(id);

        if (accountPenaltiesOpt.isEmpty()) {
            throw new NoDataFoundException(EXCEPTION_MSG);
        }

        var accountPenalties = accountPenaltiesOpt.get();
        boolean removed = accountPenalties.getPenalties().removeIf(
                p -> transactionReference.equals(p.getTransactionReference()));

        if (!removed) {
            throw new NoDataFoundException("penalty not found");
        }

        repository.save(accountPenalties);

        return ResponseEntity.noContent().build();
    }

    public AccountPenaltiesData createAccountPenalties(
            PenaltySpec penaltySpec) throws DataException {
        var accountPenalties = new AccountPenalties();
        accountPenalties.setId(new ObjectId());
        LOG.info("Creating account penalties with ID: " + accountPenalties.getId());

        accountPenalties.setCompanyCode(penaltySpec.getCompanyCode());
        accountPenalties.setCustomerCode(penaltySpec.getCustomerCode());
        accountPenalties.setCreatedAt(Instant.now());
        accountPenalties.setClosedAt(Instant.now());

        accountPenalties.setPenalties(createPenaltiesList(penaltySpec));

        try {
            return mapToAccountPenaltiesData(repository.save(accountPenalties));
        } catch (Exception ex) {
            throw new DataException("Failed to create account penalties", ex);
        }
    }

    private List<AccountPenalty> createPenaltiesList(PenaltySpec penaltySpec) {
        int numberOfPenalties = Optional.ofNullable(penaltySpec.getNumberOfPenalties()).orElse(1);
        double baseAmount = Optional.ofNullable(penaltySpec.getAmount()).orElse(375.0);
        boolean isPaid = Optional.ofNullable(penaltySpec.getIsPaid()).orElse(false);

        List<AccountPenalty> penalties = new ArrayList<>();

        for (var i = 0; i < numberOfPenalties; i++) {
            var penalty = new AccountPenalty();

            // Set common penalty properties
            penalty.setCompanyCode(getDefaultIfBlank(penaltySpec.getCompanyCode(), "LP"));
            penalty.setCustomerCode(penaltySpec.getCustomerCode());
            penalty.setLedgerCode(getDefaultIfBlank(penaltySpec.getLedgerCode(), "SC"));
            penalty.setTransactionReference(generateTransactionReference());
            penalty.setTransactionDate(getFormattedDate(1));
            penalty.setMadeUpDate(getFormattedDate(2));
            penalty.setAmount(calculatePenaltyAmount(baseAmount, i));
            penalty.setIsPaid(isPaid);
            penalty.setOutstandingAmount(isPaid ? 0.0 : penalty.getAmount());
            penalty.setTransactionType(getDefaultIfNull(penaltySpec.getTransactionType(), "1"));
            penalty.setTransactionSubType(getDefaultIfBlank(
                    penaltySpec.getTransactionSubType(), "NH"));
            penalty.setTypeDescription(getDefaultIfBlank(
                    penaltySpec.getTypeDescription(), "Penalty"));
            penalty.setDueDate(getFormattedDate(0, 6));
            penalty.setAccountStatus(getDefaultIfBlank(penaltySpec.getAccountStatus(), "CHS"));
            penalty.setDunningStatus(getDefaultIfBlank(penaltySpec.getDunningStatus(), "PEN3"));

            penalties.add(penalty);
        }

        return penalties;
    }

    private AccountPenalty createPenaltyUpdate(AccountPenalty accountPenalty, Boolean isPaid,
                                               Double amount, Double outStandingAmount) {
        var updatedPenalty = new AccountPenalty();
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
        var accountPenaltiesData = new AccountPenaltiesData();
        accountPenaltiesData.setId(accountPenalties.getId());
        accountPenaltiesData.setCompanyCode(accountPenalties.getCompanyCode());
        accountPenaltiesData.setCustomerCode(accountPenalties.getCustomerCode());
        accountPenaltiesData.setCreatedAt(accountPenalties.getCreatedAt());
        accountPenaltiesData.setClosedAt(accountPenalties.getClosedAt());

        List<PenaltyData> penalties = accountPenalties.getPenalties().stream()
                .map(this::mapToAccountPenaltyData)
                .collect(Collectors.toList());

        accountPenaltiesData.setPenalties(penalties);

        return accountPenaltiesData;
    }

    private PenaltyData mapToAccountPenaltyData(AccountPenalty penalty) {
        var penaltyData = new PenaltyData();
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

    private String generateTransactionReference() {
        var secureRandom = new SecureRandom();
        return "A" + String.format("%07d", secureRandom.nextInt(10000000));
    }

    private String getFormattedDate(int yearsAgo) {
        return LocalDate.now().minusYears(yearsAgo).format(DateTimeFormatter.ISO_DATE);
    }

    private String getFormattedDate(int years, int months) {
        return LocalDate.now().minusYears(years).minusMonths(months)
                .format(DateTimeFormatter.ISO_DATE);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private double calculatePenaltyAmount(double baseAmount, int penaltyIndex) {
        if (isFirstPenalty(penaltyIndex)) {
            return baseAmount;
        }
        return calculateMultipliedAmount(baseAmount);
    }

    private boolean isFirstPenalty(int penaltyIndex) {
        return penaltyIndex == 0;
    }

    private double calculateMultipliedAmount(double baseAmount) {
        double secureMultiplier = generateSecureMultiplier();
        return roundToTwoDecimals(baseAmount * secureMultiplier);
    }

    private double generateSecureMultiplier() {
        return 0.5 + 0.5 * SECURE_RANDOM.nextInt(5);
    }

    private String getDefaultIfBlank(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }

    private String getDefaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}
