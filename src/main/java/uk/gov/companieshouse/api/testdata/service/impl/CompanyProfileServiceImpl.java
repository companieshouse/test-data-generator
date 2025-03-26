package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.dto.AccountParameters;
import uk.gov.companieshouse.api.testdata.model.dto.CompanyDetailsParameters;
import uk.gov.companieshouse.api.testdata.model.dto.DateParameters;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OverseasEntity;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanyProfileServiceImpl.class));
    private static final String LINK_STEM = "/company/";
    private static final String FILLING_HISTORY_STEM = "/filing-history";
    private static final String OFFICERS_STEM = "/officers";
    private static final String PSC_STATEMENT_STEM = "/persons-with-significant-control-statement";
    private static final String REGISTERS_STEM = "/registers";
    private static final String COMPANY_STATUS_REGISTERED = "registered";
    private static final String OVERSEAS_ENTITY_TYPE = "registered-overseas-entity";
    private static final String OVERSEA_COMPANY_TYPE = "oversea-company";
    private static final String EXT_REGISTRATION_NUMBER = "124234R34";
    private static final String COMPANY_NAME_PREFIX = "COMPANY ";
    private static final String COMPANY_NAME_SUFFIX = " LIMITED";
    private static final String FCD_COUNTRY = "Barbados";
    private static final String LEGAL_FORM = "Plc";
    private static final String BUSINESS_ACTIVITY = "Trading";
    private static final String ORIGINATING_REGISTRY_NAME = "Barbados Financial Services";
    private static final String UPDATED_TYPE = "psc_delta";
    private static final String FOREIGN_ACCOUNT_TYPE = "ForeignAccountType";
    private static final String TERMS_OF_PUBLICATION = "Terms of Account Publication";
    private static final String GOVERNED_BY = "Federal Government";
    public static final String
            FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY =
            "full-data-available-from-financial-conduct-authority";
    public static final String
            FULL_DATA_AVAILABLE_FROM_THE_COMPANY = "full-data-available-from-the-company";
    public static final String
            FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY =
            "full-data-available-from-department-of-the-economy";
    public static final String
            FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER =
            "full-data-available-from-financial-conduct-authority-mutuals-public-register";

    @Autowired
    private RandomService randomService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CompanyProfileRepository repository;

    private boolean hasCompanyRegisters = false;

    private boolean isCompanyTypeHasNoFilingHistory = true;

    @Override
    public CompanyProfile create(CompanySpec spec) {
        final String companyNumber = spec.getCompanyNumber();
        final Jurisdiction jurisdiction = spec.getJurisdiction();
        final CompanyType companyType = spec.getCompanyType();
        final String subType = spec.getSubType();
        final Boolean hasSuperSecurePscs = spec.getHasSuperSecurePscs();
        final String companyStatusDetail = spec.getCompanyStatusDetail();
        final String companyStatus = spec.getCompanyStatus();
        final String accountsDueStatus = spec.getAccountsDueStatus();

        var accountParams = new AccountParameters(accountsDueStatus, randomService);
        var dateParams = new DateParameters(accountParams.getAccountingReferenceDate());
        var companyParams = new CompanyDetailsParameters(
                companyType, hasSuperSecurePscs, companyStatus, subType, companyStatusDetail);

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            return createOverseasEntity(companyNumber, jurisdiction, spec, dateParams,
                    OVERSEAS_ENTITY_TYPE, companyType);
        } else if (CompanyType.OVERSEA_COMPANY.equals(companyType)) {
            return createOverseasEntity(companyNumber, jurisdiction, spec, dateParams,
                    OVERSEA_COMPANY_TYPE, companyType);
        } else {
            return createNormalCompanyProfile(companyNumber, jurisdiction,
                    spec, dateParams, companyParams, accountParams);
        }
    }

    private CompanyProfile createNormalCompanyProfile(String companyNumber,
                                                      Jurisdiction jurisdiction,
                                                      CompanySpec spec,
                                                      DateParameters dateParams,
                                                      CompanyDetailsParameters companyParams,
                                                      AccountParameters accountParams) {
        LOG.info("Creating a normal CompanyProfile. " + companyNumber);

        CompanyProfile profile = new CompanyProfile();
        profile.setId(companyNumber);
        isCompanyTypeHasNoFilingHistory = hasNoFilingHistory(companyParams.getCompanyType());
        String companyTypeValue = companyParams.getCompanyType()
                != null ? companyParams.getCompanyType().getValue() : "ltd";
        setCompanyHasRegisters(spec);
        profile.setCompanyNumber(companyNumber);
        String nonJurisdictionType = (jurisdiction != null)
                ? checkNonJurisdictionTypes(jurisdiction, companyTypeValue) : "";
        profile.setLinks(nonJurisdictionType.isEmpty()
                ? createLinkForSelf(companyNumber) : createLinks(companyNumber));

        var accounts = profile.getAccounts();
        accounts.setNextDue(dateParams.getDateInOneYearNineMonths());
        accounts.setPeriodStart(dateParams.getDateNow());
        accounts.setPeriodEnd(dateParams.getDateInOneYear());
        accounts.setNextAccountsDueOn(dateParams.getDateInOneYearNineMonths());
        accounts.setNextAccountsOverdue(false);
        accounts.setNextMadeUpTo(dateParams.getDateInOneYear());
        accounts.setAccountingReferenceDateDay(
                String.valueOf(dateParams.getAccountingReferenceDate().getDayOfMonth()));
        accounts.setAccountingReferenceDateMonth(
                String.valueOf(dateParams.getAccountingReferenceDate().getMonthValue()));

        profile.setDateOfCreation(dateParams.getDateOneYearAgo());
        profile.setType(companyTypeValue);
        profile.setUndeliverableRegisteredOfficeAddress(false);

        Boolean hasSuperSecurePscs = companyParams.getHasSuperSecurePscs();
        profile.setHasSuperSecurePscs(hasSuperSecurePscs != null ? hasSuperSecurePscs : false);
        setCompanyName(profile, companyNumber, companyTypeValue);
        profile.setSicCodes(Collections.singletonList("71200"));

        var confirmationStatement = profile.getConfirmationStatement();
        if ("due-soon".equalsIgnoreCase(accountParams.getAccountsDueStatus())) {
            confirmationStatement.setLastMadeUpTo(dateParams.getDateInOneYear());
            confirmationStatement.setNextMadeUpTo(dateParams.getDateInTwoYear());
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateParams.getDateInTwoYearTwoWeeks());
        } else {
            confirmationStatement.setNextMadeUpTo(dateParams.getDateInOneYear());
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateParams.getDateInOneYearTwoWeeks());
            profile.setRegisteredOfficeIsInDispute(false);
        }
        confirmationStatement.setNextMadeUpTo(dateParams.getDateInOneYear());
        confirmationStatement.setOverdue(false);
        confirmationStatement.setNextDue(dateParams.getDateInOneYearTwoWeeks());

        profile.setRegisteredOfficeIsInDispute(false);
        setCompanyStatus(profile, companyParams.getCompanyStatus(), companyTypeValue);
        profile.setHasInsolvencyHistory(
                "dissolved".equals(Objects.requireNonNullElse(
                        companyParams.getCompanyStatus(), "")));
        profile.setEtag(this.randomService.getEtag());
        setJurisdictionAndAddress(profile, jurisdiction, nonJurisdictionType);
        profile.setHasCharges(false);
        profile.setCanFile(true);
        setPartialDataOptions(profile, jurisdiction, companyParams.getCompanyType());
        setSubType(profile, companyParams.getSubType());
        setCompanyStatusDetail(profile, companyParams.getCompanyStatusDetail(), companyTypeValue);

        return repository.save(profile);
    }

    private OverseasEntity createOverseasEntity(String companyNumber,
                                                Jurisdiction jurisdiction, CompanySpec spec,
                                                DateParameters dateParams,
                                                String entityType, CompanyType companyType) {
        LOG.info("Creating " + entityType + " for " + companyNumber);

        var overseasEntity = new OverseasEntity();
        overseasEntity.setId(companyNumber);
        overseasEntity.setCompanyNumber(companyNumber);
        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            overseasEntity.setHasMortgages(true);
            overseasEntity.setTestData(true);
        }
        setCompanyStatus(overseasEntity, spec.getCompanyStatus(), entityType);
        overseasEntity.setType(entityType);
        overseasEntity.setHasCharges(false);
        overseasEntity.setHasInsolvencyHistory(false);
        overseasEntity.setJurisdiction(jurisdiction.toString());

        overseasEntity.setDateOfCreation(dateParams.getDateOneYearAgo());

        // Confirmation Statement
        overseasEntity.getConfirmationStatement().setNextMadeUpTo(dateParams.getDateInOneYear());
        overseasEntity.getConfirmationStatement().setNextDue(dateParams.getDateInOneYearTwoWeeks());
        overseasEntity.getConfirmationStatement().setOverdue(false);

        // Company Details
        overseasEntity.setUndeliverableRegisteredOfficeAddress(false);
        overseasEntity.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + COMPANY_NAME_SUFFIX);
        overseasEntity.setRegisteredOfficeIsInDispute(false);
        overseasEntity.setEtag(randomService.getEtag());
        overseasEntity.setSuperSecureManagingOfficerCount(0);

        // Addresses
        overseasEntity.setRegisteredOfficeAddress(addressService.getOverseasAddress());
        overseasEntity.setServiceAddress(addressService.getOverseasAddress());

        // Foreign Company Details
        OverseasEntity.IForeignCompanyDetails foreignCompanyDetails =
                OverseasEntity.createForeignCompanyDetails();
        foreignCompanyDetails.setGovernedBy(GOVERNED_BY);
        foreignCompanyDetails.setLegalForm(LEGAL_FORM);
        foreignCompanyDetails.setCreditFinancialInstitution(true);
        foreignCompanyDetails.setBusinessActivity(BUSINESS_ACTIVITY);

        // Originating Registry
        OverseasEntity.IOriginatingRegistry originatingRegistry =
                OverseasEntity.createOriginatingRegistry();
        originatingRegistry.setCountry(FCD_COUNTRY);
        originatingRegistry.setName(ORIGINATING_REGISTRY_NAME);
        foreignCompanyDetails.setOriginatingRegistry(originatingRegistry);

        // Accounting Requirement
        OverseasEntity.IAccountingRequirement
                accountingRequirement = OverseasEntity.createAccountingRequirement();
        accountingRequirement.setForeignAccountType(FOREIGN_ACCOUNT_TYPE);
        accountingRequirement.setTermsOfAccountPublication(TERMS_OF_PUBLICATION);
        foreignCompanyDetails.setAccountingRequirement(accountingRequirement);

        // Accounts Details
        OverseasEntity.IAccountsDetails accountsDetails = OverseasEntity.createAccountsDetails();
        accountsDetails.setAccountPeriodFrom("1", "January");
        accountsDetails.setAccountPeriodTo("31", "December");


        OverseasEntity.IMustFileWithin mustFileWithin = OverseasEntity.createMustFileWithin();
        mustFileWithin.setMonths(12);
        accountsDetails.setMustFileWithin(mustFileWithin);

        foreignCompanyDetails.setAccounts(accountsDetails);

        overseasEntity.setForeignCompanyDetails(foreignCompanyDetails);

        // Links
        var links = new Links();
        links.setSelf(LINK_STEM + companyNumber);

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
            links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
            links.setPersonsWithSignificantControlStatement(
                    LINK_STEM + companyNumber + PSC_STATEMENT_STEM);
        }

        overseasEntity.setLinks(links);

        // Accounts
        OverseasEntity.IAccounts accounts = OverseasEntity.createAccounts();
        accounts.setOverdue(false);
        accounts.setNextMadeUpTo(dateParams.getDateInOneYear());
        accounts.setNextDue(dateParams.getDateInOneYearTwoWeeks());

        // Accounting Reference Date
        var accountingReferenceDate = new OverseasEntity.AccountingReferenceDate();
        accountingReferenceDate.setDay("9");
        accountingReferenceDate.setMonth("9");
        accounts.setAccountingReferenceDate(accountingReferenceDate);

        // Next Accounts
        var nextAccounts = new OverseasEntity.NextAccounts();
        nextAccounts.setOverdue(false);
        nextAccounts.setDueOn(dateParams.getDateInOneYearTwoWeeks());
        nextAccounts.setPeriodStartOn(dateParams.getDateNow());
        nextAccounts.setPeriodEndOn(dateParams.getDateInOneYear());
        accounts.setNextAccounts(nextAccounts);

        // Last Accounts
        var lastAccounts = new OverseasEntity.LastAccounts();
        lastAccounts.setType("aa");
        lastAccounts.setPeriodStartOn(dateParams.getDateOneYearAgo());
        lastAccounts.setPeriodEndOn(dateParams.getDateNow());
        lastAccounts.setMadeUpTo(dateParams.getDateInOneYear());
        accounts.setLastAccounts(lastAccounts);

        overseasEntity.setAccounts(accounts);

        if (CompanyType.OVERSEA_COMPANY.equals(companyType)) {
            Boolean hasSuperSecurePscs = spec.getHasSuperSecurePscs();
            overseasEntity.setHasSuperSecurePscs(hasSuperSecurePscs != null ? hasSuperSecurePscs : false);
            foreignCompanyDetails.setRegistrationNumber(EXT_REGISTRATION_NUMBER);
            overseasEntity.setDeltaAt(Instant.now());
            OverseasEntity.IUpdated updated = OverseasEntity.createUpdated();
            updated.setAt(Instant.now());
            updated.setBy(randomService.getString(16));
            updated.setType(UPDATED_TYPE);
            overseasEntity.setUpdated(updated);
        }

        repository.save(overseasEntity);
        LOG.info("Returning a CompanyProfile view for " + entityType + ". " + companyNumber);
        return overseasEntity;
    }

    private static Map<CompanyType, String> createPartialDataOptionsMap(
            Jurisdiction companyJurisdiction) {
        Map<CompanyType, String> partialDataOptions = new HashMap<>();
        partialDataOptions.put(CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ASSURANCE_COMPANY,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ROYAL_CHARTER, FULL_DATA_AVAILABLE_FROM_THE_COMPANY);
        partialDataOptions.put(CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
        if (Jurisdiction.NI.equals(companyJurisdiction)) {
            partialDataOptions.put(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY,
                    FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY);
        } else {
            partialDataOptions.put(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY,
                    FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
        }
        return partialDataOptions;
    }

    @Override
    public boolean delete(String companyId) {
        Optional<CompanyProfile> profile = repository.findByCompanyNumber(companyId);
        profile.ifPresent(repository::delete);
        return profile.isPresent();
    }

    @Override
    public boolean companyExists(String companyNumber) {
        return repository.findById(companyNumber).isPresent();
    }

    private Links createLinks(String companyNumber) {
        Links links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        if (!isCompanyTypeHasNoFilingHistory) {
            links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
        }
        links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
        links.setPersonsWithSignificantControlStatement(
                LINK_STEM + companyNumber + PSC_STATEMENT_STEM);
        if (hasCompanyRegisters) {
            links.setRegisters(LINK_STEM + companyNumber + REGISTERS_STEM);
        }
        return links;
    }

    private Links createLinkForSelf(String companyNumber) {
        var links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        return links;
    }

    private String checkNonJurisdictionTypes(Jurisdiction jurisdiction, String companyType) {
        Set<String> noJurisdictionTypes = Set.of(
                CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL.getValue(),
                CompanyType.ROYAL_CHARTER.getValue(),
                CompanyType.UK_ESTABLISHMENT.getValue()
        );
        if (jurisdiction == null) {
            return "";
        }
        return (companyType != null && noJurisdictionTypes.contains(companyType))
                ? "" : jurisdiction.toString();
    }

    private void setPartialDataOptions(CompanyProfile profile,
                                       Jurisdiction jurisdiction,
                                       CompanyType companyType) {
        Map<CompanyType, String> partialDataOptions = createPartialDataOptionsMap(jurisdiction);
        if (partialDataOptions.containsKey(companyType)) {
            profile.setPartialDataAvailable(partialDataOptions.get(companyType));
        }
    }

    private void setSubType(CompanyProfile profile, String subType) {
        if (subType != null) {
            profile.setIsCommunityInterestCompany(subType.equals("community-interest-company"));
            profile.setSubtype(subType);
        }
    }

    private void setCompanyStatusDetail(
            CompanyProfile profile, String companyStatusDetail, String companyType) {
        if (companyType.equals(CompanyType.UKEIG.getValue())) {
            profile.setCompanyStatusDetail("converted-to-ukeig");
        } else if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            profile.setCompanyStatusDetail("converted-to-uk-societas");
        } else if (!Objects.isNull(companyStatusDetail)) {
            profile.setCompanyStatusDetail(companyStatusDetail);
        }
    }

    private void setJurisdictionAndAddress(CompanyProfile profile,
                                           Jurisdiction jurisdiction,
                                           String nonJurisdictionType) {
        if (jurisdiction != null && !nonJurisdictionType.isEmpty()) {
            profile.setJurisdiction(jurisdiction.toString());
            profile.setRegisteredOfficeAddress(addressService.getAddress(jurisdiction));
        }
    }

    private void setCompanyHasRegisters(CompanySpec spec) {
        hasCompanyRegisters = spec.getRegisters() != null && !spec.getRegisters().isEmpty();
    }

    private void setCompanyStatus(
            CompanyProfile profile, String companyStatus, String companyType) {
        if (CompanyType.NORTHERN_IRELAND.getValue().equals(companyType)
                || CompanyType.NORTHERN_IRELAND_OTHER.getValue().equals(companyType)) {
            profile.setCompanyStatus("converted-closed");
        } else if (CompanyType.REGISTERED_OVERSEAS_ENTITY.getValue().equals(companyType)) {
            profile.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        } else {
            profile.setCompanyStatus(Objects.requireNonNullElse(companyStatus, "active"));
        }
    }

    private void setCompanyName(CompanyProfile profile, String companyNumber, String companyType) {
        if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            profile.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + " UK SOCIETAS");
        } else {
            profile.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + COMPANY_NAME_SUFFIX);
        }
    }

    private boolean hasNoFilingHistory(CompanyType companyType) {
        if (companyType == null) {
            return false;
        }

        Set<String> noFilingHistoryCompanyTypes = Set.of(
                CompanyType.ASSURANCE_COMPANY.getValue(),
                CompanyType.CHARITABLE_INCORPORATED_ORGANISATION.getValue(),
                CompanyType.ICVC_SECURITIES.getValue(),
                CompanyType.ICVC_UMBRELLA.getValue(),
                CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY.getValue(),
                CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL.getValue(),
                CompanyType.PROTECTED_CELL_COMPANY.getValue(),
                CompanyType.ROYAL_CHARTER.getValue(),
                CompanyType.SCOTTISH_CHARITABLE_INCORPORATED_ORGANISATION.getValue(),
                CompanyType.UK_ESTABLISHMENT.getValue()
        );

        return noFilingHistoryCompanyTypes.contains(companyType.getValue());
    }
}