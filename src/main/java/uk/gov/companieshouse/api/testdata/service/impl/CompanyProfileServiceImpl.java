package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
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
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final String LINK_STEM = "/company/";
    private static final String FILLING_HISTORY_STEM = "/filing-history";
    private static final String OFFICERS_STEM = "/officers";
    private static final String PSC_STATEMENT_STEM = "/persons-with-significant-control-statement";
    private static final String REGISTERS_STEM = "/registers";
    private static final String COMPANY_STATUS_REGISTERED = "registered";
    private static final String OVERSEAS_ENTITY_TYPE = "registered-overseas-entity";
    private static final String OVERSEA_COMPANY_TYPE = "oversea-company";
    private static final String EXT_REGISTRATION_NUMBER = "124234R34";
    private static final String FCD_COUNTRY = "Barbados";
    private static final String LEGAL_FORM = "Plc";
    private static final String BUSINESS_ACTIVITY = "Trading";
    private static final String ORIGINATING_REGISTRY_NAME = "Barbados Financial Services";
    private static final String UPDATED_TYPE = "psc_delta";
    public static final String FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY
            = "full-data-available-from-financial-conduct-authority";
    public static final String FULL_DATA_AVAILABLE_FROM_THE_COMPANY
            = "full-data-available-from-the-company";
    public static final String FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY
            = "full-data-available-from-department-of-the-economy";
    public static final String FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER
            = "full-data-available-from-financial-conduct-authority-mutuals-public-register";
    private static final String FOREIGN_ACCOUNT_TYPE = "ForeignAccountType1";
    private static final String TERMS_OF_PUBLICATION = "Terms of Account Publication";
    private static final String GOVERNED_BY = "Federal Government";

    @Autowired
    private RandomService randomService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CompanyProfileRepository repository;

    private boolean hasCompanyRegisters = false;

    @Override
    public CompanyProfile create(CompanySpec spec) {
        final String companyNumber = spec.getCompanyNumber();
        final Jurisdiction jurisdiction = spec.getJurisdiction();
        final String companyStatus = spec.getCompanyStatus();
        final CompanyType companyType = spec.getCompanyType();
        final String subType = spec.getSubType();
        final Boolean hasSuperSecurePscs = spec.getHasSuperSecurePscs();
        final String companyStatusDetail = spec.getCompanyStatusDetail();
        final String accountsDueStatus = spec.getAccountsDueStatus();

        LocalDate accountingReferenceDate = LocalDate.now();
        if (StringUtils.hasText(accountsDueStatus)) {
            accountingReferenceDate = randomService.generateAccountsDueDateByStatus(
                    accountsDueStatus);
        }
        Instant dateOneYearAgo = accountingReferenceDate.minusYears(1L)
                .atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateNow = accountingReferenceDate.atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateInOneYear = accountingReferenceDate.plusYears(1L)
                .atStartOfDay(ZONE_ID_UTC).toInstant();
        var dateInOneYearTwoWeeks
                = accountingReferenceDate.plusYears(1L).plusDays(14L)
                .atStartOfDay(ZONE_ID_UTC).toInstant();
        var dateInOneYearNineMonths
                = accountingReferenceDate.plusYears(1L)
                .plusMonths(9L).atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateInTwoYear = accountingReferenceDate
                .plusYears(2L).atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateInTwoYearTwoWeeks = accountingReferenceDate
                .plusYears(2L).plusDays(14L).atStartOfDay(ZONE_ID_UTC).toInstant();

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            return createRegisteredOverseasEntity(companyNumber, jurisdiction,
                    spec, dateOneYearAgo, dateNow, dateInOneYear, dateInOneYearTwoWeeks,
                    dateInOneYearNineMonths);
        } else if (CompanyType.OVERSEA_COMPANY.equals(companyType)) {
            return createOverseaCompany(companyNumber, jurisdiction, spec,
                    dateOneYearAgo, dateNow, dateInOneYear, dateInOneYearTwoWeeks,
                    dateInOneYearNineMonths);
        } else {
            return createNormalCompanyProfile(
                    companyNumber, jurisdiction, spec, dateOneYearAgo, dateNow,
                    dateInOneYear, dateInOneYearTwoWeeks, dateInOneYearNineMonths,
                    dateInTwoYear, dateInTwoYearTwoWeeks,
                    accountingReferenceDate, companyType, hasSuperSecurePscs,
                    accountsDueStatus, companyStatus, subType, companyStatusDetail
            );
        }
    }

    private CompanyProfile createNormalCompanyProfile(
            String companyNumber, Jurisdiction jurisdiction, CompanySpec spec,
            Instant dateOneYearAgo, Instant dateNow, Instant dateInOneYear,
            Instant dateInOneYearTwoWeeks, Instant dateInOneYearNineMonths,
            Instant dateInTwoYear, Instant dateInTwoYearTwoWeeks,
            LocalDate accountingReferenceDate, CompanyType companyType,
            Boolean hasSuperSecurePscs, String accountsDueStatus,
            String companyStatus, String subType, String companyStatusDetail
    ) {
        LOG.info("Creating a normal CompanyProfile. " + companyNumber);

        CompanyProfile profile = new CompanyProfile();
        profile.setId(companyNumber);
        String companyTypeValue = companyType != null ? companyType.getValue() : "ltd";
        checkAndSetCompanyRegisters(spec);
        profile.setCompanyNumber(companyNumber);
        String nonJurisdictionType = (jurisdiction != null)
                ? checkNonJurisdictionTypes(jurisdiction, companyTypeValue) : "";
        profile.setLinks(nonJurisdictionType.isEmpty()
                ? createLinkForSelf(companyNumber) : createLinks(companyNumber));

        CompanyProfile.Accounts accounts = profile.getAccounts();
        accounts.setNextDue(dateInOneYearNineMonths);
        accounts.setPeriodStart(dateNow);
        accounts.setPeriodEnd(dateInOneYear);
        accounts.setNextAccountsDueOn(dateInOneYearNineMonths);
        accounts.setNextAccountsOverdue(false);
        accounts.setNextMadeUpTo(dateInOneYear);
        accounts.setAccountingReferenceDateDay(
                String.valueOf(accountingReferenceDate.getDayOfMonth()));
        accounts.setAccountingReferenceDateMonth(
                String.valueOf(accountingReferenceDate.getMonthValue()));

        profile.setDateOfCreation(dateOneYearAgo);
        profile.setType(companyTypeValue);
        profile.setUndeliverableRegisteredOfficeAddress(false);

        if (hasSuperSecurePscs != null) {
            profile.setHasSuperSecurePscs(hasSuperSecurePscs);
        }
        setCompanyName(profile, companyNumber, companyTypeValue);
        profile.setSicCodes(Collections.singletonList("71200"));

        var confirmationStatement = profile.getConfirmationStatement();
        if ("due-soon".equalsIgnoreCase(accountsDueStatus)) {
            confirmationStatement.setLastMadeUpTo(dateInOneYear);
            confirmationStatement.setNextMadeUpTo(dateInTwoYear);
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateInTwoYearTwoWeeks);
        } else {
            confirmationStatement.setNextMadeUpTo(dateInOneYear);
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateInOneYearTwoWeeks);
            profile.setRegisteredOfficeIsInDispute(false);
        }
        confirmationStatement.setNextMadeUpTo(dateInOneYear);
        confirmationStatement.setOverdue(false);
        confirmationStatement.setNextDue(dateInOneYearTwoWeeks);

        profile.setRegisteredOfficeIsInDispute(false);
        setCompanyStatus(profile, companyStatus, companyTypeValue);
        profile.setHasInsolvencyHistory(
                "dissolved".equals(Objects.requireNonNullElse(companyStatus, "")));
        profile.setEtag(this.randomService.getEtag());
        setJurisdictionAndAddress(profile, jurisdiction, nonJurisdictionType);
        profile.setHasCharges(false);
        profile.setCanFile(true);
        setPartialDataOptions(profile, jurisdiction, companyType);
        setSubType(profile, subType);
        setCompanyStatusDetail(profile, companyStatusDetail, companyTypeValue);

        return repository.save(profile);
    }

    private OverseasEntity createRegisteredOverseasEntity(
            String companyNumber, Jurisdiction jurisdiction, CompanySpec spec,
            Instant dateOneYearAgo, Instant dateNow, Instant dateInOneYear,
            Instant dateInOneYearTwoWeeks, Instant dateInOneYearNineMonths) {
        LOG.info("Creating registered-overseas-entity for " + companyNumber);

        OverseasEntity overseasEntity = new OverseasEntity();
        overseasEntity.setId(companyNumber);
        overseasEntity.setCompanyNumber(companyNumber);
        overseasEntity.setHasMortgages(true);
        overseasEntity.setTestData(true);
        overseasEntity.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        overseasEntity.setType(spec.getCompanyType()
                != null ? spec.getCompanyType().getValue() : OVERSEAS_ENTITY_TYPE);
        if (OVERSEAS_ENTITY_TYPE.equals(overseasEntity.getType())) {
            overseasEntity.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        } else {
            overseasEntity.setCompanyStatus(
                    Objects.requireNonNullElse(spec.getCompanyStatus(), "active"));
        }
        overseasEntity.setHasSuperSecurePscs(spec.getHasSuperSecurePscs());
        overseasEntity.setHasCharges(false);
        overseasEntity.setHasInsolvencyHistory(false);
        overseasEntity.setJurisdiction(jurisdiction.toString());

        overseasEntity.setDateOfCreation(dateOneYearAgo);
        overseasEntity.setDeltaAt(Instant.now());

        // Confirmation Statement
        overseasEntity.getConfirmationStatement().setNextMadeUpTo(dateInOneYear);
        overseasEntity.getConfirmationStatement().setNextDue(dateInOneYearTwoWeeks);
        overseasEntity.getConfirmationStatement().setOverdue(false);

        // Company Details
        overseasEntity.setUndeliverableRegisteredOfficeAddress(false);
        overseasEntity.setCompanyName("COMPANY " + companyNumber + " LIMITED");
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
        foreignCompanyDetails.setACreditFinancialInstitution(true);
        foreignCompanyDetails.setBusinessActivity(BUSINESS_ACTIVITY);
        foreignCompanyDetails.setRegistrationNumber(EXT_REGISTRATION_NUMBER);

        // Originating Registry
        OverseasEntity.IOriginatingRegistry originatingRegistry =
                OverseasEntity.createOriginatingRegistry();
        originatingRegistry.setCountry(FCD_COUNTRY);
        originatingRegistry.setName(ORIGINATING_REGISTRY_NAME);
        foreignCompanyDetails.setOriginatingRegistry(originatingRegistry);

        // Accounting Requirement
        OverseasEntity.IAccountingRequirement accountingRequirement =
                OverseasEntity.createAccountingRequirement();
        accountingRequirement.setForeignAccountType(FOREIGN_ACCOUNT_TYPE);
        accountingRequirement.setTermsOfAccountPublication(TERMS_OF_PUBLICATION);
        foreignCompanyDetails.setAccountingRequirement(accountingRequirement);

        // Accounts Details
        OverseasEntity.IAccountsDetails accountsDetails = OverseasEntity.createAccountsDetails();
        accountsDetails.setAccountPeriodFrom("1", "January");
        accountsDetails.setAccountPeriodTo("31", "December");
        accountsDetails.setMustFileWithin("12");
        foreignCompanyDetails.setAccounts(accountsDetails);

        overseasEntity.setForeignCompanyDetails(foreignCompanyDetails);

        // Links
        Links links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
        links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
        links.setPersonsWithSignificantControlStatement(
                LINK_STEM + companyNumber + PSC_STATEMENT_STEM);
        overseasEntity.setLinks(links);

        // Accounts
        OverseasEntity.IAccounts accounts = OverseasEntity.createAccounts();
        accounts.setOverdue(false);
        accounts.setNextMadeUpTo(dateInOneYear);
        accounts.setNextDue(dateInOneYearTwoWeeks);

        // Accounting Reference Date
        OverseasEntity.AccountingReferenceDate accountingReferenceDate =
                new OverseasEntity.AccountingReferenceDate();
        accountingReferenceDate.setDay("9");
        accountingReferenceDate.setMonth("9");
        accounts.setAccountingReferenceDate(accountingReferenceDate);

        // Next Accounts
        OverseasEntity.NextAccounts nextAccounts = new OverseasEntity.NextAccounts();
        nextAccounts.setOverdue(false);
        nextAccounts.setDueOn(dateInOneYearTwoWeeks);
        nextAccounts.setPeriodStartOn(dateNow);
        nextAccounts.setPeriodEndOn(dateInOneYear);
        accounts.setNextAccounts(nextAccounts);

        // Last Accounts
        OverseasEntity.LastAccounts lastAccounts = new OverseasEntity.LastAccounts();
        lastAccounts.setType("aa");
        lastAccounts.setPeriodStartOn(dateOneYearAgo);
        lastAccounts.setPeriodEndOn(dateNow);
        lastAccounts.setMadeUpTo(dateInOneYear);
        accounts.setLastAccounts(lastAccounts);

        overseasEntity.setAccounts(accounts);

        // Updated
        OverseasEntity.IUpdated updated = OverseasEntity.createUpdated();
        updated.setAt(Instant.now());
        updated.setBy(randomService.getString(16));
        updated.setType(UPDATED_TYPE);
        overseasEntity.setUpdated(updated);

        repository.save(overseasEntity);
        LOG.info(
                "Returning a CompanyProfile view for registered-overseas-entity. " + companyNumber);
        return overseasEntity;
    }

    private OverseasEntity createOverseaCompany(
            String companyNumber, Jurisdiction jurisdiction, CompanySpec spec,
            Instant dateOneYearAgo, Instant dateNow, Instant dateInOneYear,
            Instant dateInOneYearTwoWeeks, Instant dateInOneYearNineMonths) {
        LOG.info("Creating oversea-company for " + companyNumber);

        OverseasEntity overseaCompany = new OverseasEntity();
        overseaCompany.setId(companyNumber);
        overseaCompany.setCompanyNumber(companyNumber);
        overseaCompany.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        overseaCompany.setType(OVERSEA_COMPANY_TYPE);
        overseaCompany.setHasSuperSecurePscs(spec.getHasSuperSecurePscs());
        overseaCompany.setHasCharges(false);
        overseaCompany.setHasInsolvencyHistory(true);
        overseaCompany.setJurisdiction(jurisdiction.toString());

        overseaCompany.setDateOfCreation(dateOneYearAgo);
        overseaCompany.setDeltaAt(Instant.now());

        // Confirmation Statement
        overseaCompany.getConfirmationStatement().setNextMadeUpTo(dateInOneYear);
        overseaCompany.getConfirmationStatement().setNextDue(dateInOneYearTwoWeeks);
        overseaCompany.getConfirmationStatement().setOverdue(false);

        // Company Details
        overseaCompany.setExternalRegistrationNumber(EXT_REGISTRATION_NUMBER);
        overseaCompany.setUndeliverableRegisteredOfficeAddress(false);
        overseaCompany.setCompanyName("COMPANY" + companyNumber + "Ltd");
        overseaCompany.setRegisteredOfficeIsInDispute(false);
        overseaCompany.setEtag(randomService.getEtag());
        overseaCompany.setSuperSecureManagingOfficerCount(0);

        // Addresses
        overseaCompany.setRegisteredOfficeAddress(addressService.getOverseasAddress());
        overseaCompany.setServiceAddress(addressService.getOverseasAddress());

        // Foreign Company Details
        OverseasEntity.IForeignCompanyDetails foreignCompanyDetails =
                OverseasEntity.createForeignCompanyDetails();
        foreignCompanyDetails.setGovernedBy(GOVERNED_BY);
        foreignCompanyDetails.setLegalForm(LEGAL_FORM);
        foreignCompanyDetails.setACreditFinancialInstitution(false);
        foreignCompanyDetails.setBusinessActivity(BUSINESS_ACTIVITY);
        foreignCompanyDetails.setRegistrationNumber(EXT_REGISTRATION_NUMBER);

        // Originating Registry
        OverseasEntity.IOriginatingRegistry originatingRegistry =
                OverseasEntity.createOriginatingRegistry();
        originatingRegistry.setCountry(FCD_COUNTRY);
        originatingRegistry.setName(ORIGINATING_REGISTRY_NAME);
        foreignCompanyDetails.setOriginatingRegistry(originatingRegistry);

        // Accounting Requirement
        OverseasEntity.IAccountingRequirement accountingRequirement =
                OverseasEntity.createAccountingRequirement();
        accountingRequirement.setForeignAccountType(FOREIGN_ACCOUNT_TYPE);
        accountingRequirement.setTermsOfAccountPublication(TERMS_OF_PUBLICATION);
        foreignCompanyDetails.setAccountingRequirement(accountingRequirement);

        overseaCompany.setForeignCompanyDetails(foreignCompanyDetails);

        // Links
        Links links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        overseaCompany.setLinks(links);

        // Accounts
        OverseasEntity.IAccounts accounts = OverseasEntity.createAccounts();
        accounts.setOverdue(false);
        accounts.setNextMadeUpTo(dateInOneYear);
        accounts.setNextDue(dateInOneYearTwoWeeks);

        // Accounting Reference Date
        OverseasEntity.AccountingReferenceDate accountingReferenceDate =
                new OverseasEntity.AccountingReferenceDate();
        accountingReferenceDate.setDay("31");
        accountingReferenceDate.setMonth("12");
        accounts.setAccountingReferenceDate(accountingReferenceDate);

        // Next Accounts
        OverseasEntity.NextAccounts nextAccounts = new OverseasEntity.NextAccounts();
        nextAccounts.setOverdue(false);
        nextAccounts.setPeriodEndOn(dateInOneYear);
        accounts.setNextAccounts(nextAccounts);

        // Last Accounts
        OverseasEntity.LastAccounts lastAccounts = new OverseasEntity.LastAccounts();
        lastAccounts.setType("null");
        lastAccounts.setPeriodEndOn(dateOneYearAgo);
        lastAccounts.setMadeUpTo(dateOneYearAgo);
        accounts.setLastAccounts(lastAccounts);

        overseaCompany.setAccounts(accounts);

        // Updated
        OverseasEntity.IUpdated updated = OverseasEntity.createUpdated();
        updated.setAt(Instant.now());
        updated.setBy(randomService.getString(16));
        updated.setType(UPDATED_TYPE);
        overseaCompany.setUpdated(updated);

        repository.save(overseaCompany);
        LOG.info("Returning a CompanyProfile view for oversea-company. " + companyNumber);
        return overseaCompany;
    }

    private static Map<CompanyType, String>
            createPartialDataOptionsMap(Jurisdiction companyJurisdiction) {
        Map<CompanyType, String> partialDataOptions = new HashMap<>();
        partialDataOptions.put(CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ASSURANCE_COMPANY,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ROYAL_CHARTER, FULL_DATA_AVAILABLE_FROM_THE_COMPANY);
        partialDataOptions.put(CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL, FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
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
        links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
        links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
        links.setPersonsWithSignificantControlStatement(LINK_STEM + companyNumber
                + PSC_STATEMENT_STEM);
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
        return (companyType != null && noJurisdictionTypes.contains(companyType)) ? "" : jurisdiction.toString();
    }

    private void setPartialDataOptions(CompanyProfile profile, Jurisdiction jurisdiction, CompanyType companyType) {
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

    private void setCompanyStatusDetail(CompanyProfile profile, String companyStatusDetail, String companyType) {
        if (companyType.equals(CompanyType.UKEIG.getValue())) {
            profile.setCompanyStatusDetail("converted-to-ukeig");
        } else if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            profile.setCompanyStatusDetail("converted-to-uk-societas");
        } else if (!Objects.isNull(companyStatusDetail)) {
            profile.setCompanyStatusDetail(companyStatusDetail);
        }
    }

    private void setJurisdictionAndAddress(CompanyProfile profile, Jurisdiction jurisdiction, String nonJurisdictionType) {
        if (jurisdiction != null && !nonJurisdictionType.isEmpty()) {
            profile.setJurisdiction(jurisdiction.toString());
            profile.setRegisteredOfficeAddress(addressService.getAddress(jurisdiction));
        }
    }

    private void checkAndSetCompanyRegisters(CompanySpec spec) {
        if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
            hasCompanyRegisters = true;
        }
    }

    private void setCompanyStatus(CompanyProfile profile, String companyStatus, String companyType) {
        if (CompanyType.NORTHERN_IRELAND.getValue().equals(companyType) || CompanyType.NORTHERN_IRELAND_OTHER.getValue().equals(companyType)) {
            profile.setCompanyStatus("converted-closed");
        } else if (CompanyType.REGISTERED_OVERSEAS_ENTITY.getValue().equals(companyType)) {
            profile.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        } else {
            profile.setCompanyStatus(Objects.requireNonNullElse(companyStatus, "active"));
        }
    }

    private void setCompanyName(CompanyProfile profile, String companyNumber, String companyType) {
        if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            profile.setCompanyName("COMPANY " + companyNumber + " UK SOCIETAS");
        } else {
            profile.setCompanyName("COMPANY " + companyNumber + " LIMITED");
        }
    }

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

    private void setPartialDataOptions(
            CompanyProfile profile, Jurisdiction jurisdiction, CompanyType companyType) {
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

    private void setCompanyStatusDetail(CompanyProfile profile, String companyStatusDetail) {
        if (!Objects.isNull(companyStatusDetail)) {
            profile.setCompanyStatusDetail(companyStatusDetail);
        }
    }

    private void setJurisdictionAndAddress(
            CompanyProfile profile, Jurisdiction jurisdiction, String nonJurisdictionType) {
        if (jurisdiction != null && !nonJurisdictionType.isEmpty()) {
            profile.setJurisdiction(jurisdiction.toString());
            profile.setRegisteredOfficeAddress(addressService.getAddress(jurisdiction));
        }
    }

    private void checkAndSetCompanyRegisters(CompanySpec spec) {
        if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
            hasCompanyRegisters = true;
        }
    }
}