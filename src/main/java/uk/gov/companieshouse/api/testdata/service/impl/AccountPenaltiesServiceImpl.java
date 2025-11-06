package uk.gov.companieshouse.api.testdata.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
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
import uk.gov.companieshouse.api.testdata.model.rest.PenaltiesTransactionSubType;
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
    private static final List<String> LP_LEDGER_CODES = List.of("EW", "SC", "NI");
    private static final List<String> LP_TYPE_DESCRIPTIONS = List.of("EOCFP", "EOJSD");
    private static final List<PenaltiesTransactionSubType> EXCLUDED_SUBTYPES = List.of(
            PenaltiesTransactionSubType.S1, PenaltiesTransactionSubType.A2, PenaltiesTransactionSubType.S3);
    private static final List<String> C1_LEDGER_CODES = List.of("E1", "S1", "N1");


    @Autowired
    private AccountPenaltiesRepository repository;

    private static final NoDataFoundException PENALTY_NOT_FOUND_EX =
            new NoDataFoundException("penalty not found");

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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
    public AccountPenaltiesData getAccountPenalties(String customerCode, String companyCode)
            throws NoDataFoundException {
        // CustomerCode and CompanyCode together form a composite unique key, so this would return a list of
        // a single AccountPenalties or an empty list
        var accountPenalties = repository.findByCustomerCodeAndCompanyCode(customerCode, companyCode)
                .orElseGet(Collections::emptyList)
                .stream().findFirst()
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

    public AccountPenaltiesData createAccountPenalties(PenaltySpec penaltySpec)
            throws DataException {

        List<AccountPenalty> penalties = createPenaltiesList(penaltySpec);

        if (penalties == null) {
            return null;
        }

        var accountPenalties = new AccountPenalties();
        accountPenalties.setId(new ObjectId());
        LOG.info("Creating account penalties with ID: " + accountPenalties.getId());

        accountPenalties.setCompanyCode(penaltySpec.getCompanyCode());
        accountPenalties.setCustomerCode(penaltySpec.getCustomerCode());
        accountPenalties.setCreatedAt(Instant.now());

        boolean isPaid = Optional.ofNullable(penaltySpec.getIsPaid()).orElse(false);
        accountPenalties.setClosedAt(isPaid ? Instant.now() : null);

        accountPenalties.setPenalties(createPenaltiesList(penaltySpec));

        try {
            return mapToAccountPenaltiesData(repository.save(accountPenalties));
        } catch (Exception ex) {
            throw new DataException("Failed to create account penalties", ex);
        }
    }

    List<AccountPenalty> createPenaltiesList(PenaltySpec penaltySpec) {
        if (Boolean.TRUE.equals(penaltySpec.isDuplicate())) {
            return createDuplicatePenalties(penaltySpec);
        }

        int numberOfPenalties = Optional.ofNullable(penaltySpec.getNumberOfPenalties()).orElse(1);
        boolean isPaid = Optional.ofNullable(penaltySpec.getIsPaid()).orElse(false);
        String companyCode = getDefaultIfBlank(penaltySpec.getCompanyCode(), "LP");
        String transactionSubType = getTransactionSubTypeValue(penaltySpec);

        List<AccountPenalty> penalties = new ArrayList<>();

        for (var i = 0; i < numberOfPenalties; i++) {
            AccountPenalty penalty = createPenalty(penaltySpec, companyCode, transactionSubType, SECURE_RANDOM, i, isPaid);
            penalties.add(penalty);
        }

        return penalties;
    }

    private String getTransactionSubTypeValue(PenaltySpec penaltySpec) {
        return penaltySpec.getTransactionSubType() != null
                ? penaltySpec.getTransactionSubType().getValue()
                : null;
    }

    private AccountPenalty createPenalty(PenaltySpec penaltySpec, String companyCode,
                                         String transactionSubType, Random random,
                                         int index, boolean isPaid) {
        var penalty = new AccountPenalty();
        penalty.setCompanyCode(companyCode);
        penalty.setCustomerCode(penaltySpec.getCustomerCode());

        configurePenaltyBasedOnCompanyAndSubType(penalty, companyCode, transactionSubType, random, penaltySpec);
        configurePenaltyAmount(penalty, penaltySpec, index);
        configurePenaltyDatesAndStatus(penalty, isPaid, penaltySpec);

        return penalty;
    }

    private List<AccountPenalty> createDuplicatePenalties(PenaltySpec penaltySpec) {
        int numberOfPenalties = Optional.ofNullable(penaltySpec.getNumberOfPenalties()).orElse(1);

        if (numberOfPenalties < 2) {
            return new ArrayList<>();
        }

        List<AccountPenalty> penalties = new ArrayList<>();
        String companyCode = getDefaultIfBlank(penaltySpec.getCompanyCode(), "LP");
        String transactionSubType = getTransactionSubTypeValue(penaltySpec);
        boolean isPaid = Optional.ofNullable(penaltySpec.getIsPaid()).orElse(false);

        // Create base penalty
        AccountPenalty basePenalty = createPenalty(penaltySpec, companyCode, transactionSubType, SECURE_RANDOM, 0, isPaid);

        // Generate one transaction reference for all duplicates
        String sharedTransactionRef = generateTransactionReference(companyCode, transactionSubType);

        for (var i = 0; i < numberOfPenalties; i++) {
            var penaltyCopy = new AccountPenalty();

            penaltyCopy.setCompanyCode(basePenalty.getCompanyCode());
            penaltyCopy.setCustomerCode(basePenalty.getCustomerCode());
            penaltyCopy.setLedgerCode(basePenalty.getLedgerCode());
            penaltyCopy.setTransactionType(basePenalty.getTransactionType());
            penaltyCopy.setTypeDescription(basePenalty.getTypeDescription());
            penaltyCopy.setTransactionSubType(basePenalty.getTransactionSubType());
            penaltyCopy.setAccountStatus(basePenalty.getAccountStatus());
            penaltyCopy.setDunningStatus(basePenalty.getDunningStatus());
            penaltyCopy.setIsPaid(isPaid);
            penaltyCopy.setTransactionReference(sharedTransactionRef);

            penaltyCopy.setAmount(generateRandomAmount(10, 99));
            penaltyCopy.setOutstandingAmount(isPaid ? 0.0 : penaltyCopy.getAmount());
            penaltyCopy.setTransactionDate(getFormattedDate(i));
            penaltyCopy.setMadeUpDate(getFormattedDate(i + 1));
            penaltyCopy.setDueDate(getFormattedDate(0, i + 1));

            penalties.add(penaltyCopy);
        }

        return penalties;
    }


    private void configurePenaltyBasedOnCompanyAndSubType(AccountPenalty penalty, String companyCode,
                                                          String transactionSubType, Random random,
                                                          PenaltySpec penaltySpec) {
        if ("LP".equals(companyCode)) {
            configureLPPenalty(penalty, transactionSubType, random, penaltySpec);
        } else if ("C1".equals(companyCode) && "S1".equals(transactionSubType)) {
            configureC1S1Penalty(penalty, random, penaltySpec);
        } else if ("C1".equals(companyCode) && "S3".equals(transactionSubType)) {
            configureC1S3Penalty(penalty, random, penaltySpec);
        } else if ("C1".equals(companyCode) && "A2".equals(transactionSubType)) {
            configureC1A2Penalty(penalty);
        } else {
            configureGenericPenalty(penalty, companyCode, transactionSubType, penaltySpec);
        }
    }

    private void configureLPPenalty(AccountPenalty penalty, String transactionSubType,
                                    Random random, PenaltySpec penaltySpec) {
        penalty.setTransactionType("1");

        String ledgerCode = getDefaultIfBlank(penaltySpec.getLedgerCode(),
                LP_LEDGER_CODES.get(random.nextInt(LP_LEDGER_CODES.size())));
        penalty.setLedgerCode(ledgerCode);

        String typeDescription = getDefaultIfBlank(penaltySpec.getTypeDescription(),
                LP_TYPE_DESCRIPTIONS.get(random.nextInt(LP_TYPE_DESCRIPTIONS.size())));
        penalty.setTypeDescription(typeDescription);

        String subType = getLPSubType(transactionSubType, random);
        penalty.setTransactionSubType(subType);
        penalty.setTransactionReference(generateTransactionReference("LP", subType));
    }

    private String getLPSubType(String transactionSubType, Random random) {
        if (transactionSubType == null || transactionSubType.isBlank()) {
            return Arrays.stream(PenaltiesTransactionSubType.values())
                    .filter(e -> !EXCLUDED_SUBTYPES.contains(e))
                    .skip(random.nextInt((int) Arrays.stream(PenaltiesTransactionSubType.values())
                            .filter(e -> !EXCLUDED_SUBTYPES.contains(e))
                            .count()))
                    .findFirst()
                    .map(PenaltiesTransactionSubType::getValue)
                    .orElseThrow();
        }
        return transactionSubType;
    }

    private void configureC1S1Penalty(AccountPenalty penalty, Random random, PenaltySpec penaltySpec) {
        penalty.setTransactionType("1");
        String ledgerCode = getDefaultIfBlank(penaltySpec.getLedgerCode(),
                C1_LEDGER_CODES.get(random.nextInt(C1_LEDGER_CODES.size())));
        penalty.setLedgerCode(ledgerCode);
        penalty.setTypeDescription("CS01");
        penalty.setTransactionSubType("S1");
        penalty.setTransactionReference(generateTransactionReference("C1", "S1"));
    }

    private void configureC1A2Penalty(AccountPenalty penalty) {
        penalty.setTransactionType("1");
        penalty.setLedgerCode("FU");
        penalty.setTypeDescription("PENU");
        penalty.setTransactionSubType("A2");
        penalty.setTransactionReference(generateTransactionReference("C1", "A2"));
    }

    private void configureC1S3Penalty(AccountPenalty penalty, Random random, PenaltySpec penaltySpec){
        penalty.setTransactionType("1");
        String ledgerCode = getDefaultIfBlank(penaltySpec.getLedgerCode(),
                C1_LEDGER_CODES.get(random.nextInt(C1_LEDGER_CODES.size())));
        penalty.setLedgerCode(ledgerCode);
        penalty.setTypeDescription("CS01 IDV");
        penalty.setTransactionSubType("S3");
        penalty.setTransactionReference(generateTransactionReference("C1", "S3"));
    }

    private void configureGenericPenalty(AccountPenalty penalty, String companyCode,
                                         String transactionSubType, PenaltySpec penaltySpec) {
        Optional<PenaltiesTransactionSubType> penaltyConfig =
                PenaltiesTransactionSubType.fromCompanyAndSubType(transactionSubType);

        if (penaltyConfig.isPresent()) {
            PenaltiesTransactionSubType config = penaltyConfig.get();
            penalty.setLedgerCode(config.getRandomLedgerCode());
            penalty.setTransactionType(config.getTransactionType());
            penalty.setTypeDescription(config.getRandomTypeDescription());
        } else {
            penalty.setLedgerCode(getDefaultIfBlank(penaltySpec.getLedgerCode(), "SC"));
            penalty.setTransactionType(getDefaultIfNull(penaltySpec.getTransactionType(), "1"));
            penalty.setTypeDescription(getDefaultIfBlank(penaltySpec.getTypeDescription(), "Penalty"));
        }
        penalty.setTransactionSubType(getDefaultIfBlank(transactionSubType, "NH"));
        penalty.setTransactionReference(generateTransactionReference(companyCode, transactionSubType));
    }

    private void configurePenaltyAmount(AccountPenalty penalty, PenaltySpec penaltySpec, int index) {
        double amount;
        if (penaltySpec.getAmount() != null) {
            amount = (index == 0) ? roundToTwoDecimals(penaltySpec.getAmount()) : generateRandomAmount(10, 99);
        } else {
            amount = generateRandomAmount(10, 99);
        }
        penalty.setAmount(amount);
    }

    private void configurePenaltyDatesAndStatus(AccountPenalty penalty, boolean isPaid, PenaltySpec penaltySpec) {
        penalty.setTransactionDate(getFormattedDate(1));
        penalty.setMadeUpDate(getFormattedDate(2));
        penalty.setIsPaid(isPaid);
        penalty.setOutstandingAmount(isPaid ? 0.0 : penalty.getAmount());
        penalty.setDueDate(getFormattedDate(0, 6));
        penalty.setAccountStatus(getDefaultIfBlank(penaltySpec.getAccountStatus(), "CHS"));
        penalty.setDunningStatus(getDefaultIfBlank(penaltySpec.getDunningStatus(), "PEN1"));
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

    private String generateTransactionReference(String companyCode, String transactionSubType) {
        var prefix = "A";
        if ("LP".equals(companyCode)) {
            prefix = "A";
        } else if ("C1".equals(companyCode) && ("S1".equals(transactionSubType) || "S3".equals(transactionSubType))) {
            prefix = "P";
        } else if ("C1".equals(companyCode) && "A2".equals(transactionSubType)) {
            prefix = "U";
        }
        return prefix + String.format("%07d", SECURE_RANDOM.nextInt(10000000));
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

    // Generate random numbers between min and max, both min and max inclusive
    private double generateRandomAmount(double min, double max) {
        double maxRand = max - min;
        return Math.round(min + (SECURE_RANDOM.nextDouble() * maxRand));
    }

    private String getDefaultIfBlank(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }

    private String getDefaultIfNull(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }
}